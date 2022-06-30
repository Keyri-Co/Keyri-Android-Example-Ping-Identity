package com.keyri.examplepingidentity.repository.auth

import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.Consts.ACCOUNT_OK
import com.keyri.examplepingidentity.data.SaveSignaturePublicKeyBody
import com.keyri.examplepingidentity.data.create_user.request.CreateUserBody
import com.keyri.examplepingidentity.data.create_user.request.Lifecycle
import com.keyri.examplepingidentity.data.create_user.request.Name
import com.keyri.examplepingidentity.data.create_user.request.Password
import com.keyri.examplepingidentity.data.create_user.request.Population
import com.keyri.examplepingidentity.data.create_user.response.UserResponse
import com.keyri.examplepingidentity.repository.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultAuthRepository(private val service: AuthService) : AuthRepository {

    override fun register(
        givenName: String,
        family: String,
        email: String,
        username: String,
        password: String,
        populationID: String,
        environmentId: String,
        accessToken: AccessToken
    ): Flow<Result<UserResponse>> {
        val bearerToken = accessToken.tokenType + " " + accessToken.accessToken

        val name = Name(givenName, family)
        val population = Population(populationID)
        val lifecycle = Lifecycle(ACCOUNT_OK, false)
        val passwordBody = Password(password, false)
        val request = CreateUserBody(email, name, population, lifecycle, username, passwordBody)

        return service.createUser(bearerToken, environmentId, request)
    }

    override fun getUsers(
        bearerToken: String,
        environmentId: String
    ): Flow<Result<List<UserResponse>>> {
        return service.getUsers(bearerToken, environmentId)
            .map { response ->
                response.getOrNull()?._embedded?.users?.let { Result.success(it) }
                    ?: Result.failure(Exception("Failed to fetch Users"))
            }
    }

    override fun obtainAccessTokenBasic(
        url: String,
        basicHeader: String,
        grantType: String
    ): Flow<Result<AccessToken>> = service.obtainAccessTokenBasic(url, basicHeader, grantType)

    override fun getUserInfo(url: String, bearerToken: String): Flow<Result<UserResponse>> =
        service.getUserInfo(url, bearerToken)

    override fun saveSignaturePublicKey(
        bearerToken: String,
        environmentId: String,
        userId: String,
        publicKey: String
    ): Flow<Result<SaveSignaturePublicKeyBody>> =
        service.saveSignaturePublicKey(
            bearerToken,
            environmentId,
            userId,
            SaveSignaturePublicKeyBody(publicKey)
        )
}
