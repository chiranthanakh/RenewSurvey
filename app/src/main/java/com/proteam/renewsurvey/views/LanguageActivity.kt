package com.example.renewsurvey.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import com.example.renewsurvey.MainActivity
import com.example.renewsurvey.R

class LanguageActivity : AppCompatActivity() {
    var ll_hindi: LinearLayout? = null
    var ll_marati: LinearLayout? = null
    var ll_english: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        ll_hindi = findViewById(R.id.ll_hindi)
        ll_marati = findViewById(R.id.ll_marathi)
        ll_english = findViewById(R.id.ll_english)

        ll_hindi?.setOnClickListener{
            val intent = Intent(this@LanguageActivity, ProjectActivity::class.java)
            startActivity(intent)
        }
        ll_marati?.setOnClickListener{
            val intent = Intent(this@LanguageActivity, ProjectActivity::class.java)
            startActivity(intent)
        }
        ll_english?.setOnClickListener{
            val intent = Intent(this@LanguageActivity, ProjectActivity::class.java)
            startActivity(intent)
        }

    }
}