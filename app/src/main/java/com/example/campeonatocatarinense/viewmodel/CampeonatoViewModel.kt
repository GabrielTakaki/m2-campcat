package com.example.campeonatocatarinense.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.campeonatocatarinense.data.ChampionshipRepository
import com.example.campeonatocatarinense.data.model.Match
import com.example.campeonatocatarinense.domain.StandingEntry
import com.example.campeonatocatarinense.domain.computeStandings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * React analogy:
 * - tipo um "mini Redux store" por tela: mantém estado derivado (standings),
 *   expõe StateFlows (observáveis) e oferece "actions" (updateMatch/addMatch).
 * - A UI (Compose) se inscreve via collectAsStateWithLifecycle() e re-renderiza.
 */
class CampeonatoViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ChampionshipRepository(app) // "fonte de verdade" (source of truth)

    val championship = repo.champ                 // React: store slice (StateFlow<Championship?>)

    private val _standings = MutableStateFlow<List<StandingEntry>>(emptyList())
    val standings: StateFlow<List<StandingEntry>> = _standings.asStateFlow()

    private val _ended = MutableStateFlow(false)
    val ended: StateFlow<Boolean> = _ended.asStateFlow()

    init { recompute() }

    private fun recompute() {
        val c = championship.value ?: return
        _standings.value = computeStandings(c)
        _ended.value = repo.allMatchesScored()
    }

    // React/Redux: action creator -> repository -> emite novo estado
    fun updateMatch(
        round: Int,
        matchId: String,
        teamAId: Int?,
        teamBId: Int?,
        ga: Int, gb: Int,
        loc: String?, dt: String?
    ) {
        repo.updateMatch(round, matchId, teamAId, teamBId, ga, gb, loc, dt)
        recompute()
    }

    fun addMatch(round: Int, matchId: String, teamA: Int, teamB: Int, ga: Int, gb: Int, loc: String, dt: String) {
        repo.addMatch(round, Match(matchId, teamA, teamB, ga, gb, loc, dt))
        recompute()
    }

    // Prepara payload padrão para "Adicionar" (como criar initial form state)
    fun prepareAddOrEdit(launch: (EditPayload) -> Unit) {
        val c = championship.value ?: return
        val r = c.rounds.first()
        val a = c.teams.first()
        val b = c.teams[1]
        launch(EditPayload(r.number, "r${r.number}mNEW${r.matches.size+1}", a.id, b.id, a.name, b.name, null, null, "", ""))
    }

    // Decide entre update ou add
    fun commitAddOrEdit(editing: EditPayload, ga: Int, gb: Int, loc: String, dt: String) {
        val exists = championship.value?.rounds?.any { r -> r.matches.any { it.id == editing.matchId } } == true
        if (exists) {
            updateMatch(editing.round, editing.matchId, editing.teamAId, editing.teamBId, ga, gb, loc, dt)
        } else {
            addMatch(editing.round, editing.matchId, editing.teamAId, editing.teamBId, ga, gb, loc, dt)
        }
    }

    fun restart() { repo.reset(getApplication()); recompute() }
}

data class EditPayload(
    val round: Int,
    val matchId: String,
    val teamAId: Int, val teamBId: Int,
    val teamAName: String, val teamBName: String,
    val goalsA: Int?, val goalsB: Int?,
    val location: String, val date: String
)
