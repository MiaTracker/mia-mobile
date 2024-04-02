package com.nara.mia.mobile.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.nara.mia.mobile.ui.components.Details
import com.nara.mia.mobile.view_models.MovieViewModel
import java.util.Calendar

@Composable
fun MoviePage(viewModel: MovieViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    val movie = state.movie

    Details(
        media = movie,
        navController = navController,
        viewModel = viewModel
    ) {
        movie ?: return@Details
        lateinit var releaseYear: String
        if(movie.releaseDate == null) releaseYear = ""
        else {
            val calendar = Calendar.getInstance()
            calendar.time = movie.releaseDate
            releaseYear = calendar.get(Calendar.YEAR).toString()
        }

        Text(text = movie.status ?: "")
        Text(text = releaseYear)
        if(movie.runtime != null) {
            val hours = movie.runtime / 60
            val min = movie.runtime % 60
            Text(text = "${hours}h ${min}min")
        }
    }
}