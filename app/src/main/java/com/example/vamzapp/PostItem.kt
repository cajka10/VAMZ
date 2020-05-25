package com.example.vamzapp

import android.content.Context
import android.content.Intent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.dialog_full_screen.*
import kotlinx.android.synthetic.main.row_dashboard.view.*

class PostItem(val post: Post) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.row_dashboard
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val item = viewHolder.itemView
        if (item.imageView_post_Photo != null) {


            GlideApp.with(item).load(post.photoUrl).centerCrop()
                .into(item.imageView_post_Photo)
            item.editText_post_PostTitle.setText(post.postTitle)
            item.textView_post_description.text = post.description
            item.textView_post_userName.text = post.userName
            item.textView_post_numLikes.text = post.numOfLikes.toString()
            getNumberOfLikes(post, viewHolder)

        }

    }

    private fun getNumberOfLikes(post: Post, viewHolder: ViewHolder) {
        val dbRef = FirebaseDatabase.getInstance().getReference()
        val postId = post.postId

        dbRef.child("posts/$postId/likes")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    viewHolder.itemView.textView_post_numLikes.text = p0.childrenCount.toString()
                }

            })
    }

}