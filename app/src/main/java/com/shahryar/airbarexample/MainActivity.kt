package com.shahryar.airbarexample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.shahryar.airbar.AirBar
import com.shahryar.airbar.rememberAirBarController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        airbar.setOnProgressChangedListener(object : AirBar.OnProgressChangedListener {

            override fun onProgressChanged(airBar: AirBar, progress: Double, percentage: Double) {
                textXml.text = "ANDROID VIEW: Progress: $progress \t Percentage: $percentage"
            }

            override fun afterProgressChanged(
                airBar: AirBar,
                progress: Double,
                percentage: Double
            ) {
                Toast.makeText(
                    baseContext,
                    "ANDROID VIEW: After Progress Changed: $progress",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

        airbarCompose.setContent {

            val airBarController = rememberAirBarController(
                50.0,
                isHorizontal = true,
                animateProgress = true
            )

            Column(
                modifier = Modifier
                    .width(150.dp)
                    .height(80.dp)
            ) {
                AirBar(
                    modifier = Modifier.fillMaxSize(),
                    controller = airBarController,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_volume_up_24),
                            contentDescription = ""
                        )
                    },
                    backgroundColor = colorResource(id = R.color.defaultBackground),
                    valueChanged = { value ->
                        airBarController.progress = value
                        textComposePercentage.text =
                            "COMPOSE VIEW: Percentage: $value, Value: -"
                    }
                )

            }
        }
    }

    fun onClick(view: View) {
        if (view.id == R.id.button) {
            if (textBackgroundColor.text.toString() != "") airbar.backgroundFillColor =
                Color.parseColor(textBackgroundColor.text.toString())
            if (textLevelColor.text.toString() != "") airbar.progressBarFillColor =
                Color.parseColor(textLevelColor.text.toString())
            if (textGradColor0.text.toString() != "") airbar.progressBarColor0 =
                Color.parseColor(textGradColor0.text.toString())
            if (textGradColor1.text.toString() != "") airbar.progressBarColor1 =
                Color.parseColor(textGradColor1.text.toString())
        } else {
            ColorPickerDialog
                .Builder(this)                    // Pass Activity Instance
                .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                .setDefaultColor(Color.WHITE)            // Pass Default Color
                .setColorListener { color, colorHex ->
                    findViewById<TextView>(view.id).text = colorHex
                }
                .show()
        }
    }
}