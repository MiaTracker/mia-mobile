package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.nara.mia.mobile.enums.MediaType
import java.util.Date

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class Logset(
    @JsonProperty("media_id") val mediaId: Int?,
    @JsonProperty("external_id") val externalId: Int?,
    @JsonProperty("media_type") val mediaType: MediaType,
    @JsonProperty("source") val source: String?,
    @JsonProperty("new_source") val newSource: SourceCreate?,
    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: Date,
    @JsonProperty("stars") val stars: Float?,
    @JsonProperty("completed") val completed: Boolean,
    @JsonProperty("comment") val comment: String?,
    @JsonProperty("remove_from_watchlist") val removeFromWatchlist: Boolean?
)
