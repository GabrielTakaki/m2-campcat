package com.example.campeonatocatarinense.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Analogia React:
 * - <MatchCard /> com "props" (teamA, teamB, ...).
 * - TextFields são "controlled components": estado local ga/gb (useState) + onChange.
 * - onEdit/onSaveInline = callbacks vindas do pai (RoundsScreen), como props em React.
 */
@Composable
fun MatchCard(
    teamA: String,
    teamB: String,
    goalsA: Int?,
    goalsB: Int?,
    location: String?,
    date: String?,
    onEdit: () -> Unit,
    onSaveInline: ((a: Int, b: Int) -> Unit)? = null,
    teamNameWidth: Dp = 120.dp,          // << largura fixa p/ nomes (evita "dançar" do placar)
    scoreFieldWidth: Dp = 32.dp          // largura dos inputs de gols
) {
    // React: useState controlando o valor digitado
    var ga by remember(goalsA) { mutableStateOf(goalsA?.toString() ?: "") }
    var gb by remember(goalsB) { mutableStateOf(goalsB?.toString() ?: "") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onEdit() } // tocar no card abre o editor (bottom sheet)
    ) {
        Column(Modifier.padding(16.dp)) {

            // [nome A   |  PLACAR CENTRALIZADO (Box com weight)  |   nome B]
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Coluna 1: Nome do time A (largura fixa, ellipsis)
                Text(
                    text = teamA,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.width(teamNameWidth)
                )

                // Coluna 2: Box com weight para centralizar o placar no card
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    ScoreInputs(
                        a = ga,
                        b = gb,
                        fieldWidth = scoreFieldWidth,
                        // React: onChange controlado pelo pai
                        onChangeA = { ga = it.filter { c -> c.isDigit() }.take(2) },
                        onChangeB = { gb = it.filter { c -> c.isDigit() }.take(2) }
                    )
                }

                // Coluna 3: Nome do time B (largura fixa, ellipsis, alinhado à direita)
                Text(
                    text = teamB,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(teamNameWidth)
                )
            }

            Spacer(Modifier.height(8.dp))

            // ===== Linha de localização/data CENTRALIZADA =====
            Text(
                text = "📍 ${location ?: "—"}  •  📅 ${date ?: "—"}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,         // << centraliza o conteúdo
                modifier = Modifier.fillMaxWidth()    // << ocupa largura total do card
            )

            // ===== Botão Salvar (quando inline está habilitado) =====
            if (onSaveInline != null) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            onSaveInline(ga.toIntOrNull() ?: 0, gb.toIntOrNull() ?: 0)
                        }
                    ) { Text("Salvar") }
                }
            }
        }
    }
}

/**
 * Subcomponente dos inputs de gols.
 * Analogia React: componente controlado (recebe value/onChange por props).
 */
@Composable
private fun ScoreInputs(
    a: String,
    b: String,
    fieldWidth: Dp,
    onChangeA: (String) -> Unit,
    onChangeB: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = a,
            onValueChange = onChangeA,
            modifier = Modifier.width(fieldWidth),
            singleLine = true,
        )
        Text("  -  ")
        OutlinedTextField(
            value = b,
            onValueChange = onChangeB,
            modifier = Modifier.width(fieldWidth),
            singleLine = true,
        )
    }
}
