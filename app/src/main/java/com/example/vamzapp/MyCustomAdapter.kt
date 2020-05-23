package com.example.vamzapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

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
    }
}

class CustomViewHolder(val view : View) : RecyclerView.ViewHolder(view){

}