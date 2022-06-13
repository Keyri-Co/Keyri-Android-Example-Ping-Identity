package com.keyri.examplepingidentity.data

import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: String,
    @SerializedName("scope") val scope: String,
    @SerializedName("id_token") val idToken: String
)
