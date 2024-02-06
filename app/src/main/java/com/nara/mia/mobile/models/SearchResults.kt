package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Vector

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class SearchResults(
    @JsonProperty("indexes") val indexes: Vector<MediaIndex>,
    @JsonProperty("external") val external: Vector<ExternalIndex>
)
