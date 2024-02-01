package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class Log(
    @JsonProperty("id") val id: Int,
    @JsonProperty("date") val date: Date,
    @JsonProperty("source") val source: String,
    @JsonProperty("stars") val stars: Float?,
    @JsonProperty("completed") val completed: Boolean,
    @JsonProperty("comment") val comment: String?
)
