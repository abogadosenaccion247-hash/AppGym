package com.anayaasociados.appgym

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val GymColors = darkColorScheme(
    primary = Color(0xFFFF9800),
    background = Color(0xFF0E0E10),
    surface = Color(0xFF0E0E10),
    surfaceVariant = Color(0xFF1E1E22),
    onSurfaceVariant = Color(0xFFB0B0B8)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = getSharedPreferences("appgym", Context.MODE_PRIVATE)
        val store = ProgressStore(prefs)
        val settings = NutritionSettings(prefs)
        setContent {
            MaterialTheme(colorScheme = GymColors) { AppGymRoot(store, settings) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppGymRoot(store: ProgressStore, settings: NutritionSettings) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var weekDay by rememberSaveable { mutableIntStateOf(-1) }       // día abierto desde "Semana"
    var exDay by rememberSaveable { mutableIntStateOf(-1) }         // ejercicio abierto: día
    var exName by rememberSaveable { mutableStateOf("") }           // ejercicio abierto: nombre

    BackHandler(exDay >= 0) { exDay = -1 }
    BackHandler(exDay < 0 && tab == 1 && weekDay >= 0) { weekDay = -1 }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                listOf("🔥" to "Hoy", "📅" to "Semana", "🍽️" to "Nutrición").forEachIndexed { i, (e, l) ->
                    NavigationBarItem(
                        selected = tab == i && exDay < 0,
                        onClick = { tab = i; exDay = -1; weekDay = -1 },
                        icon = { Text(e) }, label = { Text(l) }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad)) {
            val openDay = if (exDay >= 0) WorkoutData.week[exDay] else null
            val openEx = openDay?.exercises?.firstOrNull { it.name == exName }
            when {
                openDay != null && openEx != null ->
                    ExerciseDetailScreen(openDay, openEx, store) { exDay = -1 }
                tab == 0 -> DayScreen(WorkoutData.today, store, null) { d, e -> exDay = d.index; exName = e.name }
                tab == 1 && weekDay >= 0 ->
                    DayScreen(WorkoutData.week[weekDay], store, { weekDay = -1 }) { d, e -> exDay = d.index; exName = e.name }
                tab == 1 -> WeekScreen(store) { weekDay = it }
                else -> NutritionScreen(settings)
            }
        }
    }
}
