package com.nomba.wraith.core.api

import android.util.Log
import com.nomba.wraith.core.api.services.APIService
import com.nomba.wraith.core.dev_mode
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient{
    private val BASE_URL: String = if (dev_mode) "https://sandbox.nomba.com" else "https://api.nomba.com"
    val retrofit: Retrofit by lazy {
        Log.d("BASE_URL", BASE_URL)
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