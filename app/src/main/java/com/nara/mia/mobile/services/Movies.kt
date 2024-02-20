package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.models.MovieDetails
import com.nara.mia.mobile.models.SearchResults
import com.nara.mia.mobile.models.Source
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Vector

interface Movies {
    @GET("movies")
    suspend fun index(): Response<List<MediaIndex>>

    @GET("movies/search")
    suspend fun search(@Query("query") query: String): Response<SearchResults>

    @POST("movies")
    suspend fun create(@Query("tmdb_id") tmdbId: Int): Response<Unit>

    @GET("movies/{id}")
    suspend fun details(@Path("id") movieId: Int): Response<MovieDetails>

    @GET("movies/{id}/sources")
    suspend fun sources(@Path("id") movieId: Int): Response<Vector<Source>>

    @GET("movies/{id}/on_watchlist")
    suspend fun onWatchlist(@Path("id") movieId: Int): Response<Boolean>
}