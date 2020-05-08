package org.chupik.redditringtest

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface RedditService {

    @GET("top")
    fun posts(
            @Query("limit") limit: Int,
            @Query("after") after: String,
            @Header("User-Agent") userAgent: String,
            @Header("Authorization") auth: String
    ) : Call<Top>

    @FormUrlEncoded
    @POST("https://www.reddit.com/api/v1/access_token")
    fun token(
            @FieldMap body: Map<String, String>,
            @Header("Authorization") credential: String) : Call<AuthResult>
}



class Top(val data: Data) {
    fun getPosts() : List<Post> = data.children.map { it.post }
}

class Data(val children: List<Child>)

class Child(@SerializedName("data") val post: Post)

class Preview(val enabled: Boolean)


class AuthResult(
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("expires_in") val expiresIn: Int) {

    val expiresAt : Long
    get() = Calendar.getInstance().run {
        add(Calendar.SECOND, expiresIn)
        timeInMillis
    }
}