package com.example.vamzapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.row_dashboard.*
import kotlinx.android.synthetic.main.row_dashboard.view.*


open class DashboardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private var ref: FirebaseDatabase? = null

    companion object {
        val POST_KEY = "POST_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        auth = FirebaseAuth.getInstance()

        ref = FirebaseDatabase.getInstance()

        dashboard_recyclerView.layoutManager = LinearLayoutManager(this)
        dashboard_recyclerView.addItemDecoration(
            DividerItemDecoration(
                dashboard_recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        editText_dash_searchPost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchedText = editText_dash_searchPost.text.toString()
                findPostByTitle(searchedText)
            }
        })
//        textView_post_userName.setOnClickListener{
//            selectProfile(textView_post_userName.text.toString(), it.context)
//        }

        fetchPosts()
        sendNotificationIfChanged()
    }

    private fun fetchPosts() {
        var tempRef = ref?.getReference("/posts")
        tempRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    val post = it.getValue(Post::class.java)
                    if (post != null) {
                        adapter.add(PostItem(post!!))
                        adapter.setOnItemClickListener { item, view ->
                            val postItem = item as PostItem
                            val intent = Intent(view.context, FullScreenActivity::class.java)
                            intent.putExtra(POST_KEY, postItem.post)
                            startActivity(intent)
                        }
                    }
                }
                dashboard_recyclerView.adapter = adapter

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun findPostByTitle(searchedText: String) {
        val tempRef = ref?.getReference("/posts")
        if (tempRef != null) {
            tempRef.orderByChild("userName").startAt(searchedText)
                .endAt(searchedText + "\uf8ff")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val adapter = GroupAdapter<ViewHolder>()
                        p0.children.forEach {
                            val post = it.getValue(Post::class.java)
                            if (post != null) {
                                adapter.add(PostItem(post!!))
                                adapter.setOnItemClickListener { item, view ->
                                    val postItem = item as PostItem
                                    val intent =
                                        Intent(view.context, FullScreenActivity::class.java)
                                    intent.putExtra(POST_KEY, postItem.post)
                                    startActivity(intent)
                                }
                            }
                        }
                        dashboard_recyclerView.adapter = adapter

                    }

                })
        }
    }

//    private fun selectProfile(userName : String, context : Context?) {
//        val dbRef = FirebaseDatabase.getInstance().getReference("/users")
//
//        dbRef.orderByChild("userName").startAt(userName).endAt(userName + "\uf8ff")
//            .addValueEventListener( object : ValueEventListener{
//                override fun onCancelled(p0: DatabaseError) {
//                }
//
//                override fun onDataChange(p0: DataSnapshot) {
//                    val user = p0.getValue(User :: class.java)
//                    val intent = Intent(context, ProfileActivity::class.java)
//                    intent.putExtra("USER_KEY", user)
//                }
//
//            })
//    }



    private fun sendNotificationIfChanged() {
        val uid = auth.currentUser?.uid.toString()
        var tempRef = ref?.getReference()?.child("/posts")?.orderByChild("uid")?.startAt(uid)
            ?.endAt(uid + "\uf8ff")

        tempRef?.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                sendNotification()
            }
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }


        })

    }

    private fun sendNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("n", "n", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

        } else {
            return
        }

        var builder = NotificationCompat.Builder(this, "n")
            .setContentText("VAMZ")
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
            .setAutoCancel(true)
            .setContentText("Nový príspevok na nástenke v case: " + System.currentTimeMillis())
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(999, builder.build())
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        menu?.setGroupVisible(R.id.menu_offline, false)
        menu?.setGroupVisible(R.id.menu_online, true)

        return super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
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

        Log.d("DashBoard", "Odhlasujem užívatela" + auth.currentUser?.email)
        Thread.sleep(500)
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()


    }

}