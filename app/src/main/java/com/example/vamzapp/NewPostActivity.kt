package com.example.vamzapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_new_post.*
import java.io.IOException
import java.util.*

class NewPostActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private var imageUrl: String = ""
    private val PICK_IMAGE_REQUEST = 1234
    private var imagePath: Uri? = null
    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        auth = FirebaseAuth.getInstance()
        supportActionBar?.title = "Vytvor nový príspevok"

        btn_newPost_Post.setOnClickListener(this)
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        textView_newPost_description.setText(textView_newPost_description.text.toString())

    }

    private fun savePostToDatabase(imageUrl: String) {
        val userName = auth.currentUser?.displayName.toString()
        val description = textView_newPost_description.text.toString()
        val postTitle = textView_newPost_postTitle.text.toString()

        val ref = FirebaseDatabase.getInstance().getReference("posts")
        val id = ref.push()?.key

        if (id != null) {
            val post = Post(id, postTitle, userName, description, imageUrl)
            ref.child(id).setValue(post)
                .addOnSuccessListener {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Log.d("New Post", "Failed to add new post")
                }
        }

    }

    private fun showFileChooser() {
        val intent = Intent()
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.type = "image/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        startActivityForResult(Intent.createChooser(intent, "Vyber obrázok"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST
            && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            imagePath = data.data
            try {
                uploadPhoto(imagePath)

            } catch (e: IOException) {
                e.printStackTrace()
                error("Nepodarilo sa nacitat fotku")
            }
        }
    }

    private fun uploadPhoto(parImagePath: Uri?) {
        if (parImagePath != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
            imageView_newPost_Post.setImageBitmap(bitmap)

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Nahrava sa . . .")
//            progressDialog.show()
            val imageRef = storageReference!!.child("images/posts/" + UUID.randomUUID().toString())
            imageRef.putFile(imagePath!!)
                .addOnSuccessListener {
                    //                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Obrazok bol nahraty", Toast.LENGTH_SHORT)
                        .show()
                    imageRef.downloadUrl.addOnSuccessListener {
                    }
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Obrazok nebol nahraty", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    progressDialog.setMessage("Nahralo sa " + progress.toInt() + "%")
                }
            savePostToDatabase(parImagePath.toString())
        }
    }

    override fun onClick(p0: View?) {
        if (p0 === btn_newPost_Post) {
            showFileChooser()
        }
    }

}
