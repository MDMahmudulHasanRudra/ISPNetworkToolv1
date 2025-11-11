package com.rudra.ispnetworktool.data.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.rudra.ispnetworktool.R

class NetworkToolService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = createNotification("Network tool is running")
        startForeground(1, notification)

        // In a real implementation, you would use the intent to decide which
        // tool to run and manage its lifecycle here.

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "network_tool_channel",
                "Network Tool Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String): Notification {
        return NotificationCompat.Builder(this, "network_tool_channel")
            .setContentTitle("ISP Network Tool")
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with a proper icon
            .build()
    }
}
