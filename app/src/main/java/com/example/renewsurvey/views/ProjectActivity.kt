package com.example.renewsurvey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.example.renewsurvey.R

class ProjectActivity : AppCompatActivity() {
    var ll_project1: LinearLayout? = null
    var ll_project2: LinearLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        ll_project1 = findViewById(R.id.ll_project1)
        ll_project2 = findViewById(R.id.ll_project2)

        ll_project2?.setOnClickListener{
            val intent = Intent(this@ProjectActivity, UserRoleActivity::class.java)
            startActivity(intent)
        }
        ll_project1?.setOnClickListener{
            val intent = Intent(this@ProjectActivity, UserRoleActivity::class.java)
            startActivity(intent)
        }

    }
}