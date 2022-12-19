package com.asoft.blog.data.remote

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName

@Entity
data class Post(
    @SerializedName("author")
    var author: String? = "",

    @SerializedName("date")
    var date: String? = "",

    @SerializedName("description")
    var description: String? = "",

    @SerializedName("file")
    var file: String? = "",

    @SerializedName("likes")
    var likes: Int? = 0,

    @SerializedName("title")
    var title: String? = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}



