package com.example.vamzapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException
import java.util.*

class ProfileActivity : DashboardActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    private val PICK_IMAGE_REQUEST = 1234
    private var imagePath: Uri? = null
    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth = FirebaseAuth.getInstance()

        supportActionBar?.title = "Môj profil"

        btn_change.setOnClickListener(this)
        btn_confirmPhoto.setOnClickListener(this)
        storage = FirebaseStorage.getInstance()

        storageReference = storage!!.reference
        editText_Email.setText(auth.currentUser?.email)
        editText_userName.setText(auth.currentUser?.displayName)

        if (savedInstanceState != null) {
            editText_userName.setText(savedInstanceState.getString("name"))
            imagePath = Uri.parse(savedInstanceState.getString("imagePath"))

        }

    }

    /**
     * Load photo on start
     *
     */
    override fun onStart() {
        super.onStart()
        if (auth.currentUser?.photoUrl != null) {
            Glide.with(this).load(auth.currentUser?.photoUrl).into(imageView_profilePhoto)
        }
    }

    /**
     * Show file chooser for adding profile photo
     */
    private fun showFileChooser() {
        val intent = Intent()
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.type = "image/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        startActivityForResult(Intent.createChooser(intent, "Vyber obrázok"), PICK_IMAGE_REQUEST)
    }

    /**
     * sets choosed image as imageview and sets url
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST
            && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            imagePath = data.data
            try {
                val tempBitmap = BitmapFactory.decodeFile("drawable/person_icon.jpg");
                imageView_profilePhoto.setImageBitmap(tempBitmap)

            } catch (e: IOException) {
                e.printStackTrace()
                error("Nepodarilo sa nacitat fotku")
            }
        }
    }

    /**
     *After buton is clicked, uploads choosed photo to cloud
     */
    private fun uploadPhoto() {
        if (imagePath != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
            imageView_profilePhoto.setImageBitmap(bitmap)

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Nahrava sa . . .")
            progressDialog.show()
            val imageRef = storageReference!!.child("images/" + UUID.randomUUID().toString())
            imageRef.putFile(imagePath!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Obrazok bol nahraty", Toast.LENGTH_SHORT)
                        .show()
                    imageRef.downloadUrl.addOnSuccessListener {
                        saveUserInfo(it.toString())
                        Glide.with(this).load(it.toString()).into(imageView_profilePhoto)
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
        }
    }

    /**
     *Updating users info, such as photo and name in database but also to Firebase
     */
    private fun saveUserInfo(photoUri: String) {
        var displayName = editText_userName.text.toString()
        val currUser = auth.currentUser

        if (currUser != null) {
            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(Uri.parse(photoUri))
                .build()
            currUser?.updateProfile(updates)
        }
        val ref = FirebaseDatabase.getInstance().getReference("users/")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val user = User(currUser!!.uid, displayName, currUser.email.toString(), photoUri)

            ref.child(currUser.uid).setValue(user)
                .addOnSuccessListener {
                    Log.d("New user", "New User added")

                }
                .addOnFailureListener {
                    Log.d("New Post", "Failed to add new User")
                }
        }


    }

    override fun onClick(p0: View?) {
        if (p0 === btn_change) {
            showFileChooser()
        } else if (p0 === btn_confirmPhoto) {
            uploadPhoto()
        }
    }

    /**
     * Saving name and image path
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putString("name", editText_userName.text.toString().substringAfter(":"))
        outState?.putString("imagePath", imagePath.toString())
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}

