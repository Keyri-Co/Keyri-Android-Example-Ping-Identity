package com.keyri.examplepingidentity.app

import android.app.Application
import com.keyri.examplepingidentity.di.appModule
import com.keyri.examplepingidentity.di.networkModule
import com.keyri.examplepingidentity.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LoginApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@LoginApp)
            modules(appModule, networkModule, viewModelModule)
        }
    }
}
