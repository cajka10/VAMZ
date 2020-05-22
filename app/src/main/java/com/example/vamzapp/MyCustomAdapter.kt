package com.example.vamzapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_dashboard.view.*

class MyCustomAdapter: RecyclerView.Adapter<CustomViewHolder>(){

    val postTitles = listOf<String>("OMG", "What", "Really")
    override fun getItemCount(): Int {
        return  postTitles.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.row_dashboard, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val postTitle = postTitles.get(position)
        holder.view.textView_dash_postTitle.text = postTitle
    }
}

class CustomViewHolder(val view : View) : RecyclerView.ViewHolder(view){

}