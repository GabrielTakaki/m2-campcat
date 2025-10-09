package com.example.campeonatocatarinense.domain

import com.example.campeonatocatarinense.data.model.Championship
import com.example.campeonatocatarinense.data.model.Team

data class StandingEntry(
    val team: Team,
    var points: Int = 0,
    var wins: Int = 0,
    var draws: Int = 0,
    var losses: Int = 0,
    var goalsFor: Int = 0,
    var goalsAgainst: Int = 0
) { val gd: Int get() = goalsFor - goalsAgainst }

fun computeStandings(champ: Championship): List<StandingEntry> {
    val byTeam = champ.teams.associateWith { StandingEntry(it) }
    champ.rounds.forEach { r ->
        r.matches.forEach { m ->
            val a = m.goalsA; val b = m.goalsB
            if (a == null || b == null) return@forEach
            val ta = champ.teams.first { it.id == m.teamA }
            val tb = champ.teams.first { it.id == m.teamB }
            val ea = byTeam[ta]!!; val eb = byTeam[tb]!!
            ea.goalsFor += a; ea.goalsAgainst += b
            eb.goalsFor += b; eb.goalsAgainst += a
            when {
                a > b -> { ea.wins++; ea.points += 3; eb.losses++ }
                b > a -> { eb.wins++; eb.points += 3; ea.losses++ }
                else -> { ea.draws++; eb.draws++; ea.points++; eb.points++ }
            }
        }
    }
    return byTeam.values.sortedWith(
        compareByDescending<StandingEntry> { it.points }
            .thenByDescending { it.wins }
            .thenByDescending { it.gd }
            .thenByDescending { it.goalsFor }
    )
}

data class StandingsBuckets(
    val libertadores: List<StandingEntry>,
    val preLibertadores: List<StandingEntry>,
    val sulAmericana: List<StandingEntry>,
    val rebaixados: List<StandingEntry>
)

fun bucketizeStandings(ordered: List<StandingEntry>) = StandingsBuckets(
    libertadores = ordered.drop(0).take(4),
    preLibertadores = ordered.drop(4).take(2),
    sulAmericana = ordered.drop(6).take(6),
    rebaixados = ordered.takeLast(4)
)
