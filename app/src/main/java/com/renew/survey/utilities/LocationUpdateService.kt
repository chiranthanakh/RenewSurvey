package com.renew.survey.utilities

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LocationUpdateService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle
       // location update here, similar to what you were doing in the activity's onSuccess method
        return START_NOT_STICKY
    }
}