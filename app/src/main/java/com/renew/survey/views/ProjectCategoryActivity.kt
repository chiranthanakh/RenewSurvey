package com.renew.survey.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.renew.survey.databinding.ActivityProjectCategoryBinding

class ProjectCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cvCbs.setOnClickListener{
            Intent(this,LanguageActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.cvNbs.setOnClickListener{
            Intent(this,LanguageActivity::class.java).apply {
                startActivity(this)
            }
        }

    }
}