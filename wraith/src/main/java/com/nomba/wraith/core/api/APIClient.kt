package com.nomba.wraith.core.api

import com.nomba.wraith.core.api.services.APIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient{
    private const val BASE_URL = "https://sandbox.nomba.com"
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object APIClient {
    val apiService: APIService by lazy {
        RetrofitClient.retrofit.create(APIService::class.java)
    }

}