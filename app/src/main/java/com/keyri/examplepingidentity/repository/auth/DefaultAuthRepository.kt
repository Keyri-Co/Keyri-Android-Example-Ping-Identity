package com.keyri.examplepingidentity.repository.auth

import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.SaveSignaturePublicKeyBody
import com.keyri.examplepingidentity.data.ServerConfig
import com.keyri.examplepingidentity.data.create_user.response.UserResponse
import com.keyri.examplepingidentity.repository.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultAuthRepository(private val service: AuthService) : AuthRepository {

    override fun readServerConfig(url: String): Flow<ServerConfig> {
        return service.getOauthConfig(url)
    }

    override fun obtainAccessToken(
        url: String,
        clientId: String,
        grantType: String,
        code: String,
        redirectUri: String
    ): Flow<AccessToken> {
        return service.obtainAccessToken(url, clientId, grantType, code, redirectUri)
    }

    override fun obtainAccessTokenBasic(
        url: String,
        basicHeader: String,
        grantType: String
    ): Flow<AccessToken> = service.obtainAccessTokenBasic(url, basicHeader, grantType)

    override fun getUserInfo(url: String, bearerToken: String): Flow<UserResponse> =
        service.getUserInfo(url, bearerToken)

    override fun saveSignaturePublicKey(
        bearerToken: String,
        environmentId: String,
        userId: String,
        publicKey: String
    ): Flow<String> =
        service.saveSignaturePublicKey(
            bearerToken,
            environmentId,
            userId,
            SaveSignaturePublicKeyBody(publicKey)
        ).map { it.nickname }
}
