package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.models.MovieDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Movies {
    @GET("movies")
    suspend fun index(): Response<List<MediaIndex>>

    @GET("movies/{id}")
    suspend fun details(@Path("id") movieId: Int): Response<MovieDetails>
}