package com.example.renewsurvey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.renewsurvey.MainActivity
import com.example.renewsurvey.R

class SignUpActivity : AppCompatActivity() {
    var tv_submit: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

         tv_submit = findViewById(R.id.tv_submit_details)

          tv_submit?.setOnClickListener{
            val intent = Intent(this@SignUpActivity, SignUpDetailsActivity::class.java)
            startActivity(intent)
        }
    }
}