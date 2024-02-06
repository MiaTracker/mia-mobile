package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import com.nara.mia.mobile.enums.MediaType

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class ExternalIndex(
    @JsonProperty("external_id") val externalId: Int,
    @JsonProperty("type") override val type: MediaType,
    @JsonProperty("poster_path") override val posterPath: String?,
    @JsonProperty("title") override val title: String
) : IIndex
