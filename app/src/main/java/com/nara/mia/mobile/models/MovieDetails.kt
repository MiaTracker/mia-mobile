package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date
import java.util.Vector

data class MovieDetails(
    @JsonProperty("id") override val id: Int,
    @JsonProperty("poster_path") override val posterPath: String?,
    @JsonProperty("backdrop_path") override val backdropPath: String?,
    @JsonProperty("stars") override val stars: Float?,
    @JsonProperty("title") override val title: String,
    @JsonProperty("alternative_titles") override val alternativeTitles: Vector<AlternativeTitle>,
    @JsonProperty("release_date") val releaseDate: Date?,
    @JsonProperty("runtime") val runtime: Int?,
    @JsonProperty("status") override val status: String?,
    @JsonProperty("overview") override val overview: String?,
    @JsonProperty("tmdb_vote_average") override val tmdbVoteAverage: Float?,
    @JsonProperty("on_watchlist") override val onWatchlist: Boolean,
    @JsonProperty("original_language") override val originalLanguage: Language?,
    @JsonProperty("genres") override val genres: Vector<Genre>,
    @JsonProperty("tags") override val tags: Vector<Tag>,
    @JsonProperty("sources") override val sources: Vector<Source>,
    @JsonProperty("logs") override val logs: Vector<Log>
) : IMediaDetails