package com.keyri.examplepingidentity.data

import com.google.gson.annotations.SerializedName

data class ServerConfig(
    @SerializedName("authorization_endpoint") val authorizationEndpoint: String,
    @SerializedName("claim_types_supported") val claimTypesSupported: List<String>,
    @SerializedName("claims_parameter_supported") val claimsParameterSupported: Boolean,
    @SerializedName("claims_supported") val claimsSupported: List<String>,
    @SerializedName("end_session_endpoint") val endSessionEndpoint: String,
    @SerializedName("grant_types_supported") val grantTypesSupported: List<String>,
    @SerializedName("id_token_signing_alg_values_supported") val idTokenSigningAlgValuesSupported: List<String>,
    @SerializedName("issuer") val issuer: String,
    @SerializedName("jwks_uri") val jwksUri: String,
    @SerializedName("request_object_signing_alg_values_supported") val requestObjectSigningAlgValuesSupported: List<String>,
    @SerializedName("request_parameter_supported") val requestParameterSupported: Boolean,
    @SerializedName("request_uri_parameter_supported") val requestUriParameterSupported: Boolean,
    @SerializedName("response_modes_supported") val responseModesSupported: List<String>,
    @SerializedName("response_types_supported") val responseTypesSupported: List<String>,
    @SerializedName("scopes_supported") val scopesSupported: List<String>,
    @SerializedName("subject_types_supported") val subjectTypesSupported: List<String>,
    @SerializedName("token_endpoint") val tokenEndpoint: String,
    @SerializedName("token_endpoint_auth_methods_supported") val tokenEndpointAuthMethodsSupported: List<String>,
    @SerializedName("userinfo_endpoint") val userinfoEndpoint: String,
    @SerializedName("userinfo_signing_alg_values_supported") val userinfoSigningAlgValuesSupported: List<String>
)
