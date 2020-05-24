package com.example.vamzapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.LocalDateTime


class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener{
            signUpUser()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.getItemId()
        if (itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    fun signUpUser(){
        if (textEditEmail.text.toString().isEmpty()){
            textEditEmail.error = "Zadaj email"
            textEditEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(textEditEmail.text.toString()).matches()){
            textEditEmail.error = "Zadaj spravny email"
            textEditEmail.requestFocus()
            return

        }
        if (textEditPswd.text.toString().isEmpty()){
            textEditPswd.error = "Zadaj heslo"
            textEditPswd.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(textEditEmail.text.toString(), textEditPswd.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    savePostToDatabase()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(baseContext, "Registrácia zlyhala.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun savePostToDatabase() {
        val tempUserName = auth.currentUser?.displayName.toString()
        val tempEmail = textEditEmail.text.toString()
        val id = auth.currentUser?.uid

        val ref = FirebaseDatabase.getInstance().getReference("users")

        if (id != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val user = User(id, tempUserName, tempEmail)

            ref.child(id).setValue(user)
                .addOnSuccessListener {
                    Log.d("New user", "New User added")

                }
                .addOnFailureListener {
                    Log.d("New Post", "Failed to add new User")
                }
            }
        }

    }
}
