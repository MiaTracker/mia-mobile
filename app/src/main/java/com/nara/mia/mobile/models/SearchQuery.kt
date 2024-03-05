package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Vector

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class SearchQuery(
    @JsonProperty("query") val query: String,
    @JsonProperty("genres") val genres: Vector<String>?,
    @JsonProperty("only_watched") val onlyWatched: Boolean,
    @JsonProperty("min_stars") val minStars: Float?,
)