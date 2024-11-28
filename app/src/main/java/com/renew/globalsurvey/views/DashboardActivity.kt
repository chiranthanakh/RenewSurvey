package com.renew.globalsurvey.views

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.renew.globalsurvey.databinding.ActivityDashboardBinding
import com.renew.globalsurvey.databinding.NaviagationLayoutBinding
import com.renew.globalsurvey.request.MediaSyncReqItem
import com.renew.globalsurvey.room.AppDatabase
import com.renew.globalsurvey.room.entities.AssignedFilterSurveyEntity
import com.renew.globalsurvey.utilities.ApiInterface
import com.renew.globalsurvey.utilities.MyCustomDialog
import com.renew.globalsurvey.utilities.UtilMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class DashboardActivity : BaseActivity() {
    lateinit var binding: ActivityDashboardBinding
    lateinit var bindingNav: NaviagationLayoutBinding
    var list = arrayListOf<AssignedFilterSurveyEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_dashboard)
        binding= ActivityDashboardBinding.inflate(layoutInflater)
        bindingNav=binding.navLayout
        setContentView(binding.root)
        Log.e("ksdkhs","user ${preferenceManager.getUserId()}")
        if (preferenceManager.getForm().tbl_forms_id == 4) {
            bindingNav.llChangePassword.visibility = View.GONE
            bindingNav.llProject1.visibility = View.GONE
            getasigneddata()
        }
        binding.menuDrawer.setOnClickListener {
            if (binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.myDrawerLayout.closeDrawer(GravityCompat.START)
            }else{
                binding.myDrawerLayout.openDrawer(GravityCompat.START)
            }
        }
        bindingNav.llProject1.setOnClickListener {
            Intent(this,SignUpActivity::class.java).apply{
                startActivity(this)
            }
        }
        bindingNav.llSynch.setOnClickListener {
            Intent(this,SyncDataActivity::class.java).apply{
                startActivity(this)
            }
        }
        if (preferenceManager.getUserdata().user_type=="USER"){
            bindingNav.llProject1.visibility=View.GONE
        }
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val version = pInfo.versionName
            bindingNav.tvVersion.setText("Version $version")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
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

        bindingNav.llProfile.setOnClickListener{
            Intent(this,ProfileActivity::class.java).apply {
                startActivity(this)
            }
        }

        bindingNav.llLogout.setOnClickListener {
            MyCustomDialog.showDialog(this,"Sign out?","Are you sure, you want to logout?","SIGN OUT","CANCEL",true,object :MyCustomDialog.ClickListener{
                override fun onYes() {
                    preferenceManager.saveToken("")
                    lifecycleScope.launch {
                        logout()
                    }
                }

                override fun onNo() {

                }

            })
        }
        binding.cardDraft.setOnClickListener {
            if (draftCount==0){
                UtilMethods.showToast(this,"No draft survey found")
            }else{
                Intent(this,DraftSelectActivity::class.java).apply {
                    startActivity(this)
                }
            }

        }

        binding.surveyType.text = preferenceManager.getForm().title
        lifecycleScope.launch {
            val prjPhase=AppDatabase.getInstance(this@DashboardActivity).formDao().getProjectPhase(preferenceManager.getForm().tbl_forms_id,preferenceManager.getProject().id!!.toInt())
            binding.project.text=preferenceManager.getProject().project_code+" v"+prjPhase.release_version
        }

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
                for (a in answers){
                    for (d in a.dynamicAnswersList){
                        if (d.answer!!.startsWith("/")){
                            d.answer = d.answer!!.substring(d.answer!!.lastIndexOf("/")+1)
                        }
                    }
                }
                val jsonData=gson.toJson(answers)
                Log.e("dataAnswers",answers.toString())
                if(answers.size>0){
                   // syncMedia()
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
                                getCounts()
                            }else {
                                val data=json.getJSONObject("data")
                                if (data.getBoolean("is_access_disable")){
                                    preferenceManager.clear()
                                    Intent(this@DashboardActivity,LoginActivity::class.java).apply {
                                        finishAffinity()
                                        startActivity(this)
                                    }
                                }
                            }
                        }
                    }
                }else{
                    UtilMethods.showToast(this@DashboardActivity,"No data to sync")
                    syncMedia()
                }
            }
        }
        binding.btnContinue.setOnClickListener {
           // Log.d("testdcd2",preferenceManager.getForm().tbl_forms_id.toString())

            if (preferenceManager.getForm().tbl_forms_id!=1 && preferenceManager.getForm().tbl_forms_id!=4 ){
                Intent(this,SurveySelectActivity::class.java).apply {
                    startActivity(this)
                }
            } else if(preferenceManager.getForm().tbl_forms_id == 4) {
               // Log.d("testdcd2",list.toString())
                if (!list.isNullOrEmpty()) {
                    Intent(this, FormsDetailsActivity::class.java).apply {
                        putExtra("assigned", gson.toJson(list.get(0)))
                        startActivity(this)
                    }
                } else {
                    UtilMethods.showToast(this@DashboardActivity,"No Survey Found")
                }
            } else {
                Intent(this,FormsDetailsActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    private fun getasigneddata() {
        list.clear()
        lifecycleScope.launch(Dispatchers.IO) {
            list = AppDatabase.getInstance(this@DashboardActivity).formDao()
                .getAllFilteredAssignedSurvey(preferenceManager.getForm().tbl_forms_id, preferenceManager.getProject().tbl_projects_id) as ArrayList<AssignedFilterSurveyEntity>
            Log.d("testdcd",list.toString())
            list.forEach {
                Log.d("villageId",it.mst_village_id.toString())
                it.mst_village_name = AppDatabase.getInstance(this@DashboardActivity).placesDao()
                    .getVillage(it.mst_village_id)?.lowercase()
                it.mst_tehsil_name = AppDatabase.getInstance(this@DashboardActivity).placesDao()
                    .getTehsils(it.mst_tehsil_id)?.lowercase()
                it.mst_panchayat_name = it.mst_panchayat_id?.let { it1 ->
                    AppDatabase.getInstance(this@DashboardActivity).placesDao().getPanchayath(
                        it1
                    )?.lowercase()
                }
                it.mst_district_name =
                    AppDatabase.getInstance(this@DashboardActivity).placesDao()
                        .getDistricts(it.mst_district_id)?.lowercase()
                it.mst_state_name = AppDatabase.getInstance(this@DashboardActivity).placesDao()
                    .getStates(it.mst_state_id)?.lowercase()
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
            val answers=AppDatabase.getInstance(this@DashboardActivity).formDao().getAllUnsyncedMediaAnswers()
            val mediaList= arrayListOf<MediaSyncReqItem>()
            for (a in answers){
                val commonAns=AppDatabase.getInstance(this@DashboardActivity).formDao().getCommonAnswers(a.id!!)
                val dynamicAns=AppDatabase.getInstance(this@DashboardActivity).formDao().getDynamicAns(a.id!!)
                a.dynamicAnswersList=dynamicAns
                a.commonAnswersEntity=commonAns
                if (a.tbl_forms_id=="2"){
                    a.parent_survey_id=commonAns.parent_survey_id
                    a.tbl_project_survey_common_data_id=commonAns.tbl_project_survey_common_data_id
                }
                if (commonAns.font_photo_of_aadar_card.isNotEmpty()){
                    mediaList.add(MediaSyncReqItem(a.app_unique_code,commonAns.font_photo_of_aadar_card.substring(commonAns.font_photo_of_aadar_card.lastIndexOf("/")+1),a.phase,"",a.tbl_forms_id,a.tbl_projects_id,a.tbl_users_id,a.version,commonAns.font_photo_of_aadar_card))
                }
                if (commonAns.back_photo_of_aadhar_card.isNotEmpty()){
                    mediaList.add(MediaSyncReqItem(a.app_unique_code,commonAns.back_photo_of_aadhar_card.substring(commonAns.back_photo_of_aadhar_card.lastIndexOf("/")+1),a.phase,"",a.tbl_forms_id,a.tbl_projects_id,a.tbl_users_id,a.version,commonAns.back_photo_of_aadhar_card))
                }
                if (commonAns.photo_of_bill.isNotEmpty()){
                    mediaList.add(MediaSyncReqItem(a.app_unique_code,commonAns.photo_of_bill.substring(commonAns.photo_of_bill.lastIndexOf("/")+1),a.phase,"",a.tbl_forms_id,a.tbl_projects_id,a.tbl_users_id,a.version,commonAns.photo_of_bill))
                }
            }

            for (a in answers){
                for (d in a.dynamicAnswersList){
                    if (d.answer!!.startsWith("/")){
                        mediaList.add(MediaSyncReqItem(a.app_unique_code,d.answer!!.substring(d.answer!!.lastIndexOf("/")+1),a.phase,d.tbl_form_questions_id.toString(),a.tbl_forms_id,a.tbl_projects_id,a.tbl_users_id,a.version,d.answer!!))
                    }
                }
            }
            val surveyImagesParts: Array<MultipartBody.Part?> = arrayOfNulls<MultipartBody.Part>(mediaList.size)
           // Log.d("mediaListcheck",mediaList)
            if (mediaList.size>0) {
                try {
                    for (i in mediaList.indices) {
                        val file = File(mediaList[i].path)
                        if (file.exists()) {
                            val mimeType = UtilMethods.getMimeType(file)
                            val surveyBody = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)
                            surveyImagesParts[i] = MultipartBody.Part.createFormData("files[]",mediaList[i].file_name, surveyBody)
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

                val jsonData=gson.toJson(mediaList)
                Log.e("params",mediaList.toString())
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
                            for (ans in answers){
                                AppDatabase.getInstance(this@DashboardActivity).formDao().updateMediaSync(ans.id!!)
                            }
                        }
                    }
                }
            }else{
                UtilMethods.showToast(this@DashboardActivity,"No media to sync")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getCounts()
    }
    var draftCount=0
    fun getCounts(){
        lifecycleScope.launch {
            val totalCount=AppDatabase.getInstance(this@DashboardActivity).formDao().getTotalSurvey(preferenceManager.getForm().tbl_forms_id,preferenceManager.getProject().tbl_projects_id)
            val pendingCount=AppDatabase.getInstance(this@DashboardActivity).formDao().getTotalPendingSurvey(preferenceManager.getForm().tbl_forms_id,preferenceManager.getProject().tbl_projects_id)
            val doneCount=AppDatabase.getInstance(this@DashboardActivity).formDao().getTotalDoneSurvey(preferenceManager.getForm().tbl_forms_id,preferenceManager.getProject().tbl_projects_id)
            draftCount=AppDatabase.getInstance(this@DashboardActivity).formDao().getDraftSurvey(preferenceManager.getForm().tbl_forms_id,preferenceManager.getProject().tbl_projects_id)

            binding.tvTotalSurvey.text = "$totalCount"
            binding.tvPendingSurvey.text = "$pendingCount"
            binding.tvSyncDone.text = "$doneCount"
            binding.tvDraftCount.text = "$draftCount"
            //binding.tvTotalSurvey.text = "${AppDatabase.getInstance(this).formDao().getTotalSurvey()}"
        }
    }
}