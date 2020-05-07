package org.chupik.redditringtest

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RedditService {

    @GET("top")
    fun posts(
            @Query("limit") limit: Int,
            @Query("after") after: String,
            @Header("User-Agent") userAgent: String,
            @Header("Authorization") auth: String
    ) : Call<Top>
}

class Top(val data: Data) {
    fun getPosts() : List<Post> = data.children.map { it.post }
}

class Data(val children: List<Child>)

class Child(@SerializedName("data") val post: Post)

class Preview(val enabled: Boolean)

