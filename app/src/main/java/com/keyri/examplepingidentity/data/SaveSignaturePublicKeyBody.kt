package com.keyri.examplepingidentity.data

import com.google.gson.annotations.SerializedName

data class SaveSignaturePublicKeyBody(
    @SerializedName("nickname")
    val nickname: String
)
