package com.example.pract.data.network

import com.example.pract.data.api.ScheduleApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5268/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api: ScheduleApi = retrofit.create(ScheduleApi::class.java)
}