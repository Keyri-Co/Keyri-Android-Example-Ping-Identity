package com.keyri.examplepingidentity.repository.auth

import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.Config
import com.keyri.examplepingidentity.data.JWKS
import com.keyri.examplepingidentity.data.UserInfo
import com.keyri.examplepingidentity.repository.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class DefaultAuthRepository(
    private val service: AuthService,
    private val config: Config
) : AuthRepository {

    override suspend fun readServerConfig(url: String): Flow<Unit> {
        return service.getOauthConfig(url).onEach {
            config.storeConfig(it)
        }.map { }
    }

    override fun obtainAccessTokenPost(
        url: String,
        clientId: String,
        clientSecret: String,
        code: String,
        grantType: String,
        redirectUri: String
    ): Flow<AccessToken> =
        service.obtainAccessTokenPost(url, code, grantType, clientId, clientSecret, redirectUri)

    override fun obtainAccessTokenBasic(
        url: String,
        basicHeader: String,
        grantType: String,
        code: String,
        redirectUri: String
    ): Flow<AccessToken> =
        service.obtainAccessTokenBasic(url, basicHeader, grantType, code, redirectUri)

    override fun obtainAccessTokenPKCE(
        url: String,
        clientId: String,
        grantType: String,
        code_verifier: String,
        code: String,
        redirectUri: String
    ): Flow<AccessToken> =
        service.obtainAccessTokenPKCE(url, code_verifier, clientId, grantType, code, redirectUri)

    override fun obtainAccessTokenNone(
        url: String,
        clientId: String,
        code: String,
        grantType: String,
        redirectUri: String
    ): Flow<AccessToken> =
        service.obtainAccessTokenNone(url, code, grantType, clientId, redirectUri)

    override fun getUserInfo(url: String, bearerToken: String): Flow<UserInfo> =
        service.getUserInfo(url, bearerToken)

    override fun getJWKS(url: String): Flow<JWKS> = service.getJWKS(url)
}
