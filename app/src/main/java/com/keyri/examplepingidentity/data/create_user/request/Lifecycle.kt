package com.keyri.examplepingidentity.data.create_user.request

import com.google.gson.annotations.SerializedName

data class Lifecycle(
    @SerializedName("status")
    val status: String,

    @SerializedName("suppressVerificationCode")
    val suppressVerificationCode: Boolean?
)
