package com.keyri.examplepingidentity.data.create_user.request

import com.google.gson.annotations.SerializedName

data class Name(
    @SerializedName("given")
    val given: String,

    @SerializedName("family")
    val family: String
)
