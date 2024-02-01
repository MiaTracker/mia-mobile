package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.nara.mia.mobile.enums.SourceType

data class Source(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("url") val url: String,
    @JsonProperty("type") val type: SourceType
)
