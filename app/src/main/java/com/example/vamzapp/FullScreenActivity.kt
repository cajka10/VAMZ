package com.example.vamzapp

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.dialog_full_screen.*

class FullScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null

    companion object like {
        var id: String = ""
        var userName: String = ""
        var uid: String = ""

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_full_screen)

        auth = FirebaseAuth.getInstance()

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        val post = intent.getParcelableExtra<Post>(DashboardActivity.POST_KEY)
        GlideApp.with(this)
            .load(post.photoUrl)
            .into(imageView_fullScreen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        textView_screen_description.setText(post.description)

        btn_screen_download.setOnClickListener {
            downloadPhoto("test", ".jpg", Environment.DIRECTORY_DCIM, post.photoUrl)
        }
        btn_screen_like.setOnClickListener {
            updateLikes(post)
        }
        textView_screen_postTitle.setText(post.postTitle)
        getNumberOfLikes(post)
        getUserInfo(post)
    }

    /**
     *
     */
    private fun updateLikes(post: Post) {
        val postId = post.postId
        val uid = auth.currentUser?.uid.toString()
        val dbRef = FirebaseDatabase.getInstance().getReference()
        val id = dbRef.push()?.key
        like.userName = auth.currentUser?.displayName.toString()
        if (id != null) {
            like.id = id
        }
        like.uid = uid

        dbRef.child("posts/$postId/likes/$uid").setValue(like).addOnSuccessListener {
            Toast.makeText(this, "Prispevok sa vam páči", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Prispevok sa nepodaril oznacit paci sa mi", Toast.LENGTH_SHORT)
                .show()

        }

    }

    /**
     *
     */
    private fun getNumberOfLikes(post: Post) {
        val dbRef = FirebaseDatabase.getInstance().getReference()
        val postId = post.postId

        dbRef.child("posts/$postId/likes")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    textView_screen_numOfLikes.text = (p0.childrenCount).toString() + " ľuďom"
                }

            })
    }

    /**
     *
     */
    private fun getUserInfo(post: Post) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users/")
        var name = ""
        val uid = post.uid
        var url = ""
        dbRef.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    setUserPhoto(p0.child("url").getValue(String :: class.java)!!)
                    textView_userName.setText(p0.child("userName").getValue(String :: class.java)!!)



                }

            })

    }

    /**
     *
     */
    private fun setUserPhoto(url: String) {
        Glide.with(this).load(url).into(imageView_screen_user)
    }

    /**
     *
     */
    private fun downloadPhoto(
        fileName: String,
        extension: String,
        destination: String,
        photoUrl: String?
    ) {
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(photoUrl)
        val request = DownloadManager.Request(uri)

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(this, destination, fileName + extension)

        downloadManager.enqueue(request)

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
}

