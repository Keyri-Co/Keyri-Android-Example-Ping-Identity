package com.keyri.examplepingidentity.data.create_user.response

import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("canAuthenticate")
    val canAuthenticate: Boolean,

    @SerializedName("status")
    val status: String
)
