package com.anayaasociados.appgym

import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.temporal.IsoFields

enum class Muscle(val label: String, val color: Color, val emoji: String) {
    pecho("Pecho", Color(0xFFE53935), "🏋️"),
    espalda("Espalda", Color(0xFF1E88E5), "🦍"),
    hombros("Hombros", Color(0xFFFB8C00), "💪"),
    trapecio("Trapecio", Color(0xFF00ACC1), "🏔️"),
    biceps("Bíceps", Color(0xFF8E24AA), "💪"),
    triceps("Tríceps", Color(0xFFEC407A), "💪"),
    antebrazos("Antebrazos", Color(0xFF8D6E63), "🤜"),
    core("Abdomen", Color(0xFFFDD835), "🔥"),
    cuadriceps("Cuádriceps", Color(0xFF43A047), "🦵"),
    isquios("Isquiotibiales", Color(0xFF00897B), "🦵"),
    gluteos("Glúteos", Color(0xFF3949AB), "🍑"),
    pantorrillas("Pantorrillas", Color(0xFF26A69A), "🦶")
}

data class Exercise(
    val name: String,
    val primary: Muscle,
    val secondary: List<Muscle> = emptyList(),
    val sets: Int,
    val reps: String,
    val rest: Int,
    val steps: List<String>,
    val tip: String
) {
    val brush: Brush
        get() = Brush.linearGradient(listOf(primary.color, primary.color.copy(alpha = 0.55f), Color.Black.copy(alpha = 0.85f)))
}

data class WorkoutDay(val index: Int, val weekday: String, val title: String, val exercises: List<Exercise>) {
    val isRest get() = exercises.isEmpty()
    val muscles: List<Muscle> get() = exercises.map { it.primary }.distinct()
    val brush: Brush
        get() {
            val c = muscles.map { it.color }
            val colors = when {
                c.isEmpty() -> listOf(Color.Gray, Color(0x990000FF))
                else -> c + Color.Black.copy(alpha = 0.8f)
            }
            return Brush.linearGradient(colors)
        }
}

class ProgressStore(private val prefs: SharedPreferences) {
    private val done = mutableStateListOf<String>().apply { addAll(prefs.getStringSet("done", emptySet()) ?: emptySet()) }

    private fun key(d: WorkoutDay, e: Exercise): String {
        val now = LocalDate.now()
        return "${now.get(IsoFields.WEEK_BASED_YEAR)}-${now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)}|${d.index}|${e.name}"
    }

    fun isDone(d: WorkoutDay, e: Exercise) = key(d, e) in done

    fun toggle(d: WorkoutDay, e: Exercise) {
        val k = key(d, e)
        if (!done.remove(k)) done.add(k)
        prefs.edit().putStringSet("done", done.toSet()).apply()
    }

    fun progress(d: WorkoutDay): Float =
        if (d.exercises.isEmpty()) 0f else d.exercises.count { isDone(d, it) }.toFloat() / d.exercises.size
}

class IntPref(private val prefs: SharedPreferences, private val key: String, default: Int) {
    private val state = mutableIntStateOf(prefs.getInt(key, default))
    var value: Int
        get() = state.intValue
        set(v) {
            state.intValue = v
            prefs.edit().putInt(key, v).apply()
        }
}
