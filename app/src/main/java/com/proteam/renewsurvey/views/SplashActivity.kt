package com.proteam.renewsurvey.views

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.renewsurvey.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration

class SplashActivity:BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            delay(2000)
            Intent(this@SplashActivity,LoginActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}