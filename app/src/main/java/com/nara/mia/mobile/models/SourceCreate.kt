package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import com.nara.mia.mobile.enums.SourceType

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class SourceCreate(
    @JsonProperty("name") val name: String,
    @JsonProperty("url") val url: String,
    @JsonProperty("type") val type: SourceType
)
