package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.models.SearchQuery
import com.nara.mia.mobile.models.SearchResults
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Media {
    @GET("media")
    suspend fun index(): Response<List<MediaIndex>>

    @POST("media/search")
    suspend fun search(@Query("committed") committed: Boolean, @Body query: SearchQuery): Response<SearchResults>
}