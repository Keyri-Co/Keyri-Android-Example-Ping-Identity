package com.keyri.examplepingidentity.data

import android.content.Context
import android.util.Base64
import com.google.gson.Gson
import com.keyri.examplepingidentity.R
import java.io.IOException
import java.io.InputStream
import java.security.SecureRandom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Config(private val context: Context) {

    private var cachedData: ConfigData? = null
    var serverData: ServerConfig? = null
    var codeVerifier: String? = null

    fun readAuthConfig(): Flow<ConfigData> {
        return if (cachedData != null) {
            flowOf(requireNotNull(cachedData))
        } else {
            codeVerifier = generateCodeVerifier()
            val configString =
                loadJSONFromAssets(context.resources.openRawResource(R.raw.auth_config))
            this@Config.cachedData = Gson().fromJson(configString, ConfigData::class.java)

            flowOf(requireNotNull(cachedData))
        }
    }

    private fun generateCodeVerifier(): String {
        val sr = SecureRandom()
        val code = ByteArray(32)

        sr.nextBytes(code)

        return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun loadJSONFromAssets(inputStream: InputStream): String? {
        var json: String? = null

        try {
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            json = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream.close()
        }

        return json
    }

    fun storeConfig(serverConfig: ServerConfig) {
        serverData = serverConfig
    }
}
