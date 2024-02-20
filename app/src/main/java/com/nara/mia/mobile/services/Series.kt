package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.models.SearchResults
import com.nara.mia.mobile.models.SeriesDetails
import com.nara.mia.mobile.models.Source
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Vector

interface Series {
    @GET("series")
    suspend fun index(): Response<List<MediaIndex>>

    @GET("series/search")
    suspend fun search(@Query("query") query: String): Response<SearchResults>

    @POST("series")
    suspend fun create(@Query("tmdb_id") tmdbId: Int): Response<Int>

    @GET("series/{id}")
    suspend fun details(@Path("id") seriesId: Int): Response<SeriesDetails>

    @GET("series/{id}/sources")
    suspend fun sources(@Path("id") seriesId: Int): Response<Vector<Source>>

    @GET("series/{id}/on_watchlist")
    suspend fun onWatchlist(@Path("id") seriesId: Int): Response<Boolean>
}