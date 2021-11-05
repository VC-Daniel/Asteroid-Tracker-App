package com.udacity.asteroidradar

import com.squareup.moshi.Json

/** Represents the image of the day from NASA. [mediaType] contains if it is an image or another
 * type of media. [title] contains a short description of the media and [url] is the location
 * of the media data */
data class ImageOfTheDay(
    @Json(name = "media_type") val mediaType: String, val title: String,
    val url: String
)