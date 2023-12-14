package com.renew.survey.views

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.renew.survey.databinding.ActivitySplashBinding
import com.renew.survey.utilities.UtilMethods
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class SplashActivity:BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(2000)
            if (preferenceManager.getToken()!=""){
                if (isDataSyncedForTheDay()){
                    Intent(this@SplashActivity, LanguageActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(this)
                    }
                }else{
                    Intent(this@SplashActivity, SyncDataActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(this)
                    }
                }

            }else{
                Intent(this@SplashActivity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(this)
                }
            }
        }
    }
    fun isDataSyncedForTheDay():Boolean{
        val date= Date()
        return preferenceManager.getSync(UtilMethods.getFormattedDate(date))
    }
}