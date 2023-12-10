package com.renew.survey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renew.survey.adapter.FormTypeAdapter
import com.renew.survey.databinding.ActivityUserRoleBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.FormWithLanguage
import kotlinx.coroutines.launch

class UserRoleActivity : BaseActivity() ,FormTypeAdapter.ClickListener{
    lateinit var binding: ActivityUserRoleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllForms()
        binding.recyclerView.layoutManager=LinearLayoutManager(this)

        /*binding.llSurvey.setOnClickListener{
            val intent = Intent(this@UserRoleActivity, DashboardActivity::class.java)
            startActivity(intent)
        }
        binding.llDistrubutor.setOnClickListener{
            val intent = Intent(this@UserRoleActivity, DashboardActivity::class.java)
            startActivity(intent)
        }
        binding.llMonitoring.setOnClickListener{
            val intent = Intent(this@UserRoleActivity, DashboardActivity::class.java)
            startActivity(intent)
        }*/
    }
    fun getAllForms(){
        lifecycleScope.launch {
            val forms=AppDatabase.getInstance(this@UserRoleActivity).formDao().getAllFormsWithLanguage(preferenceManager.getLanguage())
            binding.recyclerView.adapter=FormTypeAdapter(this@UserRoleActivity,forms,this@UserRoleActivity)
        }
    }

    override fun onFormSelected(form: FormWithLanguage) {
        Intent(this,DashboardActivity::class.java).apply {
            startActivity(this)
        }
    }
}