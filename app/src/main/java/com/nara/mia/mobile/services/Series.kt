package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.MediaIndex
import retrofit2.Response
import retrofit2.http.GET

interface Series {
    @GET("series")
    suspend fun index(): Response<List<MediaIndex>>
}