package com.rudra.ispnetworktool

import android.app.Application
import android.content.ComponentCallbacks2
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ISPNetworkToolApplication : Application() {
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // Establish thresholds for dynamic memory usage optimization
        if (level >= TRIM_MEMORY_UI_HIDDEN) {
            // App's UI is no longer visible, release non-critical resources
            System.gc() // Suggest garbage collection for background optimization
        }
    }
}
