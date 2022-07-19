package com.keyri.examplepingidentity.data.create_user.response

import com.google.gson.annotations.SerializedName

data class Users(
    @SerializedName("users")
    val users: List<UserResponse>,
)
