package com.example.toiletfinder.network

import retrofit2.http.GET
import retrofit2.http.Query

interface PanoramaxApiService {
    @GET("api/v1/pictures")
    suspend fun getToiletImage(@Query("bbox") bbox: String): PanoramaxResponse
}
