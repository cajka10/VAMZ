package com.example.vamzapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        auth = FirebaseAuth.getInstance()
        val listView = findViewById<ListView>(R.id.homepage_listview)

        listView.adapter = MyCustomAdapter(this)

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        menu!!.get(2).setVisible(true)
        menu!!.get(3).setVisible(false)
        menu!!.get(4).setVisible(false)
        menu!!.getItem(menu.size() - 1 ).setVisible(true)


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
            R.id.profile -> {
                selectedOption = "Prihlásenie"
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
            R.id.logOut -> {
                selectedOption = "Odhlásenie"
                logOut()
            }
        }
        Toast.makeText(
            this, "Moznost " + selectedOption,
            Toast.LENGTH_SHORT
        ).show()


        return super.onOptionsItemSelected(item)
    }
    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null ){
            finish()
            startActivity(Intent(this, HomeActivity::class.java))
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
        startActivity(Intent(this, HomeActivity::class.java))
        finish()


    }
    private class MyCustomAdapter(context: Context): BaseAdapter(){

        private val mContext:Context
        private val names = arrayListOf<String>("Boi", "Nechce sa", "Mato Palcik", "NechceSaMi err", "Fero Sefcik")

        init {
            mContext = context
        }

        override fun getCount(): Int {
            return names.size
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getItem(position: Int): Any {
            return "Test string"

        }
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val row  = layoutInflater.inflate(R.layout.row_dashboard, viewGroup, false)

            val namesTextView = row.findViewById<TextView>(R.id.textView_postTitle)
            namesTextView.text = names.get(position)

            val postContent = row.findViewById<TextView>(R.id.textView_postContent)
            postContent.text = "Row number: $position"
            return row
        }
    }
}