package com.nara.mia.mobile.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.nara.mia.mobile.ui.components.Details
import com.nara.mia.mobile.view_models.SeriesViewModel
import java.util.Calendar

@Composable
fun SeriesPage(viewModel: SeriesViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()

    val series = state.series

    Details(
        media = series,
        navController = navController,
        viewModel = viewModel
    ) {
        series ?: return@Details

        lateinit var firstAirDate: String
        if(series.firstAirDate == null) firstAirDate = ""
        else {
            val calendar = Calendar.getInstance()
            calendar.time = series.firstAirDate
            firstAirDate = calendar.get(Calendar.YEAR).toString()
        }

        Text(text = series.status ?: "")
        Text(text = firstAirDate)
        if(series.numberOfSeasons != null) {
            val numberOfSeasons: String = series.numberOfSeasons.toString()
            Text(text = "$numberOfSeasons min")
        }
        if(series.numberOfEpisodes != null) {
            val numberOfEpisodes: String = series.numberOfEpisodes.toString()
            Text(text = "$numberOfEpisodes episodes")
        }
    }
}