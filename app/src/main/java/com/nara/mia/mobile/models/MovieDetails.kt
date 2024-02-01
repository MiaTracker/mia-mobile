package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date
import java.util.Vector

data class MovieDetails(
    @JsonProperty("id") val id: Int,
    @JsonProperty("poster_path") val posterPath: String?,
    @JsonProperty("backdrop_path") val backdropPath: String?,
    @JsonProperty("stars") val stars: Float?,
    @JsonProperty("title") val title: String,
    @JsonProperty("alternative_titles") val alternativeTitles: Vector<AlternativeTitle>,
    @JsonProperty("release_date") val releaseDate: Date?,
    @JsonProperty("runtime") val runtime: Int?,
    @JsonProperty("status") val status: String?,
    @JsonProperty("overview") val overview: String?,
    @JsonProperty("tmdb_vote_average") val tmdbVoteAverage: Float?,
    @JsonProperty("on_watchlist") val onWatchlist: Boolean,
    @JsonProperty("original_language") val originalLanguage: Language?,
    @JsonProperty("genres") val genres: Vector<Genre>,
    @JsonProperty("tags") val tags: Vector<Tag>,
    @JsonProperty("sources") val sources: Vector<Source>,
    @JsonProperty("logs") val logs: Vector<Log>
)