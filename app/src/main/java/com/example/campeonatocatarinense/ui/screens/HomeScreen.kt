package com.example.campeonatocatarinense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onSeeRounds: () -> Unit, onSeeTable: () -> Unit) {
    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Outlined.Star, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Campeonato Catarinense")
                }
            }
        }
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner).padding(20.dp)) {
            Box(
                Modifier.fillMaxWidth().height(180.dp)
                    .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)),
                        shape = MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Text("Campeonato Catarinense 2025", color = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ElevatedButton(onClick = onSeeRounds, modifier = Modifier.weight(1f)) { Text("Ver Rodadas") }
                Spacer(Modifier.width(12.dp))
                ElevatedButton(onClick = onSeeTable, modifier = Modifier.weight(1f)) { Text("Ver Tabela") }
            }
        }
    }
}
