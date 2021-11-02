package com.udacity.asteroidradar.api

import android.content.Context
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.R
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()


interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("api_key") apiKey: String
    ): String
}

object AsteroidApi {

    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java) }
}