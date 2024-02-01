package com.nara.mia.mobile.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nara.mia.mobile.infrastructure.imageUrl
import com.nara.mia.mobile.ui.components.TopBar
import com.nara.mia.mobile.view_models.MovieViewModel
import java.util.Calendar
import java.util.Vector

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MoviePage(viewModel: MovieViewModel = viewModel(), navController: NavController) {
    val state by viewModel.state.collectAsState()

    val movie = state.movie ?: return

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = viewModel::refresh,
        refreshThreshold = 75.dp
    )

    lateinit var releaseYear: String
    if(movie.releaseDate == null) releaseYear = ""
    val calendar = Calendar.getInstance()
    calendar.time = movie.releaseDate!!
    releaseYear = calendar.get(Calendar.YEAR).toString()
    val runtime: String = movie.runtime.toString()


    val titles = buildTagString(movie.alternativeTitles) { it.title }
    val genres = buildTagString(movie.genres) { it.name }
    val tags = buildTagString(movie.tags) { it.name }

    Scaffold(
        topBar = {
            TopBar(navController = navController) {
                Text(text = movie.title)
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = movie.backdropPath?.let { imageUrl(it) },
                    contentDescription = "Backdrop image",
                    Modifier.fillMaxWidth()
                )
                Box(
                    Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = movie.posterPath?.let { imageUrl(it) },
                        contentDescription = "Backdrop image",
                        Modifier
                            .width(150.dp)
                            .absoluteOffset(10.dp, (-10).dp)
                            .zIndex(1.1f)
                    )
                    Column(
                        Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = movie.title,
                                Modifier.absolutePadding(160.dp)
                            )
                        }
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(10.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.absolutePadding(160.dp)
                            ) {
                                Text(text = movie.status ?: "")
                                Text(text = releaseYear)
                                Text(text = "$runtime min")
                            }
                        }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier
                                    .absolutePadding(160.dp)
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                            ) {
                                if(titles.isNotEmpty())
                                    Text(text = "Titles: $titles", softWrap = true)
                                if(genres.isNotEmpty())
                                    Text(text = "Genres: $genres", softWrap = true)
                                if(tags.isNotEmpty())
                                    Text(text = "Tags: $tags", softWrap = true)
                            }
                        }
                    }
                }
                Text(text = movie.overview ?: "", Modifier.padding(10.dp, 5.dp))
            }

            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f),
            )
        }
    }
}


fun <T> buildTagString(vec: Vector<T>, f: (T) -> String): String {
    val builder = StringBuilder()
    vec.forEachIndexed { i, t ->
        if(i != 0) builder.append(", ")
        builder.append(f(t))
    }
    return builder.toString()
}