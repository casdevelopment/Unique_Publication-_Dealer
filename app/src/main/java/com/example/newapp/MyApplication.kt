package com.example.newapp

import android.app.Application

import com.example.newapp.modules.networkModule
import com.example.newapp.modules.repoModule
import com.example.newapp.modules.sharedPreferenceModule
import com.example.newapp.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin {
            androidContext(this@MyApplication)
            modules(listOf(networkModule, repoModule, viewModelModule,sharedPreferenceModule))

        }
    }
}
