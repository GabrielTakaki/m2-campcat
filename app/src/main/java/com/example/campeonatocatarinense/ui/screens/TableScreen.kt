package com.example.campeonatocatarinense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campeonatocatarinense.domain.bucketizeStandings
import com.example.campeonatocatarinense.viewmodel.CampeonatoViewModel

/**
 * React analogies:
 * - Este componente é como um <Table/> que recebe "store state" via hooks:
 *   standings/ended vêm do ViewModel (pense em um Redux store).
 * - A "coloração por faixa" é derivada do índice (1-based) de cada linha.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableScreen(
    onBack: () -> Unit = {},
    onSeeSummary: () -> Unit = {},
    vm: CampeonatoViewModel = viewModel()
) {
    val standings by vm.standings.collectAsStateWithLifecycle()
    val ended by vm.ended.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tabela") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {

            // ------- LEGENDA (React: componente <Legend/>) -------
            LegendRow()

            if (standings.isEmpty()) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            } else {
                LazyColumn {
                    // Cabeçalho (fixo)
                    item {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Time", fontWeight = FontWeight.SemiBold)
                            Text("P  V  E  D  SG", fontWeight = FontWeight.SemiBold)
                        }
                        Divider()
                    }

                    // Linhas (React: standings.map((e, idx) => <Row/>))
                    itemsIndexed(standings, key = { _, it -> it.team.id }) { idx, e ->
                        val pos = idx + 1
                        val band = bandForPosition(pos, standings.size)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                        ) {
                            // Faixa colorida à esquerda (estilo Google)
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .fillMaxHeight()
                                    .background(band?.color ?: Color.Transparent)
                            )

                            // Conteúdo da linha
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Esquerda: posição + nome
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$pos.",
                                        modifier = Modifier.widthIn(min = 28.dp),
                                        textAlign = TextAlign.End
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(e.team.name)
                                    if (band != null) {
                                        Spacer(Modifier.width(8.dp))
                                        BandPill(band.label, band.color)
                                    }
                                }

                                // Direita: estatísticas
                                Text("${e.points}  ${e.wins}  ${e.draws}  ${e.losses}  ${e.gd}")
                            }
                        }
                        Divider()
                    }

                    // Resumo + CTA
                    item {
                        if (ended) {
                            Spacer(Modifier.height(16.dp))
                            val b = bucketizeStandings(standings)
                            Column(Modifier.padding(horizontal = 16.dp)) {
                                Text("⚑ Campeonato encerrado", fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(8.dp))
                                Text("Libertadores: ${b.libertadores.joinToString { it.team.name }}")
                                Text("Pré-Libertadores: ${b.preLibertadores.joinToString { it.team.name }}")
                                Text("Sul-Americana: ${b.sulAmericana.joinToString { it.team.name }}")
                                Text("Rebaixados: ${b.rebaixados.joinToString { it.team.name }}")
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = onSeeSummary,
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Ver Resumo Final") }
                            }
                            Spacer(Modifier.height(24.dp))
                        } else {
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

/* ============================ SUPORTES DE UI ============================ */

/**
 * "Pílula" colorida com rótulo (React: <Chip/> simples)
 */
@Composable
private fun BandPill(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Legenda fixa mostrando as faixas e suas cores (React: <Legend/>)
 */
@Composable
private fun LegendRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LegendItem("LIB (1–4)", ColLib)
        LegendItem("PRÉ (5–6)", ColPre)
        LegendItem("SUL (7–12)", ColSul)
        LegendItem("REB (últ. 4)", ColReb)
    }
}

@Composable
private fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(12.dp).background(color))
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelSmall)
    }
}

/* ============================ REGRA DE CORES ============================ */

/**
 * React analogy:
 * - Função pura que mapeia "posição" -> "faixa" (lib, pré, sul, rebaixado).
 * - Mantém as regras solicitadas:
 *   - Libertadores: 1..4
 *   - Pré-Libertadores: 5..6
 *   - Sul-Americana: 7..12
 *   - Rebaixados: últimos 4 (independente do total)
 *
 * Observação:
 * - Para ligas pequenas, priorizamos "rebaixados" (últimos 4) para evitar sobreposição com SUL.
 */
private fun bandForPosition(pos: Int, total: Int): Band? {
    val last4Start = maxOf(1, total - 3) // começa nos 4 últimos
    return when {
        // Rebaixados têm prioridade quando lista é pequena (evitar sobreposição com 7..12)
        pos >= last4Start -> Band("REB", ColReb)
        pos in 1..4       -> Band("LIB", ColLib)
        pos in 5..6       -> Band("PRÉ", ColPre)
        pos in 7..12      -> Band("SUL", ColSul)
        else              -> null
    }
}

private data class Band(val label: String, val color: Color)

// Cores estilo "Google table vibe"
private val ColLib = Color(0xFF2E7D32) // verde escuro
private val ColPre = Color(0xFF66BB6A) // verde claro
private val ColSul = Color(0xFF1E88E5) // azul
private val ColReb = Color(0xFFE53935) // vermelho
