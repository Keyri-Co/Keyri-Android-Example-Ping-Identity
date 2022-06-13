package com.keyri.examplepingidentity.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.keyri.examplepingidentity.R
import com.keyri.examplepingidentity.data.Config
import com.keyri.examplepingidentity.data.Consts
import com.keyri.examplepingidentity.databinding.ActivityMainBinding
import com.keyri.examplepingidentity.repository.auth.AuthRepository
import com.keyrico.keyrisdk.Keyri
import com.keyrico.keyrisdk.ui.auth.AuthWithScannerActivity
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import org.apache.commons.codec.binary.Base64
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val config by inject<Config>()
    private val authRepository by inject<AuthRepository>()

    private val viewModel by viewModel<MainViewModel>()

    private val easyKeyriAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val text = if (it.resultCode == RESULT_OK) "Authenticated" else "Failed to authenticate"

            showMessage(findViewById(R.id.llRoot), text)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bPingAuth.setOnClickListener {
            startAuth()
        }
    }

    override fun onStart() {
        super.onStart()
        intent.dataString?.takeIf { !viewModel.authenticationStarted }?.let { string ->
            Uri.parse(string).getQueryParameter(Consts.CODE)?.let { accessCode ->
                if (accessCode.isBlank()) {
                    showMessage(window.decorView.rootView, "Code is invalid")
                    openScreenAndClearHistory(MainActivity::class.java)
                    return
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.proceedWithFlow(accessCode)
                            .onEach { accessToken ->
                                val email = viewModel.getUserEmail(accessToken).first()

                                val keyri = Keyri()

                                val payload = JSONObject().apply {
                                    put("token", Gson().toJson(accessToken))
                                    put("provider", "ping:email_password") // Optional
                                    put("timestamp", System.currentTimeMillis()) // Optional
                                    put("associationKey", keyri.getAssociationKey(email)) // Optional
                                    put("userSignature", keyri.getUserSignature(email, email)) // Optional
                                }.toString()

                                // Public user ID (email) is optional
                                keyriAuth(email, payload)
                            }.collect()
                    }
                }
            }

            viewModel.authenticationStarted = true
        }
    }

    @SuppressLint("CheckResult")
    private fun startAuth() {
        lifecycleScope.launch(Dispatchers.IO) {
            config.readAuthConfig()
                .map { String.format(it.discoveryUri, it.environmentId) }
                .flatMapConcat { authRepository.readServerConfig(it) }
                .flatMapConcat { config.readAuthConfig() }
                .onEach { authConfig ->
                    val serviceConfig = AuthorizationServiceConfiguration(
                        Uri.parse(config.serverData?.authorizationEndpoint),
                        Uri.parse(config.serverData?.tokenEndpoint)
                    )

                    val authRequestBuilder = AuthorizationRequest.Builder(
                        serviceConfig,
                        authConfig.clientId,
                        ResponseTypeValues.CODE,
                        Uri.parse(authConfig.redirectUri)
                    )

                    val authRequest = authRequestBuilder
                        .setCodeVerifier(
                            config.codeVerifier,
                            generateCodeChallenge(requireNotNull(config.codeVerifier)),
                            "S256"
                        )
                        .setScope(authConfig.authorizationScope)
                        .setPrompt("login")
                        .build()

                    val authService = AuthorizationService(this@MainActivity)
                    val authIntent = authService.getAuthorizationRequestIntent(authRequest)

                    withContext(Dispatchers.Main) {
                        startActivityForResult(authIntent, RC_AUTH)
                    }
                }
                .collect()
        }
    }

    private fun generateCodeChallenge(verifier: String): String {
        val bytes = verifier.toByteArray(Charsets.US_ASCII)
        val md = MessageDigest.getInstance("SHA-256")

        md.update(bytes, 0, bytes.size)

        val digest = md.digest()

        return Base64.encodeBase64URLSafeString(digest)
    }

    private fun keyriAuth(publicUserId: String?, payload: String) {
        val intent = Intent(this, AuthWithScannerActivity::class.java).apply {
            putExtra(AuthWithScannerActivity.APP_KEY, "IT7VrTQ0r4InzsvCNJpRCRpi1qzfgpaj")
            putExtra(AuthWithScannerActivity.PUBLIC_USER_ID, publicUserId)
            putExtra(AuthWithScannerActivity.PAYLOAD, payload)
        }

        easyKeyriAuthLauncher.launch(intent)
    }

    private fun openScreenAndClearHistory(destinationActivity: Class<out Activity>) {
        val intent = Intent(this, destinationActivity)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //clear back stack
        startActivity(intent)
        finish()
    }

    private fun showMessage(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private const val RC_AUTH = 1234
    }
}