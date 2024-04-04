package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.ImagesConfiguration
import retrofit2.Response
import retrofit2.http.GET

interface Configuration {
    @GET("configuration/images")
    suspend fun images(): Response<ImagesConfiguration>
}