package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.models.SeriesDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Series {
    @GET("series")
    suspend fun index(): Response<List<MediaIndex>>

    @GET("series/{id}")
    suspend fun details(@Path("id") seriesId: Int): Response<SeriesDetails>
}