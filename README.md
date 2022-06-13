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
    implementation("com.github.Keyri-Co:keyri-android-whitelabel-sdk:$latestKeyriVersion")
}
```

## Ping Identity Integration

* Create [auth_config.json](app/src/main/res/raw/auth_config.json) file in your resources/raw directory.
  It should looks like:

```json
{
  "environment_id": "{env_id}",
  "client_id": "{client_id}",
  "redirect_uri": "{redirect_schema}",
  "authorization_scope": "{auth_scopes}",
  "discovery_uri": "https://auth.pingone.com/%s/as/.well-known/openid-configuration",
  "token_method": "{CLIENT_SECRET_POST || CLIENT_SECRET_BASIC || NONE}",
  "client_secret": "{client_secret}"
}
```

where

- `environment_id`: *Required*. Your application's Environment ID. You can find this value at your
  Application's Settings under
  **Configuration** tab from the admin console( extract `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`
  string that specifies the environment 128-bit universally unique
  identifier ([UUID](https://tools.ietf.org/html/rfc4122)) right from `https://auth.pingone
  .com/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx/as/authorize`
  *AUTHORIZATION URL* ). Or from the *Settings* main menu (*ENVIRONMENT ID* variable)
- `client_id`: *Required*. Your application's client UUID. You can also find this value at
  Application's Settings right under the Application name.
- `redirect_uri`: *Required*. The URL to which the PingOne will redirect the user's browser after
  authorization has been granted by the user. *REDIRECT URLS* values corresponds to this data. The
  Access and ID Token will be available in the hash fragment of this URL.
- `authorization_scope`:  standard OIDC or PingOne custom scopes, separated by a space which you
  want to request authorization for.
  [PingOne platform scopes](https://apidocs.pingidentity.com/pingone/customer/v1/api/auth/p1-a_AccessServices/#PingOne-platform-scopes-and-endpoint-operations)
  are configured under "Access" tab in PingOne Admin Console
- `discovery_uri`: *Required*. The URL describe App configuration with allowed methods, system
  URL's, etc
- `token method`: *Required*. The login method what is ena,bled in Applicatin configuration,
  possible variants - CLIENT_SECRET_POST || CLIENT_SECRET_BASIC || NONE
- `client_secret`: *Required*. Your application's client secret. You can also find this value at
  Application's Settings page

### Prerequisites

You will need your own PingOne for Customers tenant. You
can [sign up for a trial](https://developer.pingidentity.com/).

* PingOne for Customers Account - If you don’t have an existing one, please register it.
* An OpenID Connect Application, configured in Native App mode. Also make sure that it is enabled
  plus redirect URL's and access grants by scopes are properly set.
* At least one user in the same environment as the application (not assigned)
*

Update [auth_config.json](app/src/main/res/raw/auth_config.json) with your tenant's variables

### Register your Application Connection

Once you have your own tenant, use the PingOne for Customers Admin Console to add an application
connection. To create the application connection:

1. Click **Connections**.
2. Click + **Application**.
3. Select the **Single Page App** type.
4. Click **Configure**.
5. Create the application profile by entering the following information:

* **Application name**: OIDC Authentication Android or other name

6. Click **Next**.
7. **Redirect URI**: The URL where dist/login/ will live. For
   example, https://www.example.com/login/
8. Click **Save and Continue**
9. At a minimum, add the following scope: **profile**
10. Click **Save and Close**

The Applications page shows the new application. Click the toggle switch to enable the application.
View the details of your application and make note of its **Client ID**.

11. **Edit** the Application (click the pencil icon)
12. On the Profile Tab of your new application, populate **SignOn URL** with the location that
    dist/login/ will live. For example, https://www.example.com/login/
13. On the Configuration Yab of your new application, populat **SignOff URLs** with the location
    that dist/logout/ will live. For example, https://www.example.com/logout/
14. Click **Save**

### Get your Environment ID

To get your **Environment ID**, in the Admin Console, click Settings, then Environment, then
Properties. The Properties page shows the environment ID.

### Create Test User

To create your test user:

1. Click **Users**.
2. Click + **Add User**.
3. At a minimum, specify a **username** such as example@example.com.
4. Click **Save**
5. View your new user and select **Reset Password**
6. Specify an initial password, such as **HelloW0rld!**.
7. Click **Save**

### Add [AppAuth-Android](https://github.com/openid/AppAuth-Android) dependency

In your module `build.gradle`:

```kotlin
dependencies {
    // ...
    implementation("net.openid:appauth:0.11.1")
}
```

## Authentication

Add possibility to handle redirections with `redirect_uri` which you define in `auth_config.json`:

```groovy
    defaultConfig {
    // ...

    // For redirect
    manifestPlaceholders = [
            'appAuthRedirectScheme': 'custom-scheme://redirect'
    ]
}
```

And in your `AndroidManifest.xml` add following `intent-filter`:

```xml

<activity android:name=".YourActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <data android:host="redirect" android:scheme="custom-scheme" />
    </intent-filter>
</activity>
```

Use following block of code to initialize AppAuth-Android and show authentication Web page:

```kotlin
val serviceConfig = AuthorizationServiceConfiguration(
    Uri.parse(config.serverData.authorizationEndpoint),
    Uri.parse(config.serverData.tokenEndpoint)
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

val authService = AuthorizationService(this@AuthActivity)
val authIntent = authService.getAuthorizationRequestIntent(authRequest)

withContext(Dispatchers.Main) {
    startActivityForResult(authIntent, 123)
}
```

You can find full example
here: [AuthActivity.kt](app/src/main/java/com/keyri/examplepingidentity/ui/MainActivity.kt). If you
want to customize flow check documentation
here: [AppAuth-Android](https://github.com/openid/AppAuth-Android#implementing-the-authorization-code-flow)
.

After retrieving code from `intent.dataString` or with `onActivityResult` get access token as
showing in [AuthViewModel.kt](app/src/main/java/com/keyri/examplepingidentity/ui/MainViewModel.kt).

Authenticate with Keyri. In the next showing `AuthWithScannerActivity` with providing
`publicUserId` and `payload`.

```kotlin
private val easyKeyriAuthLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // Process authentication result
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
