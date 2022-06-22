package com.keyri.examplepingidentity.ui.login

import android.content.SharedPreferences
import android.util.Base64
import androidx.lifecycle.ViewModel
import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.Consts
import com.keyri.examplepingidentity.data.create_user.response.UserResponse
import com.keyri.examplepingidentity.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val preferences: SharedPreferences
) : ViewModel() {

    fun getUser(
        email: String,
        userinfoEndpoint: String,
        accessToken: AccessToken
    ): Flow<UserResponse> {
        val userId = preferences.getString(email, null)

        return authRepository.getUserInfo(
            userinfoEndpoint + "$userId",
            accessToken.tokenType + " " + accessToken.accessToken
        )
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
