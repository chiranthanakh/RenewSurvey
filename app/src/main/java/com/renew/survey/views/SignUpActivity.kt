package com.renew.survey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.renew.survey.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.getBooleanExtra("user",false)){
            binding.layoutUniqueCode.visibility= View.GONE
        }else{
            binding.layoutUniqueCode.visibility= View.VISIBLE
        }


          binding.tvSubmitDetails.setOnClickListener{
            val intent = Intent(this@SignUpActivity, SignUpDetailsActivity::class.java)
            startActivity(intent)
        }
    }
}