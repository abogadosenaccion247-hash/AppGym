package com.anayaasociados.appgym

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class NutritionSettings(prefs: SharedPreferences) {
    val weight = IntPref(prefs, "weight", 75)
    val height = IntPref(prefs, "height", 175)
    val age = IntPref(prefs, "age", 30)
    val activity = IntPref(prefs, "activity", 1)
    val goal = IntPref(prefs, "goal", 1)
}

private val activities = listOf("Ligera (3 días)", "Moderada (4-5 días)", "Alta (6 días)")
private val factors = listOf(1.375, 1.55, 1.725)
private val goals = listOf("Definir", "Mantener", "Ganar músculo")
private val adjust = listOf(-400.0, 0.0, 300.0)

private val meals = listOf(
    Triple("Desayuno", "🍳", "3 huevos revueltos, avena con plátano, papaya y té verde."),
    Triple("Media mañana", "🥜", "Yogur natural con frutos secos y una fruta de temporada."),
    Triple("Almuerzo", "🍗", "Pollo o pescado (bonito, jurel), quinua o arroz integral, ensalada grande y aceite de oliva."),
    Triple("Pre-entreno (1 h antes)", "🍌", "Camote o pan integral con mantequilla de maní y un plátano."),
    Triple("Post-entreno", "🥛", "Batido de proteína con leche o agua y una fruta; o atún con galletas de arroz."),
    Triple("Cena", "🥗", "Lentejas o menestras con huevo cocido, verduras al vapor y palta."),
    Triple("Antes de dormir", "🧀", "Queso fresco o yogur griego.")
)

@Composable
fun NutritionScreen(s: NutritionSettings) {
    val tdee = (10.0 * s.weight.value + 6.25 * s.height.value - 5.0 * s.age.value + 5) * factors[s.activity.value]
    val kcal = tdee + adjust[s.goal.value]
    val protein = s.weight.value * 2.0
    val fat = s.weight.value * 0.9
    val carbs = maxOf(0.0, (kcal - protein * 4 - fat * 9) / 4)

    Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Alimentación", fontSize = 28.sp, fontWeight = FontWeight.Black)

        InfoCard("Tu perfil") {
            Counter("Peso", "kg", s.weight, 40..200)
            Counter("Estatura", "cm", s.height, 140..220)
            Counter("Edad", "años", s.age, 15..80)
            Text("Actividad", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Chips(activities, s.activity)
            Text("Objetivo", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Chips(goals, s.goal)
        }

        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFFF9800), Color(0xFFE53935), Color(0xDD000000)))).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("META DIARIA", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Text("${kcal.toInt()} kcal", color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.Black)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Macro("🥩", protein, "Proteína", Modifier.weight(1f))
                Macro("🍚", carbs, "Carbohidratos", Modifier.weight(1f))
                Macro("🥑", fat, "Grasas", Modifier.weight(1f))
            }
            Text("💧 Agua: ${"%.1f".format(s.weight.value * 0.035)} L al día", color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        InfoCard("El plato balanceado") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Plate("🥗", "½", "Verduras y frutas", Color(0xFF43A047), Modifier.weight(1f))
                Plate("🍗", "¼", "Proteína magra", Color(0xFFE53935), Modifier.weight(1f))
                Plate("🍠", "¼", "Cereales integrales", Color(0xFFFF9800), Modifier.weight(1f))
            }
            Text("Añade grasas saludables (palta, frutos secos, aceite de oliva) en porciones pequeñas.",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        InfoCard("Ejemplo de día") {
            meals.forEach { (name, emoji, desc) ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(emoji, fontSize = 28.sp)
                    Column {
                        Text(name, fontWeight = FontWeight.Bold)
                        Text(desc, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Text("Guía orientativa (fórmula Mifflin-St Jeor). Consulta a un nutricionista si tienes condiciones médicas.",
            fontSize = 12.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Counter(label: String, unit: String, pref: IntPref, range: IntRange) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("$label: ${pref.value} $unit", Modifier.weight(1f))
        FilledTonalButton(onClick = { if (pref.value > range.first) pref.value-- }) { Text("−") }
        Spacer(Modifier.width(8.dp))
        FilledTonalButton(onClick = { if (pref.value < range.last) pref.value++ }) { Text("+") }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Chips(options: List<String>, pref: IntPref) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEachIndexed { i, o ->
            FilterChip(selected = pref.value == i, onClick = { pref.value = i }, label = { Text(o) })
        }
    }
}

@Composable
private fun Macro(emoji: String, grams: Double, name: String, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.18f)).padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 26.sp)
        Text("${grams.toInt()} g", color = Color.White, fontWeight = FontWeight.Bold)
        Text(name, color = Color.White, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
private fun Plate(emoji: String, frac: String, name: String, c: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(c.copy(alpha = 0.25f)).padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 30.sp)
        Text(frac, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text(name, fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}
