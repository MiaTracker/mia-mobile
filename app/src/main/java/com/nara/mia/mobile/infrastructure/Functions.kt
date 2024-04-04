package com.nara.mia.mobile.infrastructure

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.nara.mia.mobile.R

enum class TmdbImageType {
    Poster,
    Backdrop
}

@Composable
fun tmdbImagePainter(url: String?, width: Dp, type: TmdbImageType): Painter {
    return if(url.isNullOrEmpty()) {
        painterResource(id = R.drawable.no_image_placeholder)
    } else {
        val sizes = if(type == TmdbImageType.Poster) { Config.images?.posterSizes }
            else { Config.images?.backdropSizes }
        val baseUrl = Config.images?.secureBaseUrl
        val widthPx = LocalDensity.current.run { width.toPx() }
        val fullUrl = if(sizes == null || baseUrl == null) { "" }
        else {
            var size: String? = null
            val keys = sizes.keys.sorted()
            for(key in keys) {
                if(key >= widthPx) {
                    size = sizes[key]
                    break
                }
            }
            if(size == null) { "" } else { baseUrl + size + url }
        }


        rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fullUrl)
                .crossfade(true)
                .build()
        )
    }
}

fun isInstanceUrlInitialized(): Boolean {
    return Config.run?.instance != null
}

fun isTokenPresent(): Boolean {
    return Config.run?.validateToken() ?: false
}