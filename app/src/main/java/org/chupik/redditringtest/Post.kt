package org.chupik.redditringtest

import com.google.gson.annotations.SerializedName

class Post(
        val name: String,
        val title: String,
        val author: String,
        @SerializedName("created_utc")
        val date: Long,
        val thumbnail: String?,
        @SerializedName("url")
        val fullImage: String?,
        @SerializedName("num_comments")
        val commentsNumber: Long,
        val permalink: String) {

    var preview: Preview? = null

    val dateInMiliseconds: Long
        get() = date * 1000

    val fullPermaLink: String
        get() = "https://www.reddit.com$permalink"

    val fullImageIfEnabled: String?
        get() = if (preview?.enabled == true) fullImage else null

}