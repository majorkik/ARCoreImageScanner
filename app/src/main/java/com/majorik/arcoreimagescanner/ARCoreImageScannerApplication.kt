package com.majorik.arcoreimagescanner

import android.app.Application
import com.majorik.arcoreimagescanner.di.appComponent
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class ARCoreImageScannerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Logger.addLogAdapter(AndroidLogAdapter())

        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                Logger.log(priority, tag, message, t)
            }
        })

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ARCoreImageScannerApplication)
            modules(appComponent)
        }
    }
}