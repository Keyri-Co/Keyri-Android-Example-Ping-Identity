package com.keyri.examplepingidentity.data.create_user.response

import com.google.gson.annotations.SerializedName
import com.keyri.examplepingidentity.data.create_user.request.Lifecycle
import com.keyri.examplepingidentity.data.create_user.request.Name
import com.keyri.examplepingidentity.data.create_user.request.Population

data class UserResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("environment")
    val environment: Environment,

    @SerializedName("account")
    val account: Account,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("enabled")
    val enabled: Boolean,

    @SerializedName("identityProvider")
    val identityProvider: IdentityProvider,

    @SerializedName("lifecycle")
    val lifecycle: Lifecycle,

    @SerializedName("mfaEnabled")
    val mfaEnabled: Boolean,

    @SerializedName("name")
    val name: Name,

    @SerializedName("population")
    val population: Population,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("verifyStatus")
    val verifyStatus: String
)
