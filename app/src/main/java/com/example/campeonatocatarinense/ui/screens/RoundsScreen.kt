package com.example.campeonatocatarinense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campeonatocatarinense.ui.components.AppTopBar
import com.example.campeonatocatarinense.ui.components.BottomSheetMatchEditor
import com.example.campeonatocatarinense.ui.components.MatchCard
import com.example.campeonatocatarinense.viewmodel.CampeonatoViewModel
import com.example.campeonatocatarinense.viewmodel.EditPayload

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundsScreen(
    onBack: () -> Unit = {},
    onGoTable: () -> Unit = {},
    vm: CampeonatoViewModel = viewModel() // React: "useContext(Store)" + criar store por tela
) {
    // React: "useSelector(store => store.championship)" + hook que respeita lifecycle
    val champ by vm.championship.collectAsStateWithLifecycle()

    // React: useState para controlar modal (BottomSheet) e payload em edição
    var showSheet by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<EditPayload?>(null) }

    Scaffold(
        // React: AppBar como header do layout
        topBar = { AppTopBar(title = "Lista de Jogos", canNavigateBack = true, onBack = onBack) },

        // React: FAB = <FloatingButton />; onClick prepara estado de criação/edição
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // React: setEditing(payloadGerado); setShowSheet(true)
                vm.prepareAddOrEdit { payload ->
                    editing = payload
                    showSheet = true
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            if (champ == null) {
                // React: render "loading"
                LinearProgressIndicator(Modifier.fillMaxWidth())
            } else {
                // React: <FlatList/> / .map() — lista virtualizada
                LazyColumn {
                    champ!!.rounds.forEach { round ->
                        item {
                            Text(
                                "Rodada ${round.number}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 16.dp, top = 12.dp)
                            )
                        }
                        items(round.matches, key = { it.id }) { m ->
                            val a = champ!!.teams.first { it.id == m.teamA }
                            val b = champ!!.teams.first { it.id == m.teamB }

                            // React: <MatchCard/> filho com props + callbacks
                            MatchCard(
                                teamA = a.name,
                                teamB = b.name,
                                goalsA = m.goalsA,
                                goalsB = m.goalsB,
                                location = m.location,
                                date = m.date,
                                onEdit = {
                                    // React: setEditing({...}); setShowSheet(true)
                                    editing = EditPayload(
                                        round = round.number,
                                        matchId = m.id,
                                        teamAId = a.id,
                                        teamBId = b.id,
                                        teamAName = a.name,
                                        teamBName = b.name,
                                        goalsA = m.goalsA,
                                        goalsB = m.goalsB,
                                        location = m.location ?: "",
                                        date = m.date ?: ""
                                    )
                                    showSheet = true
                                },
                                onSaveInline = { ga, gb ->
                                    // React/Redux: dispatch(action) — aqui só altera placar/local/data
                                    vm.updateMatch(
                                        round = round.number,
                                        matchId = m.id,
                                        teamAId = null,         // não troca time no inline
                                        teamBId = null,
                                        ga = ga, gb = gb,
                                        loc = m.location, dt = m.date
                                    )
                                }
                            )
                        }
                    }

                    // CTA para ver tabela
                    item {
                        Spacer(Modifier.height(72.dp))
                        Button(
                            onClick = onGoTable,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        ) { Text("Ver Tabela") }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // React: <Modal open={showSheet}> ... </Modal>
    if (showSheet && editing != null && champ != null) {
        BottomSheetMatchEditor(
            title = "Editar/Adicionar Jogo",
            teams = champ!!.teams,                // React: prop com lista para <Select>
            initTeamAId = editing!!.teamAId,
            initTeamBId = editing!!.teamBId,
            initGoalsA = editing!!.goalsA,
            initGoalsB = editing!!.goalsB,
            initLocation = editing!!.location,
            initDate = editing!!.date,
            onDismiss = { showSheet = false },
            onConfirm = { teamAId, teamBId, ga, gb, loc, dt ->
                // React/Redux: dispatch(action) — pode ser update ou add
                vm.commitAddOrEdit(
                    editing = editing!!.copy(teamAId = teamAId, teamBId = teamBId),
                    ga = ga, gb = gb, loc = loc, dt = dt
                )
                showSheet = false
            }
        )
    }
}
