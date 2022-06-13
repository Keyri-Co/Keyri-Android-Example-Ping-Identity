package com.keyri.examplepingidentity.data

import com.google.gson.annotations.SerializedName

data class ConfigData(
    @SerializedName("environment_id") val environmentId: String,
    @SerializedName("client_id") val clientId: String,
    @SerializedName("redirect_uri") val redirectUri: String,
    @SerializedName("authorization_scope") val authorizationScope: String,
    @SerializedName("discovery_uri") val discoveryUri: String,
    @SerializedName("token_method") val tokenMethod: String,
    @SerializedName("client_secret") val clientSecret: String
)
