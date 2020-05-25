package com.example.vamzapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        auth = FirebaseAuth.getInstance()
        supportActionBar?.title = "Moje memes"

    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null ){
            finish()
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        if (auth.currentUser == null ) {

            menu?.setGroupVisible(R.id.menu_offline, true)
            menu?.setGroupVisible(R.id.menu_online, false)
        }
        return super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menuItem_about_us -> {
                startActivity(Intent(this, AboutUs::class.java))
                finish()
            }
            R.id.menuItem_sign_in -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            R.id.menuItem_sign_up -> {
                startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}