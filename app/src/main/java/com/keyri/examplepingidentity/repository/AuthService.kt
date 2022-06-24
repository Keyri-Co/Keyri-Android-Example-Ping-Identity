package com.keyri.examplepingidentity.repository

import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.SaveSignaturePublicKeyBody
import com.keyri.examplepingidentity.data.ServerConfig
import com.keyri.examplepingidentity.data.create_user.response.UserResponse
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

    @Headers("Content-Type: application/json")
    @GET
    fun getOauthConfig(@Url url: String): Flow<ServerConfig>

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    fun obtainAccessToken(
        @Url url: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Flow<AccessToken>

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
    @PATCH("v1/environments/{environmentId}/users/{userId}/")
    fun saveSignaturePublicKey(
        @Header("Authorization") bearerToken: String,
        @Path("environmentId") environmentId: String,
        @Path("userId") userId: String,
        @Body request: SaveSignaturePublicKeyBody
    ): Flow<SaveSignaturePublicKeyBody>
}
