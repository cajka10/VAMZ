package com.example.vamzapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Post(val postId:String, val postTitle: String, val userName : String, val description : String, val photoUrl : String) : Parcelable {
    constructor() : this("", "", "", "", "")
}