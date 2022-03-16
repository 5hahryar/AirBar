package com.shahryar.airbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@ExperimentalComposeUiApi
@Composable
fun AirBar(
    modifier: Modifier = Modifier,
    fillColor: Color = colorResource(id = R.color.defaultLevel),
    fillColorGradient: List<Color>? = null,
    backgroundColor: Color = colorResource(id = R.color.defaultBackground),
    cornerRadius: Dp = 40.dp,
    minValue: Double = 0.0,
    maxValue: Double = 100.0,
    icon: Painter? = null,
    onValuesChanged: (percentage: Double, value: Double) -> Unit
) {
    var progress: Float by remember {
        mutableStateOf(0F)
    }

    var bottomY: Float by remember {
        mutableStateOf(0F)
    }

    /**
     * Calculate progress
     */
    fun calculateValues(touchY: Float): Pair<Double, Double> {
        val rawPercentage =
            String.format("%.2f", 100 - ((touchY.toDouble() / bottomY.toDouble()) * 100)).toDouble()
        val percentage =
            if (rawPercentage < 0) 0.0 else if (rawPercentage > 100) 100.0 else rawPercentage

        val value = String.format("%.2f", ((percentage / 100) * (maxValue - minValue) + minValue))
            .toDouble()

        return Pair(percentage, value)
    }

    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Canvas(modifier = modifier.pointerInteropFilter { event ->
            when {
                event.y in 0.0..bottomY.toDouble() -> {
                    progress = event.y
                    val values = calculateValues(event.y)
                    onValuesChanged(values.first, values.second)
                    true
                }
                event.y > 100 -> {
                    progress = bottomY
                    val values = calculateValues(event.y)
                    onValuesChanged(values.first, values.second)
                    true
                }
                event.y < 0 -> {
                    progress = 0F
                    val values = calculateValues(event.y)
                    onValuesChanged(values.first, values.second)
                    true
                }
                else -> false
            }
        }) {
            bottomY = size.height

            val path = Path()
            path.addRoundRect(
                roundRect = RoundRect(
                    0F,
                    0F,
                    size.width,
                    size.height,
                    CornerRadius(x = cornerRadius.value, y = cornerRadius.value)
                )
            )
            drawContext.canvas.drawPath(path, Paint().apply {
                color = backgroundColor
                isAntiAlias = true
            })
            drawContext.canvas.clipPath(path = path, ClipOp.Intersect)
            drawContext.canvas.drawRect(0F, progress, size.width, size.height, Paint().apply {
                color = fillColor
                isAntiAlias = true
                if (!fillColorGradient.isNullOrEmpty() && fillColorGradient.size > 1) {
                    shader = LinearGradientShader(
                        from = Offset(0f, 0f),
                        to = Offset(size.width, size.height),
                        colors = fillColorGradient
                    )
                }
            })
        }

        icon?.let {
            Icon(
                modifier = Modifier.padding(bottom = 15.dp),
                painter = it,
                contentDescription = ""
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
@Preview
fun Preview() {
    AirBar { _, _ -> }
}