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
import com.renew.survey.room.entities.TestQuestionLanguage
import kotlinx.coroutines.launch

class UserRoleActivity : BaseActivity() ,FormTypeAdapter.ClickListener{
    lateinit var binding: ActivityUserRoleBinding
    var testquestionList: List<TestQuestionLanguage> = arrayListOf()

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
            Log.d("checkUser",preferenceManager.getUserdata().user_type)
            val forms = if (preferenceManager.getUserdata().user_type=="USER") {
                AppDatabase.getInstance(this@UserRoleActivity).formDao().getAllFormsWithLanguage(preferenceManager.getLanguage(),preferenceManager.getProject().id!!,)
            } else {
                AppDatabase.getInstance(this@UserRoleActivity).formDao()
                    .getAssignedFormsWithLanguage(
                        preferenceManager.getLanguage(), preferenceManager.getProject().id!!,
                        preferenceManager.getUserId()?.toInt()!!
                    )
            }
            binding.recyclerView.adapter=FormTypeAdapter(this@UserRoleActivity,forms,this@UserRoleActivity)
            Log.d("formDetails123",preferenceManager.getLanguage().toString()+"--"+ preferenceManager.getProject().id)
        }
    }

    override fun onFormSelected(form: FormWithLanguage) {
        // Log.d("formDetails",form.toString()+"${form.tbl_forms_id}-${preferenceManager.getProject().id}")
        preferenceManager.saveForm(form)
        val job = lifecycleScope.launch {
            testquestionList = AppDatabase.getInstance(this@UserRoleActivity).formDao()
                .getAllTestQuestions(preferenceManager.getLanguage(), form.tbl_forms_id)
            //Log.d("formDetails1", form.toString())
        }

        Log.d("formDetails", form.toString() + "${form.tbl_forms_id}-${preferenceManager.getProject().id}-${testquestionList}")

        job.invokeOnCompletion {
            Log.d("formDetails2", testquestionList.toString())

       if (testquestionList.isEmpty()) {
           Log.d("trainingtestlist1","listpresent")
            Intent(this, DashboardActivity::class.java).apply {
                startActivity(this)
            }
        } else {
            val retrievedList = preferenceManager.getTrainingState("trainingState")
               // Log.d("trainingtestlist",retrievedList.toString())
            if (retrievedList?.contains("${form.tbl_forms_id}-${preferenceManager.getProject().id}-${preferenceManager.getUserId()}") != true && form.tbl_forms_id != 4) {
                Log.d("trainingtestlist2",retrievedList.toString())
                Intent(this, TrainingActivity::class.java).apply {
                    putExtra(
                        "trainingInfo",
                        preferenceManager.getProject().id.toString() + form.tbl_forms_id
                    )
                    startActivity(this)
                }
            } else {
                lifecycleScope.launch {
                    val cm = AppDatabase.getInstance(this@UserRoleActivity).formDao()
                        .getTrainings(form.tbl_forms_id)
                }
                Log.d("trainingtestlist3",retrievedList.toString())
                Intent(this, DashboardActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }
        /*Intent(this,DashboardActivity::class.java).apply {
            startActivity(this)
        }*/
    }
}