package com.publication.dealer

import android.app.Application

import com.publication.dealer.modules.networkModule
import com.publication.dealer.modules.repoModule
import com.publication.dealer.modules.sharedPreferenceModule
import com.publication.dealer.modules.viewModelModule
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
