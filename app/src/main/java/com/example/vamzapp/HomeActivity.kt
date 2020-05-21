package com.example.vamzapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FileDownloadTask
import java.io.File


class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        auth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null ){
            finish()
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        menu!!.getItem(menu.size() - 1 ).setVisible(false)
        menu!!.getItem(2).setVisible(false)
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