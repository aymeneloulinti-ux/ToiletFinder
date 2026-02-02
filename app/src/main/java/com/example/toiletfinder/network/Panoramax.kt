package com.example.toiletfinder.network

import com.squareup.moshi.Json

data class PanoramaxResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: Properties
)

data class Properties(
    @Json(name = "thumb_2048_url") val imageUrl: String
)
