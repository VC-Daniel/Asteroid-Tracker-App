package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.Constants.CALL_TIMEOUT
import com.udacity.asteroidradar.Constants.CONNECTION_TIMEOUT
import com.udacity.asteroidradar.Constants.INTERACTION_TIMEOUT
import com.udacity.asteroidradar.ImageOfTheDay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/** Used to parse api data such as the image of the day api */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/** Set the timeouts to support slower internet connections
This is based off of this tutorial https://howtodoinjava.com/retrofit2/connection-timeout-exception/ */
var httpClient = OkHttpClient.Builder()
    .callTimeout(CALL_TIMEOUT, TimeUnit.MINUTES)
    .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
    .readTimeout(INTERACTION_TIMEOUT, TimeUnit.SECONDS)
    .writeTimeout(INTERACTION_TIMEOUT, TimeUnit.SECONDS)
    .build()

// Configure to work with the NASA api
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(httpClient)
    .build()

/** Provides calls to get asteroid data and the NASA image of the day */
interface AsteroidApiService {
    /** Returns the JSON data for asteroids for 7 days from the specified day[startDate]
     * from the NASA api using the provided [apiKey]*/
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("api_key") apiKey: String
    ): String

    /** Returns information about the image of the day from the NASA api using the provided [apiKey]*/
    @GET("planetary/apod")
    suspend fun getImageOfTheDay(
        @Query("api_key") apiKey: String
    ): ImageOfTheDay
}

/** An object configured to interact with the NASA api */
object AsteroidApi {

    val retrofitService: AsteroidApiService by lazy {

        retrofit.create(AsteroidApiService::class.java)
    }
}