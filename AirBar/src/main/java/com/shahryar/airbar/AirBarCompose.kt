package com.shahryar.airbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class AirBarController(
    progress: Double = 0.0,
    progressCoordinates: Float = 0f,
    internal val isHorizontal: Boolean = false,
    animateProgress: Boolean = true
) {

    var animateProgress by mutableStateOf(animateProgress)
    internal var internalProgress by mutableStateOf(progress)
    internal var progressCoordinates by mutableStateOf(progressCoordinates)

    var progress: Double
        get() = internalProgress
        set(value) {
            progressCoordinates = reverseCalculateValues(value).toFloat()
            internalProgress = value
        }

    internal var bottomY = 0f
    internal var rightX = 0f

    private fun reverseCalculateValues(realPercentage: Double): Double {
        val p = if (isHorizontal)
            realPercentage * rightX / 100
        else
            bottomY - realPercentage * bottomY / 100

        return String.format("%.2f", p).toDouble()
    }

}

@Composable
fun rememberAirBarController(
    progress: Double = 0.0,
    progressCoordinates: Float = 0f,
    isHorizontal: Boolean = false,
    animateProgress: Boolean = true
) = remember { AirBarController(progress, progressCoordinates, isHorizontal, animateProgress) }

@ExperimentalComposeUiApi
@Composable
fun AirBar(
    modifier: Modifier = Modifier,
    controller: AirBarController = rememberAirBarController(),
    fillColor: Color = MaterialTheme.colors.primary,
    fillColorGradient: List<Color>? = null,
    backgroundColor: Color = MaterialTheme.colors.background,
    cornerRadius: CornerRadius = CornerRadius(x = 40.dp.value, y = 40.dp.value),
    icon: (@Composable () -> Unit)? = null,
    valueChanged: (Double) -> Unit
) {

    // Changing min and max values are disabled for now
    // TODO: decide on whether to use a percentage or raw value output?
    val minValue: Double = 0.0
    val maxValue: Double = 100.0

    val vertical = if (controller.isHorizontal) 0f else controller.progressCoordinates
    val horizontal = if (controller.isHorizontal) controller.progressCoordinates else controller.rightX

    val bottomToTop = if(controller.animateProgress) animateFloatAsState(vertical).value else vertical
    val startToEnd = if(controller.animateProgress) animateFloatAsState(horizontal).value else horizontal

    fun calculateValues(touchY: Float, touchX: Float): Double {
        val rawPercentage = if (controller.isHorizontal) {
            String.format("%.2f", (touchX.toDouble() / controller.rightX.toDouble()) * 100).toDouble()
        } else {
            String.format("%.2f", 100 - ((touchY.toDouble() / controller.bottomY.toDouble()) * 100)).toDouble()
        }

        val percentage = if (rawPercentage < 0) 0.0 else if (rawPercentage > 100) 100.0 else rawPercentage

        return String.format("%.2f", ((percentage / 100) * (maxValue - minValue) + minValue)).toDouble()
    }

    Box(modifier = modifier, contentAlignment = if (controller.isHorizontal) Alignment.CenterStart else Alignment.BottomCenter) {
        Canvas(modifier = modifier.pointerInteropFilter { event ->
            if (!controller.isHorizontal) {
                when {
                    event.y in 0.0..controller.bottomY.toDouble() -> {
                        valueChanged(calculateValues(event.y, event.x))
                        true
                    }
                    event.y > 100 -> {
                        valueChanged(calculateValues(event.y, event.x))
                        true
                    }
                    event.y < 0 -> {
                        valueChanged(calculateValues(event.y, event.x))
                        true
                    }
                    else -> false
                }
            } else {
                when {
                    event.x in 0.0..controller.rightX.toDouble() -> {
                        valueChanged(calculateValues(event.y, event.x))
                        true
                    }
                    event.x > 100 -> {
                        valueChanged(calculateValues(event.y, event.x))
                        true
                    }
                    event.x < 0 -> {
                        valueChanged(calculateValues(event.y, event.x))
                        true
                    }
                    else -> false
                }
            }
        }) {
            controller.bottomY = size.height
            controller.rightX = size.width
            if(controller.internalProgress > 0.0) controller.progress = controller.internalProgress

            val path = Path()
            path.addRoundRect(
                roundRect = RoundRect(
                    0F,
                    0F,
                    size.width,
                    size.height,
                    cornerRadius
                )
            )
            drawContext.canvas.drawPath(path, Paint().apply {
                color = backgroundColor
                isAntiAlias = true
            })
            drawContext.canvas.clipPath(path = path, ClipOp.Intersect)
            drawContext.canvas.drawRect(
                0F,
                bottomToTop,
                startToEnd,
                size.height,
                Paint().apply {
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

        icon?.let { Box(modifier = if (!controller.isHorizontal) Modifier.padding(bottom = 15.dp) else Modifier.padding(start = 15.dp)) { it() } }
    }
}

@ExperimentalComposeUiApi
@Composable
@Preview
fun Preview() {
    val airBar = rememberAirBarController(45.0)
    AirBar { airBar.progress = it }
}
