package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty

data class UserLogin(
    @JsonProperty("username") val username: String,
    @JsonProperty("password") val password: String
)
