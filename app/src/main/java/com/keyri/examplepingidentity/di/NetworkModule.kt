package com.keyri.examplepingidentity.di

import com.keyri.examplepingidentity.network.FlowResultCallAdapterFactory
import com.keyri.examplepingidentity.repository.AuthService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { provideAuthService(get()) }
    single { provideRetrofit(get()) }
    single { provideOkHttpClient() }
}

private fun provideAuthService(retrofit: Retrofit) = retrofit.create(AuthService::class.java)

private fun provideRetrofit(client: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl("https://api.pingone.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(FlowResultCallAdapterFactory())
        .build()

private fun provideOkHttpClient(): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
