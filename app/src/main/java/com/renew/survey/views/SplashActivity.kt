package com.renew.survey.views

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.renew.survey.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity:BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }
        setContentView(binding.root)
        lifecycleScope.launch {
            delay(2000)
            if (preferenceManager.getToken()!=""){
                Intent(this@SplashActivity, DashboardActivity::class.java).apply {
                    startActivity(this)
                }
            }else{
                Intent(this@SplashActivity, LoginActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }
}