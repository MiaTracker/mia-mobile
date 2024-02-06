package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class Language(
    @JsonProperty("iso_639_1") val iso6391: String,
    @JsonProperty("english_name") val englishName: String,
    @JsonProperty("name") val name: String
)
