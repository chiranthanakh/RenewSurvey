package com.renew.survey.utilities

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService:FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val preferenceManager=PreferenceManager(applicationContext)
        Log.e("FCM_TOKEN",token)
        preferenceManager.saveFCMToken(token)
    }

}