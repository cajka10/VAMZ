package com.example.vamzapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    private val PICK_IMAGE_REQUEST = 1234
    private var imagePath: Uri? = null
    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth = FirebaseAuth.getInstance()

        btn_change.setOnClickListener(this)
        btn_confirmPhoto.setOnClickListener(this)
        storage = FirebaseStorage.getInstance()

        storageReference = storage!!.reference
        editText_Email.setText(editText_Email.text.toString() + auth.currentUser?.email)
        editText_userName.setText("Meno: " + auth.currentUser?.displayName)
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser?.photoUrl != null) {
            Glide.with(this).load(auth.currentUser?.photoUrl).into(imageView_profilePhoto)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        menu?.setGroupVisible(R.id.menu_offline, false)
        menu?.setGroupVisible(R.id.menu_online, true)
        return super.onCreateOptionsMenu(menu)
        return true
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

            }
        }

        return super.onOptionsItemSelected(item)
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
                val tempBitmap = BitmapFactory.decodeFile("drawable/person_icon.jpg");
                imageView_profilePhoto.setImageBitmap(tempBitmap)

            } catch (e: IOException) {
                e.printStackTrace()
                error("Nepodarilo sa nacitat fotku")
            }
        }
    }

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

    private fun saveUserInfo(photoUri: String) {
        var  displayName  = editText_userName.text.toString().substringAfter(":")

        val currUser = auth.currentUser

        if (currUser != null) {
            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(Uri.parse(photoUri))
                .build()
            currUser?.updateProfile(updates)
        }
    }

    override fun onClick(p0: View?) {
        if (p0 === btn_change) {
            showFileChooser()
        } else if (p0 === btn_confirmPhoto) {
            uploadPhoto()
        }
    }
}

class User(val uid:String, val username:String, val profileImage:String)