package com.mukhutdinov.bulat.revoluttest

import android.app.Application
import android.util.Log
import com.mukhutdinov.bulat.revoluttest.di.InjectionModule
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        setRxErrorHandler()

        startKoin {
            androidContext(this@App)
            modules(InjectionModule.module)
        }
    }

    private fun setRxErrorHandler() {
        val oldHandler = RxJavaPlugins.getErrorHandler()
        RxJavaPlugins.setErrorHandler { throwable ->
            when (throwable) {
                is UndeliverableException -> Log.d(TAG, "subscription was cancelled")
                is InterruptedException -> Log.d(TAG, "some blocking code was interrupted by a dispose call")
                else -> acceptRxThrowable(oldHandler, throwable)
            }
        }
    }

    private fun acceptRxThrowable(handler: Consumer<in Throwable>?, throwable: Throwable) {
        if (handler != null) {
            handler.accept(throwable)
        } else {
            Thread.currentThread()
                .uncaughtExceptionHandler
                .uncaughtException(Thread.currentThread(), throwable)
        }
    }

    companion object {
        private const val TAG = "App"
    }
}