package com.keyri.examplepingidentity.repository.auth

import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.ServerConfig
import com.keyri.examplepingidentity.data.create_user.response.UserResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun readServerConfig(url: String): Flow<ServerConfig>

    fun obtainAccessToken(
        url: String,
        clientId: String,
        grantType: String,
        code: String,
        redirectUri: String
    ): Flow<AccessToken>

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
