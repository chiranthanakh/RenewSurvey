package com.renew.survey.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.renew.survey.R
import com.renew.survey.databinding.ActivityDashboardBinding
import com.renew.survey.databinding.ActivityFormsDetailsBinding

class FormsDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityFormsDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_forms_details)
        binding= ActivityFormsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}