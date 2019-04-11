package com.mukhutdinov.bulat.revoluttest

import android.app.Application
import com.mukhutdinov.bulat.revoluttest.di.InjectionModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(InjectionModule.module)
        }
    }
}