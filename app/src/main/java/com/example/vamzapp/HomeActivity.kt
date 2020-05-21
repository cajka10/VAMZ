package com.example.vamzapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var selectedOption = ""

        when (item?.itemId) {
            R.id.about_us -> {
                selectedOption = "O nás"
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
            R.id.sign_in -> {
                selectedOption = "Prihlásenie"
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            R.id.sign_up -> {
                selectedOption = "Registrácia"
                startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            }
        }
        Toast.makeText(
            this, "Moznost " + selectedOption,
            Toast.LENGTH_SHORT
        ).show()


        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }
}