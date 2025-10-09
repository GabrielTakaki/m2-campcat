package com.example.campeonatocatarinense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.campeonatocatarinense.domain.bucketizeStandings
import com.example.campeonatocatarinense.ui.components.AppTopBar
import com.example.campeonatocatarinense.viewmodel.CampeonatoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SummaryScreen(onBack: () -> Unit = {}, vm: CampeonatoViewModel = viewModel()) {
    val standings by vm.standings.collectAsStateWithLifecycle()
    val b = bucketizeStandings(standings)

    Scaffold(topBar = { AppTopBar(title = "Ganhadores", canNavigateBack = true, onBack = onBack) }) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            @Composable
            fun Pill(title: String, items: List<String>) {
                ElevatedCard(Modifier.fillMaxWidth().padding(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(title, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        items.forEachIndexed { i, s ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${i+1} • $s")
                                Text(if (title.contains("Rebaixados")) "⤵" else "🏆")
                            }
                        }
                    }
                }
            }
            Pill("Classificados para Libertadores", b.libertadores.map { it.team.name })
            Pill("Classificados para Pré-Libertadores", b.preLibertadores.map { it.team.name })
            Pill("Classificados para Sul-Americana", b.sulAmericana.map { it.team.name })
            Pill("Rebaixados", b.rebaixados.map { it.team.name })

            Spacer(Modifier.weight(1f))
            Button(onClick = { vm.restart() }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text("Reiniciar Campeonato")
            }
        }
    }
}
