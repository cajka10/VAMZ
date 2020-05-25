package com.example.vamzapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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


open class DashboardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var selectedCategory: CategoriesEnum? = null


    private var ref: FirebaseDatabase? = null
    private val myReceiver = object : NetworkChangedReceiver() {
        override fun broadcastResult(connected: Boolean) {
        }
    }

    companion object {
        val POST_KEY = "POST_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        auth = FirebaseAuth.getInstance()

        ref = FirebaseDatabase.getInstance()
        supportActionBar?.title = "Nástenka"

        var intentFilter = IntentFilter()

        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

        registerReceiver(myReceiver, intentFilter)

        dashboard_recyclerView.layoutManager = LinearLayoutManager(this)
        dashboard_recyclerView.addItemDecoration(
            DividerItemDecoration(
                dashboard_recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        editText_dash_searchPost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(newText: Editable?) {
            }

            override fun beforeTextChanged(newText: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(newText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchedText = editText_dash_searchPost.text.toString()
                findPostByUserName("userName", searchedText)
            }
        })

        val categories =
            arrayOf(CategoriesEnum.FUNNY, CategoriesEnum.AWESOME, CategoriesEnum.ANIMALS)
        val arrayAdapter = ArrayAdapter<CategoriesEnum>(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            categories
        )

        spinner_dash.adapter = arrayAdapter

        spinner_dash.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCategory = categories[p2]
                findPostByUserName("category", selectedCategory.toString())
            }

        }


        fetchPosts()
        sendNotificationIfChanged()
    }

    /**
     *method for retrieving data from database and loading into reczclerview
     */
    private fun fetchPosts() {
        //načíta všetky príspevky z databázy do nášho recyclerview
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

    /**
     *Method for finding posts in database
     * @param myTag represents child od post in database
     * @param searchedText represents value of tag we are seraching for
     */
    private fun findPostByUserName(myTag: String, searchedText: String) {
        //Najdenie všetkých príspevkov, podľa mena užívateľa

        val tempRef = ref?.getReference("/posts")
        val adapter = GroupAdapter<ViewHolder>()

        if (tempRef != null) {
            tempRef.orderByChild(myTag).startAt(searchedText)
                .endAt(searchedText + "\uf8ff")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        p0.children.forEach {
                            val post = it.getValue(Post::class.java)
//                            latestPosts[it.key!!] = post
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


    /**
     *Method is looking for change in post datac
     */
    private fun sendNotificationIfChanged() {
        //ked sa zmení počet lajkov v tabulke zasle notifikáciu
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

    /**
     * Method for pushing notification to device for author of post
     * triggers if someone likes user post
     * I
     */
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
            .setContentText("Pribudol ti lajk na jednom z tvojich prispevkov.")
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(999, builder.build())
    }

    /**
     * navigatin in projekt by menu items
     *
     * @param item
     * @return
     */
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

    /**
     * create menu and sets visible group of online options
     *
     * @param menu
     * @return
     */
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


    /**
     *Logs out current logged user
     */
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)

    }

}