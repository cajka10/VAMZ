package com.example.vamzapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener{
            signUpUser()
        }
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
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Registrácia zlyhala.",
                        Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }
}
