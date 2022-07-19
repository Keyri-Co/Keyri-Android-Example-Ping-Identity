package com.keyri.examplepingidentity.ui.register

import android.util.Base64
import androidx.lifecycle.ViewModel
import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.Consts
import com.keyri.examplepingidentity.data.create_user.response.UserResponse
import com.keyri.examplepingidentity.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun register(
        givenName: String,
        family: String,
        email: String,
        username: String,
        password: String,
        populationID: String,
        environmentId: String,
        accessToken: AccessToken
    ): Flow<UserResponse> {
        return authRepository.register(
            givenName,
            family,
            email,
            username,
            password,
            populationID,
            environmentId,
            accessToken
        ).map { it.getOrThrow() }
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
        ).map { it.getOrThrow() }
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
        ).map { it.getOrThrow().nickname }
    }
}
