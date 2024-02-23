package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.GenreCreate
import com.nara.mia.mobile.models.Log
import com.nara.mia.mobile.models.LogCreate
import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.models.SearchResults
import com.nara.mia.mobile.models.SeriesDetails
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

    @DELETE("series/{id}")
    suspend fun delete(@Path("id") seriesId: Int): Response<Unit>

    @POST("series/{series_id}/titles")
    suspend fun titleCreate(@Path("series_id") seriesId: Int, @Body title: TitleCreate): Response<Unit>

    @POST("series/{series_id}/titles/{id}/primary")
    suspend fun titleSetPrimary(@Path("series_id") seriesId: Int, @Path("id") titleId: Int): Response<Unit>

    @DELETE("series/{series_id}/titles/{id}")
    suspend fun titleDelete(@Path("series_id") seriesId: Int, @Path("id") titleId: Int): Response<Unit>

    @POST("series/{series_id}/genres")
    suspend fun genreCreate(@Path("series_id") seriesId: Int, @Body genre: GenreCreate): Response<Unit>

    @DELETE("series/{series_id}/genres/{id}")
    suspend fun genreDelete(@Path("series_id") seriesId: Int, @Path("id") genreId: Int): Response<Unit>

    @POST("series/{series_id}/tags")
    suspend fun tagCreate(@Path("series_id") seriesId: Int, @Body tag: TagCreate): Response<Unit>

    @DELETE("series/{series_id}/tags/{id}")
    suspend fun tagDelete(@Path("series_id") seriesId: Int, @Path("id") tagId: Int): Response<Unit>


    @POST("series/{series_id}/sources")
    suspend fun sourceCreate(@Path("series_id") seriesId: Int, @Body source: SourceCreate): Response<Unit>

    @POST("series/{series_id}/sources/{id}")
    suspend fun sourceUpdate(@Path("series_id") seriesId: Int, @Path("id") sourceId: Int, @Body source: Source): Response<Source>

    @DELETE("series/{series_id}/sources/{id}")
    suspend fun deleteSource(@Path("series_id") seriesId: Int, @Path("id") sourceId: Int): Response<Unit>

    @POST("series/{series_id}/logs")
    suspend fun logCreate(@Path("series_id") seriesId: Int, @Body log: LogCreate): Response<Unit>

    @POST("series/{series_id}/logs/{id}")
    suspend fun logUpdate(@Path("series_id") seriesId: Int, @Path("id") logId: Int, @Body log: Log): Response<Unit>

    @DELETE("series/{series_id}/logs/{id}")
    suspend fun deleteLog(@Path("series_id") seriesId: Int, @Path("id") logId: Int): Response<Unit>
}