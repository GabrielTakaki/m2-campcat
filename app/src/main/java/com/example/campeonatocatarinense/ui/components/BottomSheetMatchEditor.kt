package com.example.campeonatocatarinense.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.campeonatocatarinense.data.model.Team

/**
 * React analogies:
 * - Este componente é como um <Modal> com "form state" local (useState).
 * - Recebe a "store source of truth" por props (teams + valores iniciais).
 * - onConfirm = callback para "submeter" (como um onSubmit que despacha ação).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetMatchEditor(
    title: String,
    teams: List<Team>,                 // React: prop para preencher <Select>
    initTeamAId: Int,
    initTeamBId: Int,
    initGoalsA: Int?,
    initGoalsB: Int?,
    initLocation: String?,
    initDate: String?,
    onDismiss: () -> Unit,
    onConfirm: (teamAId: Int, teamBId: Int, ga: Int, gb: Int, location: String, date: String) -> Unit
) {
    // React: useState local do formulário
    var selectedA by remember { mutableStateOf(initTeamAId) }
    var selectedB by remember { mutableStateOf(initTeamBId) }
    var goalsA by remember { mutableStateOf(initGoalsA?.toString() ?: "") }
    var goalsB by remember { mutableStateOf(initGoalsB?.toString() ?: "") }
    var location by remember { mutableStateOf(initLocation ?: "") }
    var date by remember { mutableStateOf(initDate ?: "") }

    var expandA by remember { mutableStateOf(false) } // React: controle do dropdown A
    var expandB by remember { mutableStateOf(false) } // React: controle do dropdown B

    val teamNameA = teams.firstOrNull { it.id == selectedA }?.name ?: "Selecione"
    val teamNameB = teams.firstOrNull { it.id == selectedB }?.name ?: "Selecione"
    val sameTeamError = selectedA == selectedB

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            // Campo: Local do jogo
            OutlinedTextField(
                value = location, onValueChange = { location = it },
                label = { Text("Digite o local do jogo") }, leadingIcon = { Text("📍") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // ---- Select de Time X (A) ----
            ExposedDropdownMenuBox(expanded = expandA, onExpandedChange = { expandA = it }) {
                OutlinedTextField(
                    value = teamNameA,
                    onValueChange = {},
                    readOnly = true, // React: input "controlado" porém leitura via dropdown
                    label = { Text("Time X") },
                    leadingIcon = { Text("🏟️") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandA) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandA, onDismissRequest = { expandA = false }) {
                    teams.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t.name) },
                            onClick = { selectedA = t.id; expandA = false }
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // Gols do Time X
            OutlinedTextField(
                value = goalsA,
                onValueChange = { goalsA = it.filter { c -> c.isDigit() }.take(2) },
                label = { Text("Gols do Time X") }, leadingIcon = { Text("🥅") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // ---- Select de Time Y (B) ----
            ExposedDropdownMenuBox(expanded = expandB, onExpandedChange = { expandB = it }) {
                OutlinedTextField(
                    value = teamNameB,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time Y") },
                    leadingIcon = { Text("🏟️") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandB) },
                    isError = sameTeamError,
                    supportingText = { if (sameTeamError) Text("Times não podem ser iguais") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandB, onDismissRequest = { expandB = false }) {
                    teams.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t.name) },
                            onClick = { selectedB = t.id; expandB = false }
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // Gols do Time Y
            OutlinedTextField(
                value = goalsB,
                onValueChange = { goalsB = it.filter { c -> c.isDigit() }.take(2) },
                label = { Text("Gols do Time Y") }, leadingIcon = { Text("🥅") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // Data do jogo
            OutlinedTextField(
                value = date, onValueChange = { date = it },
                label = { Text("Digite a data do jogo (AAAA-MM-DD)") }, leadingIcon = { Text("📅") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // React: <Button disabled={sameTeamError} onClick={() => onConfirm(...)} />
            Button(
                onClick = {
                    onConfirm(
                        selectedA, selectedB,
                        goalsA.toIntOrNull() ?: 0,
                        goalsB.toIntOrNull() ?: 0,
                        location.ifBlank { "—" },
                        date.ifBlank { "—" }
                    )
                },
                enabled = !sameTeamError,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Salvar") }

            Spacer(Modifier.height(8.dp))
        }
    }
}
