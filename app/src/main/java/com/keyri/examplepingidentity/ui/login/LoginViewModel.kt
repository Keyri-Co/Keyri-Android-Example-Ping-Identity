package com.keyri.examplepingidentity.ui.login

import android.util.Base64
import androidx.lifecycle.ViewModel
import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.Consts
import com.keyri.examplepingidentity.data.create_user.response.UserResponse
import com.keyri.examplepingidentity.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun getUser(
        email: String,
        environmentId: String,
        accessToken: AccessToken
    ): Flow<UserResponse> {
        val token = accessToken.tokenType + " " + accessToken.accessToken

        return authRepository.getUsers(token, environmentId).map {
            it.first { user -> user.email == email }
        }
    }

    fun getAccessTokenWithBasic(
        clientId: String,
        clientSecret: String,
        tokenEndpoint: String
    ): Flow<AccessToken> {
        val credentials = "$clientId:$clientSecret"
        val basic = Consts.BASIC + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        return authRepository.obtainAccessTokenBasic(
            url = tokenEndpoint,
            basicHeader = basic,
            grantType = Consts.CLIENT_CREDENTIALS
        )
    }

    fun saveSignaturePublicKey(
        userId: String,
        environmentId: String,
        accessToken: AccessToken,
        publicKey: String
    ): Flow<String> {
        val authorization = accessToken.tokenType + " " + accessToken.accessToken

        return authRepository.saveSignaturePublicKey(
            bearerToken = authorization,
            environmentId = environmentId,
            userId = userId,
            publicKey = publicKey
        )
    }
}
