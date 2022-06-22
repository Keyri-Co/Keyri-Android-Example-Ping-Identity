package com.keyri.examplepingidentity.di

import com.keyri.examplepingidentity.ui.login.LoginViewModel
import com.keyri.examplepingidentity.ui.register.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get(), get()) }
}
