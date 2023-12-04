package com.renew.survey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.renew.survey.databinding.ActivityProjectBinding

class ProjectActivity : AppCompatActivity() {
    lateinit var binding: ActivityProjectBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

       binding.llProject2.setOnClickListener{
            val intent = Intent(this@ProjectActivity, UserRoleActivity::class.java)
            startActivity(intent)
        }
        binding.llProject1.setOnClickListener{
            val intent = Intent(this@ProjectActivity, UserRoleActivity::class.java)
            startActivity(intent)
        }

    }
}