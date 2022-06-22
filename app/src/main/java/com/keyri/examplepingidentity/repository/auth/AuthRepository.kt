package com.keyri.examplepingidentity.repository.auth

import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.create_user.response.UserResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun register(
        givenName: String,
        family: String,
        email: String,
        username: String,
        password: String,
        populationID: String,
        environmentId: String,
        accessToken: AccessToken
    ): Flow<UserResponse>

    fun obtainAccessTokenBasic(
        url: String,
        basicHeader: String,
        grantType: String
    ): Flow<AccessToken>

    fun getUserInfo(url: String, bearerToken: String): Flow<UserResponse>

    fun saveSignaturePublicKey(
        bearerToken: String,
        environmentId: String,
        userId: String,
        publicKey: String
    ): Flow<String>
}
