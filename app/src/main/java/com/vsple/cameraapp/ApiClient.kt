package com.vsple.cameraapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var BASE_URL = "https://api.publicapis.org/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
object ApiCall {
    val apiService: ApiInterface by lazy {
        ApiClient.retrofit.create(ApiInterface::class.java)
    }
}