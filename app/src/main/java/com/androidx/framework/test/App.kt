package com.androidx.framework.test

import android.app.Application
import com.androidx.framework.logic.network.NetworkUtils

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        NetworkUtils.init(this)
    }
}