package com.example.toiletfinder.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OverpassApiService {
    @FormUrlEncoded
    @POST("interpreter")
    suspend fun getToilets(@Field("data") query: String): OverpassResponse
}
