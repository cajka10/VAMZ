package com.example.vamzapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNo = findViewById<Button>(R.id.button)
        val btnYes = findViewById<Button>(R.id.button2)
        val btnOk = findViewById<Button>(R.id.button3)
    }

}
