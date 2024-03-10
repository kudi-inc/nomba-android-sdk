package com.nomba.wraith.core.api.services

import android.telecom.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {

    @GET("posts/{id}")
    fun getPostById(@Path("id") postID: Int): Call<Post>

    @POST("/v1/auth/token/issue")
    fun obtainAccessToken()
}