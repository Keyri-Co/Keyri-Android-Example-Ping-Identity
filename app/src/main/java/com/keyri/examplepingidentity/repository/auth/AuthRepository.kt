package com.keyri.examplepingidentity.repository.auth

import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.JWKS
import com.keyri.examplepingidentity.data.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun readServerConfig(url: String): Flow<Unit>

    fun obtainAccessTokenPKCE(
        url: String,
        clientId: String,
        grantType: String,
        code_verifier: String,
        code: String,
        redirectUri: String
    ): Flow<AccessToken>

    fun obtainAccessTokenPost(
        url: String,
        clientId: String,
        clientSecret: String,
        code: String,
        grantType: String,
        redirectUri: String
    ): Flow<AccessToken>

    fun obtainAccessTokenNone(
        url: String,
        clientId: String,
        code: String,
        grantType: String,
        redirectUri: String
    ): Flow<AccessToken>

    fun obtainAccessTokenBasic(
        url: String,
        basicHeader: String,
        grantType: String,
        code: String,
        redirectUri: String
    ): Flow<AccessToken>

    fun getUserInfo(url: String, bearerToken: String): Flow<UserInfo>

    fun getJWKS(url: String): Flow<JWKS>

    fun saveSignaturePublicKey(
        url: String,
        authorization: String,
        publicKey: String
    ): Flow<String>
}
