package com.anayaasociados.appgym

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val CardBg = Color(0xFF1E1E22)

@Composable
fun MuscleChip(m: Muscle) {
    Text(m.label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.background(m.color.copy(alpha = 0.9f), CircleShape).padding(horizontal = 10.dp, vertical = 4.dp))
}

@Composable
fun DayScreen(day: WorkoutDay, store: ProgressStore, onBack: (() -> Unit)?, onExercise: (WorkoutDay, Exercise) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (onBack != null) item { TextButton(onClick = onBack) { Text("← Semana") } }
        item {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(day.brush).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val today = day.index == WorkoutData.todayIndex
                Text(if (today) "RUTINA DE HOY · ${day.weekday.uppercase()}" else day.weekday.uppercase(),
                    color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                Text(day.title, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, lineHeight = 36.sp)
                if (!day.isRest) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) { day.muscles.take(4).forEach { MuscleChip(it) } }
                    LinearProgressIndicator(progress = { store.progress(day) }, Modifier.fillMaxWidth(), color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f))
                    Text("${(store.progress(day) * 100).toInt()}% completado • ${day.exercises.size} ejercicios",
                        color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                    BodyMap(day.muscles.toSet(), Modifier.fillMaxWidth().height(260.dp))
                }
            }
        }
        if (day.isRest) item {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(CardBg).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🛌", fontSize = 44.sp)
                Text("Día de recuperación", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Camina 30-40 min, estira 10 min y duerme 7-9 horas. Los músculos crecen mientras descansas.",
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(day.exercises, key = { it.name }) { ex ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(CardBg)
                .clickable { onExercise(day, ex) }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(84.dp).clip(RoundedCornerShape(18.dp)).background(ex.brush), contentAlignment = Alignment.Center) {
                    Text(ex.primary.emoji, fontSize = 36.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(ex.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${ex.sets} series × ${ex.reps}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    MuscleChip(ex.primary)
                }
                Checkbox(checked = store.isDone(day, ex), onCheckedChange = { store.toggle(day, ex) })
            }
        }
    }
}

@Composable
fun WeekScreen(store: ProgressStore, onOpen: (Int) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("Tu semana", fontSize = 28.sp, fontWeight = FontWeight.Black) }
        items(WorkoutData.week, key = { it.index }) { d ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(d.brush)
                .clickable { onOpen(d.index) }.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(d.weekday.uppercase(), color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    Text(d.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(if (d.isRest) "Recuperación" else d.muscles.joinToString(" • ") { it.label },
                        color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                }
                if (d.index == WorkoutData.todayIndex)
                    Text("HOY", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Black,
                        modifier = Modifier.background(Color.White, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp))
                else if (!d.isRest && store.progress(d) >= 1f) Text("✅", fontSize = 22.sp)
            }
        }
    }
}

@Composable
fun ExerciseDetailScreen(day: WorkoutDay, ex: Exercise, store: ProgressStore, onBack: () -> Unit) {
    var remaining by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }
    LaunchedEffect(running) {
        if (running) {
            while (remaining > 0) { delay(1000); remaining-- }
            running = false
        }
    }
    Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextButton(onClick = onBack) { Text("← ${day.title}") }
        Text(ex.name, fontSize = 26.sp, fontWeight = FontWeight.Black)

        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(ex.brush).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("MÚSCULO TRABAJADO", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Text(ex.primary.label, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            BodyMap(setOf(ex.primary), Modifier.fillMaxWidth().height(300.dp), secondary = ex.secondary.toSet())
            if (ex.secondary.isNotEmpty())
                Text("También: " + ex.secondary.joinToString { it.label }, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Stat("${ex.sets}", "SERIES", Modifier.weight(1f))
            Stat(ex.reps, "REPS", Modifier.weight(1f))
            Stat(if (ex.rest > 0) "${ex.rest}s" else "—", "DESCANSO", Modifier.weight(1f))
        }

        if (ex.rest > 0) Button(onClick = { remaining = ex.rest; running = true }, Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ex.primary.color, contentColor = Color.White)) {
            Text(if (remaining > 0) "⏱ Descansando… $remaining s" else "⏱ Iniciar descanso (${ex.rest} s)", Modifier.padding(6.dp))
        }

        InfoCard("Cómo hacerlo") {
            ex.steps.forEachIndexed { i, s ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("${i + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                        modifier = Modifier.size(22.dp).background(ex.primary.color, CircleShape).wrapContentSize())
                    Text(s)
                }
            }
        }
        InfoCard("Consejo del entrenador") { Text("💡 ${ex.tip}", color = Color(0xFFFFEB3B)) }

        val done = store.isDone(day, ex)
        Button(onClick = { store.toggle(day, ex) }, Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (done) Color(0xFF43A047) else Color(0xFFFF9800), contentColor = Color.White)) {
            Text(if (done) "✅ Completado" else "Marcar como completado", Modifier.padding(6.dp))
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun Stat(value: String, label: String, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(16.dp)).background(CardBg).padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(CardBg).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        content()
    }
}
