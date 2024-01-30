package com.nara.mia.mobile.infrastructure

fun imageUrl(url: String): String {
    return "https://image.tmdb.org/t/p/original$url"
}

fun isInstanceUrlInitialized(): Boolean {
    return Config.run?.instance != null
}

fun isTokenPresent(): Boolean {
    return Config.run?.validateToken() ?: false
}