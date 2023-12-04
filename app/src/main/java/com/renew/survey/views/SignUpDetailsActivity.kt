package com.renew.survey.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.renew.survey.databinding.ActivitySignUpDetailsBinding

class SignUpDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}