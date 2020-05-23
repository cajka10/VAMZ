package com.example.vamzapp

class Post(val postId:String, val postTitle: String, val userName : String, val description : String, val photoUrl : String) {
    constructor() : this("", "", "", "", "")
}