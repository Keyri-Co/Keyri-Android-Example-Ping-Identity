package com.keyri.examplepingidentity.data

import com.google.gson.annotations.SerializedName

data class JWKS(
    val keys: List<Key>
)

data class Key(
    @SerializedName("e") val e: String,
    @SerializedName("kid") val kid: String,
    @SerializedName("kty") val kty: String,
    @SerializedName("n") val n: String,
    @SerializedName("use") val use: String,
    @SerializedName("x5c") val x5c: List<String>,
    @SerializedName("x5t") val x5t: String
)
