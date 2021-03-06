package com.example.vamzapp

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_new_post.*
import java.io.IOException
import java.util.*

class NewPostActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1234
    private var imagePath: Uri? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var selectedCategory: CategoriesEnum? = null
    private var imageUrl: String = ""
    private var imageName: String = ""

    var myReceiver = object : NetworkChangedReceiver() {
        override fun broadcastResult(connected: Boolean) {
            if (!connected) {
                imageView_newPost_Post.isClickable = connected

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        var intentFilter = IntentFilter()

        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

        registerReceiver(myReceiver, intentFilter)

        auth = FirebaseAuth.getInstance()
        supportActionBar?.title = "Vytvor nový príspevok"


        btn_newPost_Post.setOnClickListener(this)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        textView_newPost_description.setText(textView_newPost_description.text.toString())

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val categories =
            arrayOf(CategoriesEnum.FUNNY, CategoriesEnum.AWESOME, CategoriesEnum.ANIMALS)
        val arrayAdapter = ArrayAdapter<CategoriesEnum>(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            categories
        )

        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCategory = categories[p2]
            }

        }

        imageView_newPost_Post.setOnClickListener(this)


    }

    /**
     *Show file chooser for adding photo
     */
    private fun showFileChooser() {
        //otvori nam prehliadac obrázkov
        val intent = Intent()
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.type = "image/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        startActivityForResult(Intent.createChooser(intent, "Vyber obrázok"), PICK_IMAGE_REQUEST)
    }

    /**
     *After choosing photo trigger uploading to cloud
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
                uploadPhoto(imagePath)

            } catch (e: IOException) {
                e.printStackTrace()
                error("Nepodarilo sa nacitat fotku")
            }
        }
    }

    /**
     *Uploading choosen photo to cloud
     */
    private fun uploadPhoto(parImagePath: Uri?) {
        //nahrá fotku na cloud a zobrazí ju v našom okne
        btn_newPost_Post.isClickable = false
        if (parImagePath != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
            imageView_newPost_Post.setImageBitmap(bitmap)

            val progressDialog = ProgressDialog(this)
            imageName = UUID.randomUUID().toString()
            progressDialog.setTitle("Nahrava sa . . .")
            val imageRef = storageReference!!.child("images/posts/" + imageName)
            imageRef.putFile(imagePath!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener {
                        imageUrl = it.toString()
                        Glide.with(this).load(imageUrl).into(imageView_newPost_Post)
                        Toast.makeText(
                            applicationContext,
                            "Obrazok bol nahraty do cloudu, môžeš ho pridať",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        btn_newPost_Post.isClickable = true
                    }
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Obrázok nebol nahratý", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    /**
     *Saving post to our database
     */
    private fun savePostToDatabase(imageUrl: String, imageName: String) {
        //uloží Post do databázy s jeho udajmi aj s udajmi o userovi, po uspesnom pridani posle notifikáciu
        val userName = auth.currentUser?.displayName.toString()
        val description = textView_newPost_description.text.toString()
        val postTitle = textView_newPost_postTitle.text.toString()
        val uid = auth.currentUser?.uid.toString()
        val ref = FirebaseDatabase.getInstance().getReference("posts")
        val id = ref.push()?.key

        if (id != null) {
            val post = Post(
                id,
                imageName,
                postTitle,
                userName,
                uid,
                description,
                imageUrl,
                0,
                selectedCategory!!
            )
            ref.child(id).setValue(post)
                .addOnSuccessListener {
                    sendNotification()
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Log.d("Nový príspevok", "Pridanie zlyhalo")
                }
        }

    }

    /**
     *Send notofication if post is succesfully added to database
     */
    private fun sendNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "postAddedSuccesfuly",
                "n",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

        } else {
            return
        }

        var builder = NotificationCompat.Builder(this, "postAddedSuccesfuly")
            .setContentText("VAMZ")
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
            .setAutoCancel(true)
            .setContentText("Pridal si nový príspevok.")
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(999, builder.build())
    }

    override fun onClick(p0: View?) {
        if (p0 === imageView_newPost_Post) {
            showFileChooser()
        }
        //pokial nie je pridaný obrázok nie je možné kliknut tlačidlo
        if (p0 === btn_newPost_Post) {
            if (imagePath != null) {
                savePostToDatabase(imageUrl, imageName)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.getItemId()
        if (itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

}
