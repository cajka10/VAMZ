package com.example.vamzapp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dialog_full_screen.*
import kotlinx.android.synthetic.main.row_dashboard.view.*


class DashboardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        auth = FirebaseAuth.getInstance()

        val dialog = Dialog(this, android.R.style.Theme_DeviceDefault_Light_DarkActionBar)
        dialog.setContentView(R.layout.dialog_full_screen)

        dashboard_recyclerView.layoutManager = LinearLayoutManager(this)
        dashboard_recyclerView.addItemDecoration(
            DividerItemDecoration(
                dashboard_recyclerView.getContext(),
                DividerItemDecoration.VERTICAL
            )
        )


        fetchPosts()

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
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach{
                    val post = it.getValue(Post::class.java)
                    if (post != null) {
                        adapter.add(PostItem(post!!))
                    adapter.setOnItemClickListener{ item, view ->
//                        val dialog = Dialog(view.context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar)
//                        dialog.setContentView(R.layout.dialog_full_screen)
//                        Glide.with(view!!).load(post.photoUrl)
//                            .into(imageView_fullScreen)
//                        dialog.show()
                    }

                    }
                }
                dashboard_recyclerView.adapter = adapter

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
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()


    }

}