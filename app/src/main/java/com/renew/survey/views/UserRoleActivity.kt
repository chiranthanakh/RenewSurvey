package com.renew.survey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.renew.survey.databinding.ActivityUserRoleBinding

class UserRoleActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserRoleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llSurvey.setOnClickListener{
            val intent = Intent(this@UserRoleActivity, DashboardActivity::class.java)
            startActivity(intent)
        }
        binding.llDistrubutor.setOnClickListener{
            val intent = Intent(this@UserRoleActivity, DashboardActivity::class.java)
            startActivity(intent)
        }
        binding.llMonitoring.setOnClickListener{
            val intent = Intent(this@UserRoleActivity, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}