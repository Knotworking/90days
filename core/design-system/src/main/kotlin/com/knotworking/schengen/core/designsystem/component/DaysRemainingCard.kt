package com.knotworking.schengen.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knotworking.schengen.core.designsystem.theme.SchengenAppTheme

private val arcColor: (daysRemaining: Int) -> Color = { days ->
    when {
        days > 60 -> Color(0xFF4CAF50) // green
        days > 30 -> Color(0xFFFFC107) // amber
        days > 10 -> Color(0xFFFF9800) // orange
        else      -> Color(0xFFF44336) // red
    }
}

@Composable
fun DaysRemainingCard(
    daysUsed: Int,
    daysRemaining: Int,
    modifier: Modifier = Modifier,
    arcSize: Dp = 140.dp
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                val color = arcColor(daysRemaining)
                val trackColor = MaterialTheme.colorScheme.surfaceVariant
                val strokeWidth = 12.dp
                val sweepAngle = (daysUsed / 90f).coerceIn(0f, 1f) * 300f

                Canvas(modifier = Modifier.size(arcSize)) {
                    val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                    val inset = strokeWidth.toPx() / 2
                    val arcRect = Size(size.width - inset * 2, size.height - inset * 2)
                    val topLeft = Offset(inset, inset)

                    // Track (background arc)
                    drawArc(
                        color = trackColor,
                        startAngle = 120f,
                        sweepAngle = 300f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcRect,
                        style = stroke
                    )
                    // Progress arc
                    drawArc(
                        color = color,
                        startAngle = 120f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcRect,
                        style = stroke
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = daysRemaining.toString(),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = arcColor(daysRemaining)
                    )
                    Text(
                        text = "days left",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "$daysUsed / 90 days used",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun DaysRemainingCardPreview() {
    SchengenAppTheme {
        DaysRemainingCard(daysUsed = 35, daysRemaining = 55)
    }
}

@Preview
@Composable
private fun DaysRemainingCardLowPreview() {
    SchengenAppTheme {
        DaysRemainingCard(daysUsed = 82, daysRemaining = 8)
    }
}
