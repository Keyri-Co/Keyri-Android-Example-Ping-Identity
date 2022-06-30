package com.keyri.examplepingidentity.di

import com.keyri.examplepingidentity.repository.auth.AuthRepository
import com.keyri.examplepingidentity.repository.auth.DefaultAuthRepository
import org.koin.dsl.module

val appModule = module {
    single<AuthRepository> { DefaultAuthRepository(get()) }
}
