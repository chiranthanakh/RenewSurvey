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
    var testquestionList: MutableList<TestQuestionLanguage> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllForms()
        initilize()
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
    }

    private fun initilize() {
        binding.tvProjectName.text = preferenceManager.getProject().title
        binding.tvProjectCode.text = "Project Code : "+preferenceManager.getProject().project_code
        binding.tvProjectDivision.text = "Division : "+preferenceManager.getProject().mst_divisions_id.toString()
        binding.tvProjectCategory.text = "Category : " + preferenceManager.getProject().mst_categories_id.toString()
        lifecycleScope.launch {
            binding.tvProjectSate.text = "State : " +AppDatabase.getInstance(this@UserRoleActivity).placesDao()
                .getStates(preferenceManager.getProject().mst_state_id)?.lowercase()

            binding.tvProjectDivision.text = "Division :${AppDatabase.getInstance(this@UserRoleActivity).formDao()
                .getDivisions(preferenceManager.getProject().mst_divisions_id).division_name}"

            binding.tvProjectCategory.text = "Category : ${AppDatabase.getInstance(this@UserRoleActivity).formDao()
                .getCategories(preferenceManager.getProject().mst_categories_id).category_name}"
        }
    }

    fun getAllForms(){
        lifecycleScope.launch {
            val forms = if (preferenceManager.getUserdata().user_type=="USER") {
                AppDatabase.getInstance(this@UserRoleActivity).formDao().getAllFormsWithLanguage(preferenceManager.getLanguage(),
                    preferenceManager.getProject().id!!,
                    preferenceManager.getProject().mst_divisions_id,preferenceManager.getProject().mst_categories_id)
            } else {
                AppDatabase.getInstance(this@UserRoleActivity).formDao()
                    .getAssignedFormsWithLanguage(
                        preferenceManager.getLanguage(), preferenceManager.getProject().id!!,
                        preferenceManager.getUserId()?.toInt()!!,preferenceManager.getProject().mst_divisions_id,
                        preferenceManager.getProject().mst_categories_id
                    )
            }
            Log.d("formsDetailsTest",forms.toString())
            binding.recyclerView.adapter=FormTypeAdapter(this@UserRoleActivity,forms,this@UserRoleActivity)
        }
    }

    override fun onFormSelected(form: FormWithLanguage) {
        preferenceManager.saveForm(form)
        val job = lifecycleScope.launch {
            val testDetails = AppDatabase.getInstance(this@UserRoleActivity).formDao().getTest(preferenceManager.getLanguage(),form.tbl_forms_id,preferenceManager.getProject().mst_categories_id)
            testquestionList.clear()
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
                    putExtra("trainingInfo", preferenceManager.getProject().id.toString() + form.tbl_forms_id)
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