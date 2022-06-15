package com.keyri.examplepingidentity.repository

import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.JWKS
import com.keyri.examplepingidentity.data.SaveSignaturePublicKeyBody
import com.keyri.examplepingidentity.data.ServerConfig
import com.keyri.examplepingidentity.data.UserInfo
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface AuthService {

    @Headers("Content-Type: application/json")
    @GET
    fun getOauthConfig(@Url url: String): Flow<ServerConfig>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    fun obtainAccessTokenPost(
        @Url url: String,
        @Query("code") code: String,
        @Query("grant_type") grantType: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("redirect_uri") redirectUri: String
    ): Flow<AccessToken>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    fun obtainAccessTokenNone(
        @Url url: String,
        @Query("code") code: String,
        @Query("grant_type") grantType: String,
        @Query("client_id") clientId: String,
        @Query("redirect_uri") redirectUri: String
    ): Flow<AccessToken>

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    fun obtainAccessTokenBasic(
        @Url url: String,
        @Header("Authorization") basicAuth: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Flow<AccessToken>

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    fun obtainAccessTokenPKCE(
        @Url url: String,
        @Field("code_verifier") codeVerifier: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Flow<AccessToken>

    @Headers("Content-Type: application/json")
    @GET
    fun getUserInfo(@Url url: String, @Header("Authorization") bearerToken: String): Flow<UserInfo>

    @Headers("Content-Type: application/json")
    @GET
    fun getJWKS(@Url url: String): Flow<JWKS>

    @Headers("Content-Type: application/json")
    @PATCH
    fun saveSignaturePublicKey(
        @Url url: String,
        @Header("Authorization") bearerToken: String,
        @Body request: SaveSignaturePublicKeyBody
    ): Flow<SaveSignaturePublicKeyBody>
}
