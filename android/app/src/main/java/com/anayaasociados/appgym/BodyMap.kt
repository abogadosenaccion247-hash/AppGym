package com.anayaasociados.appgym

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private class Region(val front: Boolean, val muscle: Muscle?, val x: Float, val y: Float, val w: Float, val h: Float, val oval: Boolean = false)

private val regions: List<Region> = buildList {
    for (f in listOf(true, false)) {
        add(Region(f, null, .42f, .02f, .16f, .10f, true))   // cabeza
        add(Region(f, null, .46f, .11f, .08f, .06f))         // cuello
        add(Region(f, null, .29f, .17f, .42f, .33f))         // torso
        add(Region(f, null, .32f, .47f, .36f, .07f))         // pelvis
        add(Region(f, null, .12f, .57f, .08f, .06f, true))   // manos
        add(Region(f, null, .80f, .57f, .08f, .06f, true))
        add(Region(f, null, .30f, .95f, .15f, .04f))         // pies
        add(Region(f, null, .55f, .95f, .15f, .04f))
        add(Region(f, Muscle.hombros, .19f, .18f, .11f, .09f, true))
        add(Region(f, Muscle.hombros, .70f, .18f, .11f, .09f, true))
        add(Region(f, Muscle.antebrazos, .13f, .42f, .08f, .14f))
        add(Region(f, Muscle.antebrazos, .79f, .42f, .08f, .14f))
        add(Region(f, Muscle.pantorrillas, .31f, .77f, .13f, .18f))
        add(Region(f, Muscle.pantorrillas, .56f, .77f, .13f, .18f))
    }
    // Frente
    add(Region(true, Muscle.pecho, .30f, .20f, .19f, .10f)); add(Region(true, Muscle.pecho, .51f, .20f, .19f, .10f))
    add(Region(true, Muscle.biceps, .17f, .28f, .08f, .13f)); add(Region(true, Muscle.biceps, .75f, .28f, .08f, .13f))
    add(Region(true, Muscle.core, .40f, .31f, .20f, .17f))
    add(Region(true, Muscle.cuadriceps, .30f, .54f, .17f, .22f)); add(Region(true, Muscle.cuadriceps, .53f, .54f, .17f, .22f))
    // Espalda
    add(Region(false, Muscle.trapecio, .38f, .14f, .24f, .07f))
    add(Region(false, Muscle.espalda, .30f, .22f, .40f, .16f)); add(Region(false, Muscle.espalda, .36f, .38f, .28f, .08f))
    add(Region(false, Muscle.triceps, .17f, .28f, .08f, .13f)); add(Region(false, Muscle.triceps, .75f, .28f, .08f, .13f))
    add(Region(false, Muscle.gluteos, .31f, .47f, .38f, .09f))
    add(Region(false, Muscle.isquios, .30f, .57f, .17f, .19f)); add(Region(false, Muscle.isquios, .53f, .57f, .17f, .19f))
}

/** Muñeco masculino (frente y espalda) con los músculos trabajados iluminados. */
@Composable
fun BodyMap(primary: Set<Muscle>, modifier: Modifier = Modifier, secondary: Set<Muscle> = emptySet()) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
        for (front in listOf(true, false)) {
            Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (front) "FRENTE" else "ESPALDA", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Canvas(Modifier.weight(1f).aspectRatio(0.5f)) {
                    for (r in regions.filter { it.front == front }) {
                        val tl = Offset(r.x * size.width, r.y * size.height)
                        val sz = Size(r.w * size.width, r.h * size.height)
                        val corner = CornerRadius(minOf(sz.width, sz.height) * 0.35f)
                        val m = r.muscle
                        val color = when {
                            m != null && m in primary -> m.color
                            m != null && m in secondary -> m.color.copy(alpha = 0.45f)
                            else -> Color.Gray.copy(alpha = 0.28f)
                        }
                        if (m != null && m in primary) {
                            drawRoundRect(m.color.copy(alpha = 0.3f), tl - Offset(4f, 4f), Size(sz.width + 8f, sz.height + 8f), corner)
                        }
                        if (r.oval) {
                            drawOval(color, tl, sz)
                            drawOval(Color.White.copy(alpha = 0.35f), tl, sz, style = Stroke(1.2f))
                        } else {
                            drawRoundRect(color, tl, sz, corner)
                            drawRoundRect(Color.White.copy(alpha = 0.35f), tl, sz, corner, style = Stroke(1.2f))
                        }
                    }
                }
            }
        }
    }
}
