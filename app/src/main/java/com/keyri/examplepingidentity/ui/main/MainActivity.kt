package com.keyri.examplepingidentity.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.keyri.examplepingidentity.R
import com.keyri.examplepingidentity.data.AccessToken
import com.keyri.examplepingidentity.data.Consts
import com.keyri.examplepingidentity.data.create_user.response.UserResponse
import com.keyri.examplepingidentity.databinding.ActivityMainBinding
import com.keyrico.keyrisdk.Keyri
import com.keyrico.keyrisdk.ui.auth.AuthWithScannerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val easyKeyriAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val text = if (it.resultCode == RESULT_OK) "Authenticated" else "Failed to authenticate"

            showMessage(findViewById(R.id.llRoot), text)
        }

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            bPingAuth.setOnClickListener {
                authorize()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        intent.dataString?.let { Uri.parse(it).getQueryParameter(Consts.CODE) }?.let { code ->
            val clientId = getString(R.string.client_id)
            val redirectUri = getString(R.string.redirect_uri)
            val userInfoUri = getString(R.string.user_info_uri)

            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.proceedWithCode(code, clientId, redirectUri)
                    .collectLatest { accessToken ->
                        viewModel.getUserInfo(userInfoUri, accessToken)
                            .collectLatest { keyriAuth(accessToken, it) }
                    }
            }
        }
    }

    private fun authorize() {
        val discoveryUri = getString(R.string.discovery_uri)
        val environmentId = getString(R.string.environment_id)

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.authorize(discoveryUri, environmentId)
                .onEach {
                    val authorizationUrl = Uri.parse(it.authorizationEndpoint)
                        .buildUpon()
                        .appendQueryParameter(Consts.RESPONSE_TYPE, Consts.CODE)
                        .appendQueryParameter(Consts.CLIENT_ID, getString(R.string.client_id))
                        .appendQueryParameter(Consts.SCOPE, getString(R.string.authorization_scope))
                        .appendQueryParameter(Consts.REDIRECT_URI, getString(R.string.redirect_uri))
                        .build()

                    startActivity(Intent(Intent.ACTION_VIEW, authorizationUrl))
                }.collect()
        }
    }

    private fun keyriAuth(accessToken: AccessToken, user: UserResponse) {
        val environmentId = getString(R.string.environment_id)

        val email = user.email
        val keyri = Keyri()

        val associationKey = keyri.getAssociationKey(email)

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.saveSignaturePublicKey(
                accessToken.accessToken,
                environmentId,
                user.id,
                associationKey
            ).collectLatest {
                val timestampNonce = "${System.currentTimeMillis()}_${Random.nextInt()}"
                val signature = keyri.getUserSignature(email, timestampNonce)

                val payload = JSONObject().apply {
                    put("username", user.username)
                    put("timestamp_nonce", timestampNonce)
                    put("userSignature", signature) // Optional
                }.toString()

                // Public user ID (email) is optional
                keyriAuth(email, payload)
            }
        }
    }

    private fun keyriAuth(publicUserId: String?, payload: String) {
        val intent = Intent(this, AuthWithScannerActivity::class.java).apply {
            putExtra(AuthWithScannerActivity.APP_KEY, "NJOFSuP652zthaoHaeDmImZ2CTh4NGqX")
            putExtra(AuthWithScannerActivity.PUBLIC_USER_ID, publicUserId)
            putExtra(AuthWithScannerActivity.PAYLOAD, payload)
        }

        easyKeyriAuthLauncher.launch(intent)
    }

    private fun showMessage(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }
}
