package com.example.lab1variant5

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val normalizer = ListNormalizer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputList = findViewById<EditText>(R.id.inputList)
        val normalizeButton = findViewById<Button>(R.id.normalizeButton)
        val resultList = findViewById<TextView>(R.id.resultList)

        val numbers = normalizer.createList()
        inputList.setText(normalizer.listToString(numbers))

        normalizeButton.setOnClickListener {
            resultList.text = ""

            try {
                val enteredNumbers = inputList.text.toString()
                    .split(",")
                    .map { it.trim().toInt() }

                val normalizedNumbers = normalizer.normalize(enteredNumbers)
                resultList.text = normalizer.listToString(normalizedNumbers)
            } catch (exception: NumberFormatException) {
                Toast.makeText(
                    this,
                    "Enter numbers separated by commas",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}