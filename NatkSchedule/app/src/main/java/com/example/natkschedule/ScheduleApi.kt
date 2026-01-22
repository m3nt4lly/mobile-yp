package com.example.natkschedule

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleApi {
    @GET("api/schedule/groups")
    suspend fun getGroups(): List<String>

    @GET("api/schedule/group/{groupName}")
    suspend fun getSchedule(
        @Path("groupName") groupName: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): List<ScheduleByDateDto>
}

object RetrofitInstance {
    private const val BASE_URL = "https://m3nt4lly.ru:5001/"

    val api: ScheduleApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ScheduleApi::class.java)
    }
}
