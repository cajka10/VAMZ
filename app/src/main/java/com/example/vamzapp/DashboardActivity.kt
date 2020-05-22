package com.example.vamzapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        auth = FirebaseAuth.getInstance()

        dashboard_recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = GroupAdapter<ViewHolder>()

        dashboard_recyclerView.adapter = adapter

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        menu?.setGroupVisible(R.id.menu_offline, false)
        menu?.setGroupVisible(R.id.menu_online, true)

        return super.onCreateOptionsMenu(menu)
        return true
    }

    private fun fetchPosts(){
        val ref = FirebaseDatabase.getInstance().getReference("/posts")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    val user = it.getValue(Post::class.java)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menuItem_dashBoard -> {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            R.id.menuItem_new_post -> {
                val intent = Intent(this, NewPostActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            R.id.menuItem_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            R.id.menuItem_logOut -> {
                logOut()
            }
        }


        return super.onOptionsItemSelected(item)
    }
    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null ){
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    fun logOut() {
        Toast.makeText(
            baseContext, "Odhlasujem",
            Toast.LENGTH_SHORT
        ).show()

        Log.d("DashBoard", "Odhlasujem užívatela" + auth.currentUser?.email )
        Thread.sleep(2000)
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, ProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()


    }

}