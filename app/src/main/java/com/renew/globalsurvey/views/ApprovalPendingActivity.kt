package com.renew.globalsurvey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.renew.globalsurvey.databinding.ActivityApprovalPendingBinding

class ApprovalPendingActivity : AppCompatActivity() {
    lateinit var binding:ActivityApprovalPendingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityApprovalPendingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnContinue.setOnClickListener {
            Intent(this,LoginActivity::class.java).apply {
                finishAffinity()
                startActivity(this)
            }
        }
    }
}