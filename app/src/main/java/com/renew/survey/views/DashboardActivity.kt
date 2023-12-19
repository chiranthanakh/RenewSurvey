package com.renew.survey.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.renew.survey.databinding.ActivityDashboardBinding
import com.renew.survey.databinding.NaviagationLayoutBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.utilities.ApiInterface
import com.renew.survey.utilities.UtilMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class DashboardActivity : BaseActivity() {
    lateinit var binding: ActivityDashboardBinding
    lateinit var bindingNav: NaviagationLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_dashboard)
        binding= ActivityDashboardBinding.inflate(layoutInflater)
        bindingNav=binding.navLayout
        setContentView(binding.root)
        Log.e("ksdkhs","user ${preferenceManager.getUserId()}")
        binding.menuDrawer.setOnClickListener {
            if (binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.myDrawerLayout.closeDrawer(GravityCompat.START)
            }else{
                binding.myDrawerLayout.openDrawer(GravityCompat.START)
            }
        }
        Log.e("userdata",gson.toJson(preferenceManager.getUserdata()))
        bindingNav.name.setText(preferenceManager.getUserdata().full_name)
        bindingNav.mobile.setText(preferenceManager.getUserdata().mobile)
        bindingNav.email.setText(preferenceManager.getUserdata().email)
        Glide.with(this).load(preferenceManager.getUserdata().profile_photo).into(bindingNav.ivProfile)

        bindingNav.llChangePassword.setOnClickListener {
            Intent(this,ChangePasswordActivity::class.java).apply {
                startActivity(this)
            }
        }

        bindingNav.llLogout.setOnClickListener {
            preferenceManager.clear()
            lifecycleScope.launch {
                logout()
            }

        }
        /*lifecycleScope.launch {
            val totalCount=AppDatabase.getInstance(this@DashboardActivity).formDao().getTotalSurvey()
            val pendingCount=AppDatabase.getInstance(this@DashboardActivity).formDao().getTotalPendingSurvey()
            val doneCount=AppDatabase.getInstance(this@DashboardActivity).formDao().getTotalDoneSurvey()

            binding.tvTotalSurvey.text = "$totalCount"
            binding.tvPendingSurvey.text = "$pendingCount"
            binding.tvSyncDone.text = "$doneCount"
            //binding.tvTotalSurvey.text = "${AppDatabase.getInstance(this).formDao().getTotalSurvey()}"
        }*/
        binding.surveyType.text = preferenceManager.getForm().title
        binding.project.text=preferenceManager.getProject().project_code
        binding.btnSync.setOnClickListener {
            lifecycleScope.launch {

                val answers=AppDatabase.getInstance(this@DashboardActivity).formDao().getAllUnsyncedAnswers()
                for (a in answers){
                    val commonAns=AppDatabase.getInstance(this@DashboardActivity).formDao().getCommonAnswers(a.id!!)
                    val dynamicAns=AppDatabase.getInstance(this@DashboardActivity).formDao().getDynamicAns(a.id)
                    a.dynamicAnswersList=dynamicAns
                    a.commonAnswersEntity=commonAns
                    if (a.tbl_forms_id=="2"){
                        a.parent_survey_id=commonAns.parent_survey_id
                        a.tbl_project_survey_common_data_id=commonAns.tbl_project_survey_common_data_id
                    }
                }
                val jsonData=gson.toJson(answers)
                Log.e("data",jsonData)
                if(answers.size>0){
                    binding.progressLayout.visibility=View.VISIBLE
                    ApiInterface.getInstance()?.apply {
                        val response=syncSubmitForms(preferenceManager.getToken()!!,answers)
                        binding.progressLayout.visibility=View.GONE
                        if (response.isSuccessful){
                            Log.e("response","${response.body()}")
                            val json=JSONObject(response.body().toString())
                            UtilMethods.showToast(this@DashboardActivity,json.getString("message"))
                            if (json.getString("success")=="1"){
                                for (ans in answers){
                                    AppDatabase.getInstance(this@DashboardActivity).formDao().updateSync(ans.id!!)
                                }
                            }
                        }
                    }
                }else{
                    UtilMethods.showToast(this@DashboardActivity,"No data to sync")
                }
            }
        }
        binding.btnContinue.setOnClickListener {
            if (preferenceManager.getForm().tbl_forms_id!=1){
                Intent(this,SurveySelectActivity::class.java).apply {
                    startActivity(this)
                }
            }else {
                Intent(this,FormsDetailsActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.myDrawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }
    fun logout(){
        lifecycleScope.launch (Dispatchers.IO){
            AppDatabase.getInstance(this@DashboardActivity).clearAllTables()
        }
        Intent(this,LoginActivity::class.java).apply {
            flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            finishAffinity()
            startActivity(this)
        }
    }
}