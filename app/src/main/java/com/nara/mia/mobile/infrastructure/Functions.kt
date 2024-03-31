package com.nara.mia.mobile.infrastructure

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.nara.mia.mobile.R

@Composable
fun tmdbImagePainter(url: String?): Painter {
    return if(url.isNullOrEmpty()) {
        painterResource(id = R.drawable.no_image_placeholder)
    } else {
        rememberAsyncImagePainter(
            model = "https://image.tmdb.org/t/p/original$url"
        )
    }
}

fun isInstanceUrlInitialized(): Boolean {
    return Config.run?.instance != null
}

fun isTokenPresent(): Boolean {
    return Config.run?.validateToken() ?: false
}