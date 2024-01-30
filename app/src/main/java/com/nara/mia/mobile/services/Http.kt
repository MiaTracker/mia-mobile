package com.nara.mia.mobile.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object Http {
    suspend fun testConnection(url: String?): Boolean {
        if(url == null) return false

        val client = OkHttpClient()
        val baseUrl = if(url.endsWith("/")) url else "$url/"
        val request = Request.Builder()
            .url(baseUrl + "ping")
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                response.isSuccessful && (response.body?.string() ?: "") == ""
            }
        }
    }
}