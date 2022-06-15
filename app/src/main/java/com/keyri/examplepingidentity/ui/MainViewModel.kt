package com.keyri.examplepingidentity.ui

import android.util.Base64
import androidx.lifecycle.ViewModel
import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.Config
import com.keyri.examplepingidentity.data.ConfigData
import com.keyri.examplepingidentity.data.Consts
import com.keyri.examplepingidentity.data.TokenMethod
import com.keyri.examplepingidentity.data.UserInfo
import com.keyri.examplepingidentity.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat

class MainViewModel(private val authRepository: AuthRepository, private val config: Config) :
    ViewModel() {

    var authenticationStarted = false

    fun proceedWithFlow(code: String): Flow<AccessToken> {
        return config.readAuthConfig()
            .flatMapConcat {
                when (it.tokenMethod) {
                    TokenMethod.CLIENT_SECRET_POST.stringValue -> proceedWithPost(code, it)
                    TokenMethod.CLIENT_SECRET_BASIC.stringValue -> proceedWithBasic(code, it)
                    else -> proceedWithPKCE(code, it)
                }
            }
    }

    fun getUserEmail(accessToken: AccessToken): Flow<UserInfo> {
        return authRepository.getUserInfo(
            requireNotNull(config.serverData?.userinfoEndpoint),
            accessToken.tokenType + " " + accessToken.accessToken
        )
    }

    fun saveSignaturePublicKey(
        userId: String,
        accessToken: AccessToken,
        publicKey: String
    ): Flow<String> {
        val url =
            "https://api.pingone.com/v1/environments/0930f393-9d60-4e3a-a4e1-4394197537d2/users/$userId"

        val authorization = accessToken.tokenType + " " + accessToken.accessToken

        return authRepository.saveSignaturePublicKey(url, authorization, publicKey)
    }

    private fun proceedWithPKCE(accessCode: String, configData: ConfigData): Flow<AccessToken> {
        return authRepository.obtainAccessTokenPKCE(
            url = requireNotNull(config.serverData?.tokenEndpoint),
            clientId = configData.clientId,
            code_verifier = requireNotNull(config.codeVerifier),
            code = accessCode,
            grantType = Consts.AUTHORIZATION_CODE,
            redirectUri = configData.redirectUri
        )
    }

    private fun proceedWithBasic(accessCode: String, configData: ConfigData): Flow<AccessToken> {
        val credentials = configData.clientId + ":" + configData.clientSecret
        val basic = Consts.BASIC + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        return authRepository.obtainAccessTokenBasic(
            url = requireNotNull(config.serverData?.tokenEndpoint),
            basicHeader = basic,
            grantType = Consts.AUTHORIZATION_CODE,
            code = accessCode,
            redirectUri = configData.redirectUri
        )
    }

    private fun proceedWithPost(accessCode: String, configData: ConfigData): Flow<AccessToken> {
        return authRepository.obtainAccessTokenPost(
            url = requireNotNull(config.serverData?.tokenEndpoint),
            clientId = configData.clientId,
            clientSecret = configData.clientSecret,
            grantType = accessCode,
            code = Consts.AUTHORIZATION_CODE,
            redirectUri = configData.redirectUri
        )
    }
}
