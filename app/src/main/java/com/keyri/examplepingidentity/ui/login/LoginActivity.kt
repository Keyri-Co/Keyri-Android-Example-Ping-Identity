package com.keyri.examplepingidentity.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.keyri.examplepingidentity.R
import com.keyri.examplepingidentity.databinding.ActivityLoginBinding
import com.keyri.examplepingidentity.ui.main.MainActivity
import com.keyrico.keyrisdk.Keyri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModel<LoginViewModel>()

    private val keyri by lazy(::Keyri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            bLogin.setOnClickListener {
                val email = etEmail.getNotEmptyText()
                val password = etPassword.getNotEmptyText()

                if (email != null && password != null) {
                    lifecycleScope.launchWhenCreated {
                        val clientId = getString(R.string.client_id)
                        val clientSecret = getString(R.string.client_secret)
                        val environmentID = getString(R.string.environment_id)
                        val tokenEndpoint = getString(R.string.token_endpoint)

                        viewModel.getAccessTokenWithBasic(clientId, clientSecret, tokenEndpoint)
                            .onEach { accessToken ->
                                val user =
                                    viewModel.getUser(email, environmentID, accessToken).first()

                                val timestampNonce =
                                    "${System.currentTimeMillis()}_${Random.nextInt()}"
                                val signature = keyri.getUserSignature(email, timestampNonce)

                                val payload = JSONObject().apply {
                                    put("username", user.username)
                                    put("timestamp_nonce", timestampNonce)
                                    put("userSignature", signature)
                                }.toString()

                                val intent = Intent().apply {
                                    putExtra(MainActivity.KEY_EMAIL, email)
                                    putExtra(MainActivity.KEY_PAYLOAD, payload)
                                }

                                setResult(RESULT_OK, intent)
                                finish()
                            }
                            .handleErrors()
                            .collect()
                    }
                }
            }
        }
    }

    private fun EditText.getNotEmptyText(): String? {
        return text?.takeIf { it.isNotEmpty() }?.toString()
    }

    private fun <T> Flow<T>.handleErrors(): Flow<T> = catch { e ->
        Log.e("Keyri example", e.message.toString())

        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
    }
}
