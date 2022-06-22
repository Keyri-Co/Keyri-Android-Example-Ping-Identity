package com.keyri.examplepingidentity.data.create_user.request

import com.google.gson.annotations.SerializedName

data class Password(
    @SerializedName("value")
    val value: String,

    @SerializedName("forceChange")
    val forceChange: Boolean
)
