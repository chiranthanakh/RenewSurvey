package com.renew.survey.utilities

import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService:FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val preferenceManager=PreferenceManager(applicationContext)
        preferenceManager.saveFCMToken(token)
    }

}