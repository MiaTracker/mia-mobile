package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.GenreCreate
import com.nara.mia.mobile.models.Log
import com.nara.mia.mobile.models.LogCreate
import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.models.MovieDetails
import com.nara.mia.mobile.models.SearchResults
import com.nara.mia.mobile.models.Source
import com.nara.mia.mobile.models.SourceCreate
import com.nara.mia.mobile.models.TagCreate
import com.nara.mia.mobile.models.TitleCreate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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
    suspend fun create(@Query("tmdb_id") tmdbId: Int): Response<Int>

    @GET("movies/{id}")
    suspend fun details(@Path("id") movieId: Int): Response<MovieDetails>

    @GET("movies/{id}/sources")
    suspend fun sources(@Path("id") movieId: Int): Response<Vector<Source>>

    @GET("movies/{id}/on_watchlist")
    suspend fun onWatchlist(@Path("id") movieId: Int): Response<Boolean>

    @DELETE("movies/{id}")
    suspend fun delete(@Path("id") movieId: Int): Response<Unit>

    @POST("movies/{movie_id}/titles")
    suspend fun titleCreate(@Path("movie_id") movieId: Int, @Body title: TitleCreate): Response<Unit>

    @POST("movies/{movie_id}/titles/{id}/primary")
    suspend fun titleSetPrimary(@Path("movie_id") movieId: Int, @Path("id") titleId: Int): Response<Unit>

    @DELETE("movies/{movie_id}/titles/{id}")
    suspend fun titleDelete(@Path("movie_id") movieId: Int, @Path("id") titleId: Int): Response<Unit>

    @POST("movies/{movie_id}/genres")
    suspend fun genreCreate(@Path("movie_id") movieId: Int, @Body genre: GenreCreate): Response<Unit>

    @DELETE("movies/{movie_id}/genres/{id}")
    suspend fun genreDelete(@Path("movie_id") movieId: Int, @Path("id") genreId: Int): Response<Unit>

    @POST("movies/{movie_id}/tags")
    suspend fun tagCreate(@Path("movie_id") movieId: Int, @Body tag: TagCreate): Response<Unit>

    @DELETE("movies/{movie_id}/tags/{id}")
    suspend fun tagDelete(@Path("movie_id") movieId: Int, @Path("id") tagId: Int): Response<Unit>

    @POST("movies/{movie_id}/sources")
    suspend fun sourceCreate(@Path("movie_id") movieId: Int, @Body source: SourceCreate): Response<Unit>

    @POST("movies/{movie_id}/sources/{id}")
    suspend fun sourceUpdate(@Path("movie_id") movieId: Int, @Path("id") sourceId: Int, @Body source: Source): Response<Source>

    @DELETE("movies/{movie_id}/sources/{id}")
    suspend fun deleteSource(@Path("movie_id") movieId: Int, @Path("id") sourceId: Int): Response<Unit>

    @POST("movies/{movie_id}/logs")
    suspend fun logCreate(@Path("movie_id") movieId: Int, @Body log: LogCreate): Response<Unit>

    @POST("movies/{movie_id}/logs/{id}")
    suspend fun logUpdate(@Path("movie_id") movieId: Int, @Path("id") logId: Int, @Body log: Log): Response<Unit>

    @DELETE("movies/{movie_id}/logs/{id}")
    suspend fun deleteLog(@Path("movie_id") movieId: Int, @Path("id") logId: Int): Response<Unit>
}