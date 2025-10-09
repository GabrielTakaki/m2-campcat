package com.example.campeonatocatarinense.data

import android.content.Context
import com.example.campeonatocatarinense.data.model.*
import com.example.campeonatocatarinense.data.xml.ChampionshipParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * React/Redux analogy:
 * - Este é o "reducer" + "store" de dados persistidos em memória.
 * - Sempre retorna NOVOS objetos (copy/map) — não muta listas existentes.
 *   (equivalente a usar [...state.items] em vez de .push()).
 */
class ChampionshipRepository(context: Context) {

    private val _champ = MutableStateFlow<Championship?>(ChampionshipParser.loadFromAssets(context))
    val champ: StateFlow<Championship?> = _champ.asStateFlow()

    fun reset(context: Context) {
        _champ.value = ChampionshipParser.loadFromAssets(context)
    }

    fun updateMatch(
        roundNum: Int,
        matchId: String,
        teamAId: Int?,
        teamBId: Int?,
        goalsA: Int,
        goalsB: Int,
        location: String?,
        date: String?
    ) {
        val current = _champ.value ?: return

        // Imutável: recria rounds/matches necessários
        val newRounds = current.rounds.map { round ->
            if (round.number != roundNum) round else {
                val newMatches = round.matches.map { m ->
                    if (m.id == matchId) {
                        m.copy(
                            teamA = teamAId ?: m.teamA,
                            teamB = teamBId ?: m.teamB,
                            goalsA = goalsA,
                            goalsB = goalsB,
                            location = location ?: m.location,
                            date = date ?: m.date
                        )
                    } else m
                }.toMutableList()
                round.copy(matches = newMatches)
            }
        }

        // Publica NOVO Championship → Flow emite → Compose recompõe
        _champ.value = current.copy(rounds = newRounds)
    }

    fun addMatch(roundNum: Int, match: Match) {
        val current = _champ.value ?: return
        val newRounds = current.rounds.map { r ->
            if (r.number != roundNum) r else r.copy(matches = (r.matches + match).toMutableList())
        }
        _champ.value = current.copy(rounds = newRounds)
    }

    fun allMatchesScored(): Boolean =
        _champ.value?.rounds?.all { r -> r.matches.all { it.goalsA != null && it.goalsB != null } } ?: false
}
