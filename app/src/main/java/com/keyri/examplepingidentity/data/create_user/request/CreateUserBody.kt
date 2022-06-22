package com.keyri.examplepingidentity.data.create_user.request

import com.google.gson.annotations.SerializedName

data class CreateUserBody(
    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: Name,

    @SerializedName("population")
    val population: Population,

    @SerializedName("lifecycle")
    val lifecycle: Lifecycle,

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: Password
)
