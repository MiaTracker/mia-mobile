package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class UserLogin(
    @JsonProperty("username") val username: String,
    @JsonProperty("password") val password: String
)
