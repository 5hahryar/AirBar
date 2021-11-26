package com.shahryar.airbar

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
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
    backgroundColor: Color = colorResource(id = R.color.defaultBackground),
    cornerRadius: Dp = 40.dp,
    maxValue: Double = 0.0,
    minValue: Double = 100.0,
    onPercentageChanged: (Double) -> Unit
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
    fun getProgress(percentage: Double): Double {
        return String.format("%.2f", ((((maxValue - minValue) * percentage) / 100.00) + minValue) - minValue).toDouble()
    }

    /**
     * Calculate percentage
     */
    fun getPercentage(touchY: Float): Double {
        val percentage = String.format(
            "%.2f",
            (100 - ((touchY.toDouble() / bottomY.toDouble()) * 100))
        ).toDouble()
//        onProgressChanged(getProgress(percentage))

        return percentage
    }

    Canvas(modifier = modifier.pointerInteropFilter { event ->
        when {
            event.y in 0.0..bottomY.toDouble() -> {
                progress = event.y
                onPercentageChanged(getPercentage(event.y))
                true
            }
            event.y > 100 -> {
                progress = bottomY
                onPercentageChanged(getPercentage(bottomY))
                true
            }
            event.y < 0 -> {
                progress = 0F
                onPercentageChanged(getPercentage(0F))
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
        })
    }
}

@ExperimentalComposeUiApi
@Composable
@Preview
fun Preview() {
    AirBar(onPercentageChanged = {})
}