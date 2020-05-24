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
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.dialog_full_screen.*

class FullScreenActivity : AppCompatActivity() {

    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_full_screen)

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

    }

    private fun download(imageName: String) {
        val imageRef = storageReference!!.child("images/posts/$imageName")
        imageRef.downloadUrl.addOnSuccessListener {
            downloadPhoto("test", ".jpg", Environment.DIRECTORY_DCIM, it.toString())
        }

    }

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
