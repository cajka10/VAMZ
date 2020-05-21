package com.example.vamzapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity(), View.OnClickListener{

    private val PICK_IMAGE_REQUEST = 1234
    private var imagePath : Uri? = null
    internal  var storage:FirebaseStorage? = null
    internal var storageReference:StorageReference?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        btn_change.setOnClickListener(this)
        btn_confirmPhoto.setOnClickListener(this)
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

    }


    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Vyber obrázok"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST
            && resultCode == Activity.RESULT_OK
            && data != null && data.data != null){
            imagePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
                imageView_profilePhoto.setImageBitmap(bitmap)
            }catch (e:IOException){
                e.printStackTrace()
                error("Nepodarilo sa nacitat fotku")
            }
        }
    }
    private fun uploadPhoto(){
        if (imagePath != null){
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Nahrava sa . . .")
            progressDialog.show()
            val imageRef = storageReference!!.child("images/" + UUID.randomUUID().toString())
            imageRef.putFile(imagePath!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Obrazok bol nahraty", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Obrazok nebol nahraty", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot -> val progress = 100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount
                progressDialog.setMessage("Nahralo sa" + progress.toInt() + "%")}

        }

    }

    override fun onClick(p0: View?) {

        if (p0 === btn_change){
            showFileChooser()
        }
        else if(p0 === btn_confirmPhoto){
            uploadPhoto()
        }
    }
}