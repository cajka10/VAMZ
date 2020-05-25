package com.example.vamzapp

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
class User(val uid : String, val userName : String, val email : String, val url : String) :
    Parcelable {
    @RequiresApi(Build.VERSION_CODES.O)
    constructor() : this("","","", "")
}