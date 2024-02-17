package com.nara.mia.mobile.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nara.mia.mobile.enums.MediaType
import com.nara.mia.mobile.infrastructure.imageUrl
import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.ui.components.TopBar
import com.nara.mia.mobile.view_models.IndexViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IndexPage(viewModel: IndexViewModel, navController: NavController, drawerState: DrawerState) {
    val state by viewModel.state.collectAsState()
    val pullRefreshState = remember {
        PullToRefreshState(70f, state.index == null)
    }
    if(pullRefreshState.isRefreshing) {
        viewModel.refresh { pullRefreshState.endRefresh() }
    }

    Scaffold(
        topBar = {
            TopBar(navController = navController, drawerState = drawerState) {
                Text(text = viewModel.title())
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FlowRow(
                Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
            ) {
                state.index?.forEach { idx ->
                    Poster(index = idx, navController = navController)
                }
            }
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

}

@Composable
fun Poster(index: MediaIndex, navController: NavController) {
    Column(
        Modifier
            .width(100.dp)
            .clickable {
                navController.navigate(if (index.type == MediaType.Movie) "movie/" + index.id else "series/" + index.id)
            }
    ) {
        AsyncImage(
            model = imageUrl(index.posterPath ?: ""),
            contentDescription = index.title
        )
        Text(text = index.title, softWrap = true, textAlign = TextAlign.Center)
    }
}