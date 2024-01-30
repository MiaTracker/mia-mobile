package com.nara.mia.mobile.services

import com.nara.mia.mobile.models.UserLogin
import com.nara.mia.mobile.models.UserToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Users {
    @POST("users/login")
    suspend fun login(@Body user: UserLogin): Response<UserToken>
}