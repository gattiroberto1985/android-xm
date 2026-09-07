package it.gr85.android.apps.em.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.sqrt

data class PieSlice(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun PieChartWithLegend(
    title: String,
    slices: List<PieSlice>,
    modifier: Modifier = Modifier,
    onSliceTapped: (PieSlice) -> Unit = {}
) {
    var selectedSliceIndex by remember { mutableStateOf<Int?>(null) }

    val total = slices.sumOf { it.value.toDouble() }.toFloat()
    val percentages = slices.map { it.value / total }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        // PIE CHART
        PieChartCanvas(
            slices = slices,
            percentages = percentages,
            selectedIndex = selectedSliceIndex,
            onSliceTapped = { index ->
                selectedSliceIndex = index
                onSliceTapped(slices[index])
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(250.dp)
        )

        // LEGENDA
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            slices.forEachIndexed { index, slice ->
                LegendItem(
                    slice = slice,
                    percentage = percentages[index] * 100,
                    isSelected = selectedSliceIndex == index,
                    onClick = {
                        selectedSliceIndex = index
                        onSliceTapped(slice)
                    }
                )
            }
        }
    }
}

@Composable
fun PieChartCanvas(
    slices: List<PieSlice>,
    percentages: List<Float>,
    selectedIndex: Int?,
    onSliceTapped: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                val tappedSlice = detectTappedSlice(
                    offset = offset,
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.width / 2f,
                    slices = slices,
                    percentages = percentages
                )
                tappedSlice?.let { onSliceTapped(it) }
            }
        }
    ) {
        val radius = size.width / 2f
        val center = Offset(radius, radius)

        var currentAngle = -90f
        slices.forEachIndexed { index, slice ->
            val sweepAngle = percentages[index] * 360f
            val isSelected = selectedIndex == index
            val scaleFactor = if (isSelected) 1.1f else 1f

            drawArc(
                color = slice.color,
                startAngle = currentAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = androidx.compose.ui.geometry.Size(
                    radius * 2f * scaleFactor,
                    radius * 2f * scaleFactor
                ),
                topLeft = Offset(
                    center.x - radius * scaleFactor,
                    center.y - radius * scaleFactor
                )
            )
            currentAngle += sweepAngle
        }
    }
}

@Composable
fun LegendItem(
    slice: PieSlice,
    percentage: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .background(
                color = if (isSelected) Color(0xFFF5F5F5) else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF5F5F5) else Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .pointerInput(Unit) {
                    detectTapGestures { onClick() }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Quadrato colore
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(slice.color, RoundedCornerShape(2.dp))
            )

            // Label e percentuale
            Column {
                Text(
                    text = slice.label,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp
                )
                Text(
                    text = "€${slice.value} (${String.format("%.1f", percentage)}%)",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// Hit detection: calcola quale fetta è stata tappata
private fun detectTappedSlice(
    offset: Offset,
    center: Offset,
    radius: Float,
    slices: List<PieSlice>,
    percentages: List<Float>
): Int? {
    val dx = offset.x - center.x
    val dy = offset.y - center.y
    val distance = sqrt(dx * dx + dy * dy)

    // Se il tap è fuori dal raggio, non tocca nulla
    if (distance > radius) return null

    // Calcola l'angolo del tap (in gradi, da -90°)
    var tapAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    if (tapAngle < -90) tapAngle += 360f

    // Trova quale fetta corrisponde all'angolo
    var currentAngle = -90f
    slices.forEachIndexed { index, _ ->
        val sweepAngle = percentages[index] * 360f
        if (tapAngle in currentAngle..(currentAngle + sweepAngle)) {
            return index
        }
        currentAngle += sweepAngle
    }

    return null
}

@Preview
@Composable
fun PieChartPreview() {
    val sampleData = listOf(
        PieSlice("Cibo", 15000f, Color(0xFF4CAF50)),
        PieSlice("Trasporti", 8000f, Color(0xFF2196F3)),
        PieSlice("Intrattenimento", 5000f, Color(0xFFFF9800)),
        PieSlice("Altro", 2000f, Color(0xFF9C27B0))
    )

    PieChartWithLegend(
        title = "ESEMPIO DI PIE CHART",
        slices = sampleData,
        onSliceTapped = { slice ->
            println("Tappato: ${slice.label}")
        }
    )
}