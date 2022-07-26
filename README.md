# Overview

This module contains example of implementation [Keyri](https://keyri.com) with Ping Identity.

## Contents

* [Requirements](#Requirements)
* [Permissions](#Permissions)
* [Keyri Integration](#Keyri-Integration)
* [Ping Identity Integration](#Ping-Identity-Integration)
* [Authentication](#Authentication)

## Requirements

* Android API level 23 or higher
* AndroidX compatibility
* Kotlin coroutines compatibility

Note: Your app does not have to be written in kotlin to integrate this SDK, but must be able to
depend on kotlin functionality.

## Permissions

Open your app's `AndroidManifest.xml` file and add the following permission:

```xml

<uses-permission android:name="android.permission.INTERNET" />
```

## Keyri Integration

* Add the JitPack repository to your root build.gradle file:

```groovy
allprojects {
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
}
```

* Add SDK dependency to your build.gradle file and sync project:

```kotlin
dependencies {
    // ...
  implementation("com.github.Keyri-Co.keyri-android-whitelabel-sdk:keyrisdk:$latestKeyriVersion")
  implementation("com.github.Keyri-Co.keyri-android-whitelabel-sdk:scanner:$latestKeyriVersion")
}
```

## Ping Identity Integration

Create Ping project and add worker app. Get client_id, client_secret, environment_id,
token_endpoint. Add all this params to your project as following:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="client_id">CLIENT_ID</string>
    <string name="client_secret">CLIENT_SECRET</string>
    <string name="environment_id">ENVIRONMENT_ID</string>
    <string name="population_id">POPULATION_ID</string>
    <string name="token_endpoint">TOKEN_ENDPOINT</string>
    <string name="users">USERS_ENDPOINT</string>
</resources>
```

where:

- `client_id`: Your application's client UUID. You can also find this value at Application's
  Settings right under the Application name.
- `client_secret`: Your application's client secret. You can also find this value at Application's
  Settings page.
- `environment_id`: Your application's Environment ID. You can find this value at your Application's
  Settings under **Configuration** tab from the admin console(
  extract `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`
  string that specifies the environment 128-bit universally unique
  identifier ([UUID](https://tools.ietf.org/html/rfc4122)) right from `https://auth.pingone
  .com/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx/as/authorize` *AUTHORIZATION URL* ). Or from the *
  Settings* main menu (*ENVIRONMENT ID* variable).
- `population_id`: Users Population ID. You can find this value at your Identities -> Populations
  page.
- `token_endpoint`: Your token endpoint. Needed to get access token for API.
- `users_endpoint`: Get users endpoint. Needed to get list of Users.

### Prerequisites

You will need your own PingOne for Customers tenant. You
can [sign up for a trial](https://developer.pingidentity.com/).

* PingOne for Customers Account - If you don’t have an existing one, please register it.
* An Worker Application (must be enabled).

Update [auth_config.json](app/src/main/res/values/ping.xml) with your tenant's variables.

### Register your Application Connection

Once you have your own tenant, use the PingOne for Customers Admin Console to add an application
connection.

The Applications page shows the new application. Click the toggle switch to enable the application.
View the details of your application and make note of its **Client ID**.

### Get your Environment ID

To get your **Environment ID**, in the Admin Console, click Settings, then Environment, then
Properties. The Properties page shows the environment ID.

## Authentication

Get access token and after use this
endpoint `https://api.pingone.com/v1/environments/{environmentId}/users` to create new user.
Pass [CreateUserBody](app/src/main/java/com/keyri/examplepingidentity/data/create_user/request/CreateUserBody.kt)
, `bearerToken` and `environmentId`.

After receiving user data, create payload as showed
in [RegisterActivity](app/src/main/java/com/keyri/examplepingidentity/ui/register/RegisterActivity.kt)
and pass it to the Keyri.

Authenticate with Keyri. In the next showing `AuthWithScannerActivity` with providing
`publicUserId` and `payload`.

```kotlin
private val easyKeyriAuthLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // Process authentication result
    }

private fun authenticate() {
    viewModel.getAccessTokenWithBasic(clientId, clientSecret, tokenEndpoint)
        .onEach { accessToken ->
            val timestamp = System.currentTimeMillis()

            val user = viewModel.register(
                givenName,
                family,
                email,
                username + "_$timestamp",
                password,
                populationID,
                environmentID,
                accessToken
            ).first()

            val associationKey = keyri.getAssociationKey(user.email)

            val data = JSONObject().apply {
                put("timestamp", timestamp)
                put("username", username)
                put("userID", user.username)
            }.toString()

            val userSignature = keyri.getUserSignature(email, data)

            val payload = JSONObject().apply {
                put("token", Gson().toJson(accessToken))
                put("associationKey", associationKey)
                put("data", data)
                put("userSignature", userSignature)
            }.toString()

            keyriAuth(user.email, payload)
        }
}

private fun keyriAuth(publicUserId: String?, payload: String) {
    val intent = Intent(this, AuthWithScannerActivity::class.java).apply {
        putExtra(AuthWithScannerActivity.APP_KEY, BuildConfig.APP_KEY)
        putExtra(AuthWithScannerActivity.PUBLIC_USER_ID, publicUserId)
        putExtra(AuthWithScannerActivity.PAYLOAD, payload)
    }

    easyKeyriAuthLauncher.launch(intent)
}
```
