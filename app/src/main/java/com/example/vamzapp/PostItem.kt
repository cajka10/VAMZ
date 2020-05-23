package com.example.vamzapp

import android.app.Dialog
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.row_dashboard.view.*

class PostItem(val post:Post): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.row_dashboard
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val item = viewHolder.itemView
        if ( item.imageView_post_Photo != null) {


            GlideApp.with(item).load(post.photoUrl).centerCrop()
                .into(item.imageView_post_Photo)

            item.textView_post_description.text = post.description
            item.textView_post_userName.text = post.userName


        }
    }

}