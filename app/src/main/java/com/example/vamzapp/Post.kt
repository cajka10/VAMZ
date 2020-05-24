package com.example.vamzapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Post(val postId:String, val imageName: String, val postTitle: String, val userName : String, val description : String, val photoUrl : String, var numOfLikes : Int) : Parcelable {
    constructor() : this("", "", "", "", "", "", 0)

    fun addLike(){
        numOfLikes += 1
    }


}