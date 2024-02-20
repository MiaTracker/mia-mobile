package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.MediaIndex
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Watchlist {
    @GET("watchlist")
    suspend fun index(): Response<List<MediaIndex>>

    @GET("watchlist/search")
    suspend fun search(@Query("query") query: String): Response<List<MediaIndex>>
}