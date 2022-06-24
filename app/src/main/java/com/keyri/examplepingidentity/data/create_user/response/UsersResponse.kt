package com.keyri.examplepingidentity.data.create_user.response

import com.google.gson.annotations.SerializedName

data class UsersResponse(
    @SerializedName("_embedded")
    val _embedded: Users,
)
