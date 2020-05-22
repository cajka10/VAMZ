package com.example.vamzapp

import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class Post(userName : String, description : String, photoUrl : String): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.row_dashboard
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
    }
}