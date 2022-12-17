package com.asoft.blog.data.remote

import com.google.gson.annotations.SerializedName

class Post {
    @SerializedName("id")
    var id: String? = ""

    @SerializedName("author")
    var author: String? = ""

    @SerializedName("date")
    var date: String? = ""

    @SerializedName("description")
    var description: String? = ""

    @SerializedName("file")
    var file: String? = ""

    @SerializedName("likes")
    var likes: Int? = 0

    @SerializedName("title")
    var title: String? = ""
}