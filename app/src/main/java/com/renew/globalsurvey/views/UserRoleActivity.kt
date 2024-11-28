package com.renew.globalsurvey.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renew.globalsurvey.adapter.FormTypeAdapter
import com.renew.globalsurvey.databinding.ActivityUserRoleBinding
import com.renew.globalsurvey.room.AppDatabase
import com.renew.globalsurvey.room.entities.FormWithLanguage
import com.renew.globalsurvey.room.entities.TestQuestionLanguage
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

    }
    fun getAllForms(){
        lifecycleScope.launch {
            Log.d("checkUser",preferenceManager.getUserdata().user_type)
            val forms = if (preferenceManager.getUserdata().user_type=="USER") {
                AppDatabase.getInstance(this@UserRoleActivity).formDao().getAllFormsWithLanguage(preferenceManager.getLanguage(),preferenceManager.getProject().id!!,)
            } else {
                Log.d("testprojectid",preferenceManager.getProject().id.toString())
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
        preferenceManager.saveForm(form)
        val job = lifecycleScope.launch {
            val testDetails = AppDatabase.getInstance(this@UserRoleActivity).formDao().getTest(preferenceManager.getLanguage(),form.tbl_forms_id,preferenceManager.getProject().mst_categories_id)
            Log.d("checktestdata",testDetails.toString())
            if(testDetails?.equals(null) == false){
                testquestionList = AppDatabase.getInstance(this@UserRoleActivity).formDao()
                    .getAllTestQuestions(preferenceManager.getLanguage(), testDetails.tbl_tests_id)
            }
        }

        job.invokeOnCompletion {
       if (testquestionList.isEmpty()) {
            Intent(this, DashboardActivity::class.java).apply {
                startActivity(this)
            }
        } else {
            val retrievedList = preferenceManager.getTrainingState("trainingState")
            if (retrievedList?.contains("${form.tbl_forms_id}-${preferenceManager.getProject().id}-${preferenceManager.getUserId()}") != true && form.tbl_forms_id != 4) {
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