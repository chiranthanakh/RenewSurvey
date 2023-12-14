package com.renew.survey.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.renew.survey.databinding.ActivityDashboardBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.utilities.ApiInterface
import com.renew.survey.utilities.UtilMethods
import kotlinx.coroutines.launch
import org.json.JSONObject

class DashboardActivity : BaseActivity() {
    lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_dashboard)
        binding= ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            val totalCount=AppDatabase.getInstance(this@DashboardActivity).formDao().getTotalSurvey()
            val pendingCount=AppDatabase.getInstance(this@DashboardActivity).formDao().getTotalPendingSurvey()
            val doneCount=AppDatabase.getInstance(this@DashboardActivity).formDao().getTotalDoneSurvey()

            binding.tvTotalSurvey.text = "$totalCount"
            binding.tvPendingSurvey.text = "$pendingCount"
            binding.tvSyncDone.text = "$doneCount"
            //binding.tvTotalSurvey.text = "${AppDatabase.getInstance(this).formDao().getTotalSurvey()}"
        }
        binding.tvProject.text=preferenceManager.getProject().project_code
        binding.btnSync.setOnClickListener {
            lifecycleScope.launch {
                binding.progressLayout.visibility=View.VISIBLE
                val answers=AppDatabase.getInstance(this@DashboardActivity).formDao().getAllUnsyncedAnswers()
                for (a in answers){
                    val commonAns=AppDatabase.getInstance(this@DashboardActivity).formDao().getCommonAnswers(a.id!!)
                    val dynamicAns=AppDatabase.getInstance(this@DashboardActivity).formDao().getDynamicAns(a.id)
                    a.dynamicAnswersList=dynamicAns
                    a.commonAnswersEntity=commonAns
                }
                /*val jsonData=gson.toJson(answers)
                Log.e("data",jsonData)*/
                if(answers.size>0){
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
            Intent(this,FormsDetailsActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}