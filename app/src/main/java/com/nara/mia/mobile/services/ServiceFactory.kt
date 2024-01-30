package com.nara.mia.mobile.services

import com.nara.mia.mobile.infrastructure.Config
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import kotlin.reflect.KClass

class ServiceFactory {
    private val builder: Retrofit

    private val client: OkHttpClient.Builder = OkHttpClient.Builder()

    init {
        client.addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder().addHeader("Authorization", "Bearer " + Config.run!!.token).build()
            return@Interceptor chain.proceed(request)
        })
        builder = Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .client(client.build())
            .build()
    }

    private fun getBaseUrl(): String {
        val url = Config.run!!.instance ?: ""
        if (url.endsWith("/")) return url
        return "$url/"
    }

    companion object {
        private var factory: ServiceFactory? = null

        fun <T:Any> create(t: KClass<T>): T {
            if (factory == null) {
                factory = ServiceFactory()
            }

            return factory!!.builder.create(t.java)
        }
    }
}

