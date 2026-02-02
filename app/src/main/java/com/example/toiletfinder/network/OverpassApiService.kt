package com.example.toiletfinder.network

import retrofit2.http.GET
import retrofit2.http.Query

interface OverpassApiService {
    @GET("api/interpreter")
    suspend fun getToilets(@Query("data") query: String): OverpassResponse

    @GET("api/v1/pictures")
    suspend fun getToiletImage(@Query("bbox") bbox: String): PanoramaxResponse
}
