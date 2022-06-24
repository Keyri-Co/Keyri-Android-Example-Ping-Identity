package com.keyri.examplepingidentity.ui.main

import androidx.lifecycle.ViewModel
import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.Consts
import com.keyri.examplepingidentity.data.ServerConfig
import com.keyri.examplepingidentity.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class MainViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private var serverConfig: ServerConfig? = null

    fun authorize(discoveryUri: String, environmentId: String): Flow<ServerConfig> {
        val url = String.format(discoveryUri, environmentId)

        return authRepository.readServerConfig(url).onEach { serverConfig = it }
    }

    fun proceedWithCode(
        authCode: String,
        clientId: String,
        redirectUri: String
    ): Flow<AccessToken> {
        return authRepository.obtainAccessToken(
            url = requireNotNull(serverConfig?.tokenEndpoint),
            clientId = clientId,
            code = authCode,
            grantType = Consts.AUTHORIZATION_CODE,
            redirectUri = redirectUri
        )
    }
}
