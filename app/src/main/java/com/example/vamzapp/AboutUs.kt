package com.example.vamzapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about_us.*
import java.lang.Exception

class AboutUs : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "O nás"

        setContentView(R.layout.activity_about_us)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        var fileName = "about_us_file.txt"
        var tempString = ""

        try {
            application.assets.open(fileName).apply {
                tempString = this.readBytes().toString(Charsets.UTF_8)
            }.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        textView_aboutUs.setText(tempString)


    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}
