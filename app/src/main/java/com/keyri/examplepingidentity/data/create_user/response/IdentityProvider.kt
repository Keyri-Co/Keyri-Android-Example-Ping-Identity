package com.keyri.examplepingidentity.data.create_user.response

import com.google.gson.annotations.SerializedName

data class IdentityProvider(
    @SerializedName("type")
    val type: String
)
