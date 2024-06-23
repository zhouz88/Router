package com.zz.kerwingo

import android.app.Application
import com.zz.route_runtime.Router

class KerwinApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Router.init()
    }
}