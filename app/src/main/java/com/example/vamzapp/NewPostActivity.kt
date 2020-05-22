package com.example.vamzapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_new_post.*
import java.util.*

class NewPostActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private var imageUrl:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        auth = FirebaseAuth.getInstance()
        supportActionBar?.title = "Vytvor nový príspevok"

        btn_newPost_Post.setOnClickListener(this)


    }
    private fun savePostToDatabase(imageUrl:String){
        val userName = auth.currentUser?.displayName.toString()
        val description = textView_newPost_description.text.toString()
        val postName = UUID.randomUUID().toString()

        val ref = FirebaseDatabase.getInstance().getReference("/posts/$postName")


        val post = Post(userName, description, imageUrl)

        ref.setValue(post)
            .addOnSuccessListener {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener{
                Log.d("New Post", "Filed tu add new post")
            }

    }

    override fun onClick(p0: View?) {
        if (p0 === btn_newPost_Post){
            savePostToDatabase(imageUrl)
        }
    }

}
