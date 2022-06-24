package com.keyri.examplepingidentity.repository

import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.create_user.request.CreateUserBody
import com.keyri.examplepingidentity.data.SaveSignaturePublicKeyBody
import com.keyri.examplepingidentity.data.create_user.response.UserResponse
import com.keyri.examplepingidentity.data.create_user.response.UsersResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface AuthService {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    fun obtainAccessTokenBasic(
        @Url url: String,
        @Header("Authorization") basicAuth: String,
        @Field("grant_type") grantType: String
    ): Flow<AccessToken>

    @Headers("Content-Type: application/json")
    @GET
    fun getUserInfo(
        @Url url: String,
        @Header("Authorization") bearerToken: String
    ): Flow<UserResponse>

    @Headers("Content-Type: application/json")
    @GET("v1/environments/{environmentId}/users")
    fun getUsers(
        @Header("Authorization") bearerToken: String,
        @Path("environmentId") environmentId: String
    ): Flow<UsersResponse>

    @Headers("Content-Type: application/vnd.pingidentity.user.import+json")
    @POST("v1/environments/{environmentId}/users")
    fun createUser(
        @Header("Authorization") bearerToken: String,
        @Path("environmentId") environmentId: String,
        @Body data: CreateUserBody
    ): Flow<UserResponse>

    @Headers("Content-Type: application/json")
    @PATCH("v1/environments/{environmentId}/users/{userId}/")
    fun saveSignaturePublicKey(
        @Header("Authorization") bearerToken: String,
        @Path("environmentId") environmentId: String,
        @Path("userId") userId: String,
        @Body request: SaveSignaturePublicKeyBody
    ): Flow<SaveSignaturePublicKeyBody>
}
