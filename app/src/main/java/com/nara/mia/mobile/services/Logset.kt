package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.Logset
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Logset {
    @POST("logset")
    suspend fun create(@Body logset: Logset): Response<Void>
}