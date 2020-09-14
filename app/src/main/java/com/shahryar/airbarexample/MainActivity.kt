package com.shahryar.airbarexample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.shahryar.airbar.AirBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        airbar.setOnProgressChangedListener(object : AirBar.OnProgressChangedListener{
            override fun onProgressChanged(airBar: AirBar, progress: Double, percentage: Double) {
                text.text = "Progress: $progress \t Percentage: $percentage"
            }

            override fun afterProgressChanged(
                    airBar: AirBar,
                    progress: Double,
                    percentage: Double
            ) {
                Toast.makeText(baseContext, "After Progress Changed: $progress", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun onClick(view: View) {
        if (view.id == R.id.button) {
            if (textBackgroundColor.text.toString() != "") airbar.backgroundFillColor = Color.parseColor(textBackgroundColor.text.toString())
            if (textLevelColor.text.toString() != "") airbar.progressBarFillColor = Color.parseColor(textLevelColor.text.toString())
            if (textGradColor0.text.toString() != "") airbar.progressBarColor0 = Color.parseColor(textGradColor0.text.toString())
            if (textGradColor1.text.toString() != "") airbar.progressBarColor1 = Color.parseColor(textGradColor1.text.toString())
        }
        else {
            ColorPickerDialog
                    .Builder(this)        			// Pass Activity Instance
                    .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                    .setDefaultColor(Color.WHITE)        	// Pass Default Color
                    .setColorListener { color, colorHex ->
                        findViewById<TextView>(view.id).text = colorHex
                    }
                    .show()
        }
    }
}