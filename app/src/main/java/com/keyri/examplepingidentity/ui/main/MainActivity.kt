package com.keyri.examplepingidentity.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.keyri.examplepingidentity.R
import com.keyri.examplepingidentity.databinding.ActivityMainBinding
import com.keyri.examplepingidentity.ui.login.LoginActivity
import com.keyri.examplepingidentity.ui.register.RegisterActivity
import com.keyrico.keyrisdk.ui.auth.AuthWithScannerActivity

class MainActivity : AppCompatActivity() {

    private val easyKeyriAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val text = if (it.resultCode == RESULT_OK) "Authenticated" else "Failed to authenticate"

            showMessage(findViewById(R.id.llRoot), text)
        }

    private val authLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val payload = result.data?.getStringExtra(KEY_PAYLOAD)
            val email = result.data?.getStringExtra(KEY_EMAIL)

            if (result.resultCode == RESULT_OK && email != null && payload != null) {
                keyriAuth(email, payload)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            bPingLogin.setOnClickListener {
                startAuthActivity(LoginActivity::class.java)
            }

            bPingRegister.setOnClickListener {
                startAuthActivity(RegisterActivity::class.java)
            }
        }
    }

    private fun keyriAuth(publicUserId: String?, payload: String) {
        val intent = Intent(this, AuthWithScannerActivity::class.java).apply {
            putExtra(AuthWithScannerActivity.APP_KEY, "IT7VrTQ0r4InzsvCNJpRCRpi1qzfgpaj")
            putExtra(AuthWithScannerActivity.PUBLIC_USER_ID, publicUserId)
            putExtra(AuthWithScannerActivity.PAYLOAD, payload)
        }

        easyKeyriAuthLauncher.launch(intent)
    }

    private fun showMessage(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }

    private fun startAuthActivity(clazz: Class<*>) {
        Intent(this@MainActivity, clazz).let(authLauncher::launch)
    }

    companion object {
        const val KEY_EMAIL = "EMAIL"
        const val KEY_PAYLOAD = "PAYLOAD"
    }
}
