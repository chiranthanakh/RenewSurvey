package com.renew.survey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.renew.survey.R
import com.renew.survey.databinding.ActivitySyncDataBinding

class SyncDataActivity : BaseActivity() {
    lateinit var binding:ActivitySyncDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySyncDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnContinue.setOnClickListener {
            Intent(this,LanguageActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}