package com.renew.survey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.renew.survey.R
import com.renew.survey.databinding.ActivityDashboardBinding
import com.renew.survey.databinding.ActivityMainBinding

class DashboardActivity : BaseActivity() {
    lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_dashboard)
        binding= ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*binding.btnSync.setOnClickListener {
            Intent(this,LanguageActivity::class.java).apply {
                startActivity(this)
            }
        }*/
        binding.btnContinue.setOnClickListener {
            Intent(this,FormsDetailsActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}