package com.vsple.cameraapp

import com.vsple.cameraapp.modes.DataModel
import com.vsple.cameraapp.modes.Entry
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET("entries")
    fun getDATA(): Call<DataModel>
}