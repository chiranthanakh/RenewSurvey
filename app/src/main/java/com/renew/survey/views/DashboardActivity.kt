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
import com.renew.survey.request.MediaSyncReqItem
import com.renew.survey.room.AppDatabase
import com.renew.survey.utilities.ApiInterface
import com.renew.survey.utilities.UtilMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Date

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
        binding.cardDraft.setOnClickListener {
            Intent(this,DraftSelectActivity::class.java).apply {
                startActivity(this)
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
                    val dynamicAns=AppDatabase.getInstance(this@DashboardActivity).formDao().getDynamicAns(a.id!!)
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
                    binding.llProgress.visibility=View.VISIBLE
                    ApiInterface.getInstance()?.apply {
                        val response=syncSubmitForms(preferenceManager.getToken()!!,answers)
                        binding.llProgress.visibility=View.GONE
                        if (response.isSuccessful){
                            Log.e("response","${response.body()}")
                            val json=JSONObject(response.body().toString())
                            UtilMethods.showToast(this@DashboardActivity,json.getString("message"))
                            if (json.getString("success")=="1"){
                                syncMedia()
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
    fun syncMedia(){
        lifecycleScope.launch {
            val answers=AppDatabase.getInstance(this@DashboardActivity).formDao().getAllUnsyncedAnswers()
            for (a in answers){
                val commonAns=AppDatabase.getInstance(this@DashboardActivity).formDao().getCommonAnswers(a.id!!)
                val dynamicAns=AppDatabase.getInstance(this@DashboardActivity).formDao().getDynamicAns(a.id!!)
                a.dynamicAnswersList=dynamicAns
                a.commonAnswersEntity=commonAns
                if (a.tbl_forms_id=="2"){
                    a.parent_survey_id=commonAns.parent_survey_id
                    a.tbl_project_survey_common_data_id=commonAns.tbl_project_survey_common_data_id
                }
            }
            val mediaList= arrayListOf<MediaSyncReqItem>()
            for (a in answers){
                for (d in a.dynamicAnswersList){
                    if (d.answer!!.startsWith("/")){
                        mediaList.add(MediaSyncReqItem(a.app_unique_code,d.answer!!.substring(d.answer!!.lastIndexOf("/")+1),a.phase,d.tbl_form_questions_id.toString(),a.tbl_forms_id,a.tbl_projects_id,a.tbl_users_id,a.version,d.answer!!))
                    }
                }
            }
            val surveyImagesParts: Array<MultipartBody.Part?> = arrayOfNulls<MultipartBody.Part>(mediaList.size)
            if (mediaList.size>0) {
                for (i in mediaList.indices) {
                    val file = File(mediaList[i].path)
                    val mimeType = UtilMethods.getMimeType(file)
                    val surveyBody = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)
                    surveyImagesParts[i] = MultipartBody.Part.createFormData("files",mediaList[i].file_name, surveyBody)
                }
                val jsonData=gson.toJson(mediaList)
                Log.e("params",jsonData.toString())
                binding.llProgress.visibility=View.VISIBLE
                binding.progressMessage.text="Synchronizing media with server"
                ApiInterface.getInstance()?.apply {
                    val datapart=RequestBody.create(MultipartBody.FORM, gson.toJson(mediaList))
                    val response=syncMediaFiles(preferenceManager.getToken()!!,datapart,surveyImagesParts)
                    binding.llProgress.visibility=View.GONE
                    if (response.isSuccessful){
                        val jsonObject=JSONObject(response.body().toString())
                        Log.e("response",jsonObject.toString())
                        UtilMethods.showToast(this@DashboardActivity,jsonObject.getString("message"))
                        if (jsonObject.getString("success")=="1"){

                        }
                    }
                }
            }
        }
    }
}