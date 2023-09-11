package com.reanimator.analogclock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.chip.ChipGroup

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clock = findViewById<AnalogClockView>(R.id.analog_clock)
        val chipGroup = findViewById<ChipGroup>(R.id.second_hand_color_chip_group)
        val colorInput = findViewById<EditText>(R.id.input_hand_color)
        val colorButton = findViewById<Button>(R.id.change_color_button)

        colorButton.setOnClickListener {
            try {
                clock.setSecondHandColor(colorInput.text.toString())
            } catch (e: IllegalArgumentException) {
                Toast.makeText(
                    this,
                    "Not this one",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        chipGroup.setOnCheckedStateChangeListener { _, _ ->
            val thickness = when (chipGroup.checkedChipId) {
                R.id.thin_chip -> "thin"
                R.id.normal_chip -> "normal"
                else -> "thick"
            }

            clock.setHandsThickness(thickness)
        }
    }
}