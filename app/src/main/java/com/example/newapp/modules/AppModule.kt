package com.example.newapp.modules

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.example.newapp.SessionManager
import com.example.newapp.dashboard.DashBoardViewModel
import com.example.newapp.login.viewmodel.LoginViewModel
import com.example.newapp.network.repo.Repository
import com.example.newapp.network.retofit.provideOkHttpClient
import com.example.newapp.network.retofit.provideRetrofit
import com.example.newapp.network.retofit.provideRetrofitInterface
import com.example.newapp.util.AppConstants.SHARED_PREF_NAME
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repoModule = module {
    single {
        Repository(get())
    }
}

val viewModelModule= module{
    viewModel { LoginViewModel(get()) }
    viewModel { DashBoardViewModel(get()) }
}

val networkModule = module {
    factory { provideOkHttpClient(get()) }
    factory { provideRetrofitInterface(get()) }
    single { provideRetrofit(get()) }
}

val sharedPreferenceModule = module {
    single {
        provideSharedPreference(get())
    }
    single {
        provideEditor(get())
    }
    single {
        SessionManager(get())
    }
}

fun provideSharedPreference(appContext: Context): SharedPreferences {
    return appContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
}

fun provideEditor(sharedPreferences: SharedPreferences): Editor {
    return sharedPreferences.edit()
}
