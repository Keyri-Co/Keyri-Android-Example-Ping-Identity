package com.keyri.examplepingidentity.di

import android.content.Context
import android.content.SharedPreferences
import com.keyri.examplepingidentity.repository.auth.AuthRepository
import com.keyri.examplepingidentity.repository.auth.DefaultAuthRepository
import org.koin.dsl.module

val appModule = module {
    single<AuthRepository> { DefaultAuthRepository(get()) }
    single { getSharedPreferences(get()) }
}

private fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("Keyri Ping example", Context.MODE_PRIVATE)
}
