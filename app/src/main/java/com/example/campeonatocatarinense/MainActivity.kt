package com.example.campeonatocatarinense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.campeonatocatarinense.ui.screens.HomeScreen
import com.example.campeonatocatarinense.ui.screens.RoundsScreen
import com.example.campeonatocatarinense.ui.screens.TableScreen
import com.example.campeonatocatarinense.ui.theme.CampeonatoCatarinenseTheme

private enum class Screen { Home, Rounds, Table }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampeonatoCatarinenseTheme {
                AppRoot()
            }
        }
    }
}

@Composable
private fun AppRoot() {
    var current by rememberSaveable { mutableStateOf(Screen.Home) }

    BackHandler(enabled = current != Screen.Home) { current = Screen.Home }

    Scaffold(modifier = Modifier.fillMaxSize()) { inner ->
        Box(Modifier.padding(inner)) {
            when (current) {
                Screen.Home -> HomeScreen(
                    onSeeRounds = { current = Screen.Rounds },
                    onSeeTable  = { current = Screen.Table }
                )
                Screen.Rounds -> RoundsScreen(
                    onBack    = { current = Screen.Home },
                    onGoTable = { current = Screen.Table }
                )
                Screen.Table -> TableScreen(
                    onBack       = { current = Screen.Home },
                    onSeeSummary = { /* se quiser: adicionar SummaryScreen */ }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    CampeonatoCatarinenseTheme {
        HomeScreen(
            onSeeRounds = {},
            onSeeTable = {}
        )
    }
}
