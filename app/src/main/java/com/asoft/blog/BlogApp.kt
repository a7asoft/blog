package com.asoft.blog

import android.app.Application
import com.asoft.blog.utils.NetworkMonitoringUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BlogApp : Application(){

    var mNetworkMonitoringUtil: NetworkMonitoringUtil? = null

    override fun onCreate() {
        super.onCreate()

        mNetworkMonitoringUtil = NetworkMonitoringUtil(applicationContext)
        mNetworkMonitoringUtil!!.checkNetworkState()
        mNetworkMonitoringUtil!!.registerNetworkCallbackEvents()
    }
}