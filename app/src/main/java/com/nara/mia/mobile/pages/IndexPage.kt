package com.nara.mia.mobile.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nara.mia.mobile.R
import com.nara.mia.mobile.enums.MediaType
import com.nara.mia.mobile.infrastructure.TmdbImageType
import com.nara.mia.mobile.infrastructure.tmdbImagePainter
import com.nara.mia.mobile.models.ExternalIndex
import com.nara.mia.mobile.models.IIndex
import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.ui.components.TopBar
import com.nara.mia.mobile.view_models.IndexViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexPage(viewModel: IndexViewModel, navController: NavController, drawerState: DrawerState) {
    val state by viewModel.state.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()
    var searchVisible by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = "") {
        pullRefreshState.startRefresh()
    }

    if(pullRefreshState.isRefreshing) {
        viewModel.refresh { pullRefreshState.endRefresh() }
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                drawerState = drawerState,
                title = {
                    Text(text = viewModel.title())
                },
                actions = {
                    IconButton(onClick = { searchVisible = !searchVisible }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = viewModel.snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if(searchVisible) {
                DockedSearchBar(
                    query = state.query,
                    onQueryChange = { v -> viewModel.applySearch(v) },
                    onSearch = { v -> viewModel.applySearch(v, true) },
                    active = false,
                    onActiveChange = { },
                    trailingIcon = {
                        if(state.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.applySearch("") }) {
                                Icon(painter = painterResource(id = R.drawable.baseline_clear_24), contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 10.dp, 0.dp, 0.dp)
                        .focusRequester(searchFocusRequester)
                ) { }
            }

            LaunchedEffect(key1 = searchVisible) {
                if(searchVisible) searchFocusRequester.requestFocus()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(pullRefreshState.nestedScrollConnection)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 110.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                    contentPadding = PaddingValues(15.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    val idxs = state.index
                    if(idxs != null) {
                        items(idxs) { idx ->
                            Poster(
                                idx,
                                viewModel.multiType,
                                Modifier.clickable { navController.navigate(if (idx.type == MediaType.Movie) "movie/${idx.id}" else "series/${idx.id}") }
                            )
                        }
                    }

                    val externalIdxs = state.external
                    if(!externalIdxs.isNullOrEmpty()) {
                        item(
                            span = { GridItemSpan(3) }
                        ) {
                            Text(
                                text = "External",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                        }

                        items(externalIdxs) { idx ->
                            Poster(
                                idx,
                                viewModel.multiType,
                                Modifier.clickable {
                                    viewModel.create(
                                        idx,
                                        navController
                                    )
                                }
                            )
                        }
                    }
                }

                PullToRefreshContainer(
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
fun Poster(index: IIndex, showType: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier
            .width(110.dp)
    ) {
        Box {
            Image(
                painter = tmdbImagePainter(index.posterPath, 110.dp, TmdbImageType.Poster),
                contentDescription = index.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp)
            )

            if(showType) {
                if(index.type == MediaType.Movie) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_movie_48),
                        contentDescription = "Movie",
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(5.dp)
                            .size(25.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_tv_48),
                        contentDescription = "Series",
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(5.dp)
                            .size(25.dp)
                    )
                }
            }

            if(index is ExternalIndex) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_link_24),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .size(25.dp)
                )
            } else if(index is MediaIndex && index.stars != null) {
                Row(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                ) {
                    Icon(painter = painterResource(id = R.drawable.baseline_star_rate_24), contentDescription = "", tint = Color.Yellow)
                    Text(text = ((index.stars * 10.0).roundToInt() / 10.0).toString())
                }
            }
        }
        Text(text = index.title, softWrap = false, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}