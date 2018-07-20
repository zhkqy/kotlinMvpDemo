package com.yilong.mvp

import android.app.Application
import android.content.Context

class ZKApplication : Application() {

    override fun onCreate() {
        context = this.applicationContext
        super.onCreate()
    }

    companion object {
        var context: Context? = null
    }
}