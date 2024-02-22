package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class LogCreate(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("date") val date: Date,
    @JsonProperty("source") val source: String,
    @JsonProperty("stars") val stars: Float?,
    @JsonProperty("completed") val completed: Boolean,
    @JsonProperty("comment") val comment: String?
)