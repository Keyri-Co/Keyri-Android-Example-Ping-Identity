package com.keyri.examplepingidentity.data

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("address") val address: Address,
    @SerializedName("email") val email: String,
    @SerializedName("family_name") val familyName: String,
    @SerializedName("given_name") val givenName: String,
    @SerializedName("middle_name") val middleName: String,
    @SerializedName("name") val name: String,
    @SerializedName("preferred_username") val preferredUsername: String,
    @SerializedName("sub") val sub: String,
    @SerializedName("updated_at") val updatedAt: Int
)

data class Address(
    @SerializedName("country") val country: String,
    @SerializedName("locality") val locality: String,
    @SerializedName("postal_code") val postalCode: String,
    @SerializedName("region") val region: String,
    @SerializedName("street_address") val streetAddress: String
)
