package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.models.SearchQuery
import com.nara.mia.mobile.models.SearchResults
import com.nara.mia.mobile.models.WatchlistParams
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Watchlist {
    @GET("watchlist")
    suspend fun index(): Response<List<MediaIndex>>

    @POST("watchlist/search")
    suspend fun search(@Body query: SearchQuery): Response<SearchResults>

    @POST("watchlist/add")
    suspend fun add(@Body params: WatchlistParams): Response<Unit>

    @POST("watchlist/remove")
    suspend fun remove(@Body params: WatchlistParams): Response<Unit>
}