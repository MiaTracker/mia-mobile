package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.models.SeriesDetails
import com.nara.mia.mobile.models.Source
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.Vector

interface Series {
    @GET("series")
    suspend fun index(): Response<List<MediaIndex>>

    @GET("series/{id}")
    suspend fun details(@Path("id") seriesId: Int): Response<SeriesDetails>

    @GET("series/{id}/sources")
    suspend fun sources(@Path("id") seriesId: Int): Response<Vector<Source>>

    @GET("series/{id}/on_watchlist")
    suspend fun onWatchlist(@Path("id") seriesId: Int): Response<Boolean>
}