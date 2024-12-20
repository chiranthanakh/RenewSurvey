package com.renew.globalsurvey.views

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.renew.globalsurvey.databinding.ActivitySyncDataBinding
import com.renew.globalsurvey.request.MediaSyncReqItem
import com.renew.globalsurvey.response.sync.SyncData
import com.renew.globalsurvey.room.AppDatabase
import com.renew.globalsurvey.room.entities.AnswerEntity
import com.renew.globalsurvey.room.entities.AssignedFormEntry
import com.renew.globalsurvey.room.entities.AssignedSurveyEntity
import com.renew.globalsurvey.room.entities.CategoryEntity
import com.renew.globalsurvey.room.entities.DistrictEntity
import com.renew.globalsurvey.room.entities.DivisionEntity
import com.renew.globalsurvey.room.entities.FileTypeEntity
import com.renew.globalsurvey.room.entities.FormEntity
import com.renew.globalsurvey.room.entities.FormLanguageEntity
import com.renew.globalsurvey.room.entities.FormQuestionEntity
import com.renew.globalsurvey.room.entities.FormQuestionGroupEntity
import com.renew.globalsurvey.room.entities.FormQuestionOptionsEntity
import com.renew.globalsurvey.room.entities.LanguageEntity
import com.renew.globalsurvey.room.entities.PanchayathEntity
import com.renew.globalsurvey.room.entities.ProjectEntity
import com.renew.globalsurvey.room.entities.ProjectPhaseQuestionEntity
import com.renew.globalsurvey.room.entities.ProjectsPhase
import com.renew.globalsurvey.room.entities.StatesEntity
import com.renew.globalsurvey.room.entities.TehsilEntity
import com.renew.globalsurvey.room.entities.TestEntry
import com.renew.globalsurvey.room.entities.TestOptionsEntity
import com.renew.globalsurvey.room.entities.TestQuestionsEntry
import com.renew.globalsurvey.room.entities.TutorialEntity
import com.renew.globalsurvey.room.entities.VillageEntity
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
import java.util.Date

class SyncDataActivity : BaseActivity() {
    lateinit var binding:ActivitySyncDataBinding
    private var downloadManager: DownloadManager? = null
    private var downloadReference: Long = 0
    private var navigate : Boolean = true
    val trainingList = arrayListOf<Uri>()
    //var str = List<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySyncDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getCounts()
        getDataToSync()
        binding.btnContinue.setOnClickListener {
            showProgress()
            if (draftCount>0){
               MyCustomDialog.showDialog(this,"","All the draft data will be overwritten","OK","",true,object :MyCustomDialog.ClickListener{
                   override fun onYes() {
                       lifecycleScope.launch {
                           AppDatabase.getInstance(this@SyncDataActivity).formDao().deleteDrafts(1)
                       }
                       syncAPI()
                   }

                   override fun onNo() {

                   }

               })
            }else{
                syncAPI()
            }
        }
    }
    fun syncAPI(){
        lifecycleScope.launch (Dispatchers.IO){
            AppDatabase.getInstance(this@SyncDataActivity).clearAllTables()
        }
       // preferenceManager.saveTrainingState("trainingState", emptyList())
        lifecycleScope.launch {
            ApiInterface.getInstance()?.apply {
                Log.e("param","UserId=${preferenceManager.getUserId()}")
                val response=syncDataFromServer(preferenceManager.getToken()!!,preferenceManager.getUserId()!!)
                if (response.isSuccessful){
                    hideProgress()
                    val json=JSONObject(response.body().toString())
                    Log.e("response","Resp=${response.code()}")
                    Log.e("response","Resp=${response.body()}")
                    if (json.getString("success")=="1"){
                        syncDataFromServer(json)
                    }else {
                        val data=json.getJSONObject("data")
                        if (data.getBoolean("is_access_disable")){
                            preferenceManager.clear()
                            Intent(this@SyncDataActivity,LoginActivity::class.java).apply {
                                finishAffinity()
                                startActivity(this)
                            }
                        }
                    }

                }else{
                    Log.e("response","response code ${response.code()}")
                    hideProgress()
                }
            }
        }
    }
    fun navigateToNext(){
        Intent(this,LanguageActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
    fun showProgress(){
        binding.tvText.text="Synchronizing the data from server"
        binding.llProgress.visibility= View.VISIBLE
        binding.btnContinue.visibility= View.GONE
    }
    fun hideProgress(){
        binding.llProgress.visibility=View.GONE
        binding.btnContinue.visibility=View.VISIBLE
    }

    private fun downloadPdf(url : String) {
        val pdfUrl = url//"https://www.learningcontainer.com/wp-content/uploads/2019/09/sample-pdf-file.pdf"//url
        var str = pdfUrl.split("/")
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(pdfUrl))
            .setTitle("PDF Download")
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, str.last())
        downloadReference = downloadManager?.enqueue(request) ?: 0
        val pdfFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), str.last())
        val pdfUri = FileProvider.getUriForFile(this@SyncDataActivity, "your.fileprovider.authority", pdfFile)

        trainingList.add(pdfUri)
        preferenceManager.saveUriList(trainingList)
       Log.d("traininglistforTest",pdfUri.toString())
    }
    fun syncDataFromServer(json:JSONObject){
        lifecycleScope.launch {
            val data=gson.fromJson(json.getString("data").toString(), SyncData::class.java)
            for (s in data.tables){
                when(s.table_name){
                    "mst_language"->{
                        val languageList= arrayListOf<LanguageEntity>()
                        for (d in s.data){
                            if (!d.mst_language_id.isNullOrEmpty() && !d.title.isNullOrEmpty() && !d.symbol.isNullOrEmpty() && !d.mst_language_id.isNullOrEmpty()) {
                                val languageEntity = LanguageEntity(
                                    d.mst_language_id.toInt(),
                                    d.title,
                                    d.symbol,
                                    d.mst_language_id.toInt()
                                )
                                languageList.add(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(this@SyncDataActivity,"Some Data is Missing in mst_language, Please Correct it and Try Again")
                            }
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertAllLanguage(languageList)
                    }
                    "mst_state"->{
                        val statesEntities= arrayListOf<StatesEntity>()
                        for (d in s.data) {

                            if (!d.mst_state_id.isNullOrEmpty() && !d.state_name.isNullOrEmpty() && !d.state_code.isNullOrEmpty() && !d.mst_state_id.isNullOrEmpty() && !d.mst_country_id.isNullOrEmpty()) {
                                val statesEntity = StatesEntity(
                                    d.mst_state_id.toInt(),
                                    d.state_name,
                                    d.state_code,
                                    d.mst_state_id.toInt(),
                                    d.mst_country_id.toInt()
                                )
                                statesEntities.add(statesEntity)
                                //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(this@SyncDataActivity,"Some Data is Missing in mst_state, Please Correct it and Try Again")
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).placesDao().insertAllStates(statesEntities)
                    }
                    "mst_district"->{
                        val districtEntityList= arrayListOf<DistrictEntity>()
                        for (d in s.data){
                            if (!d.mst_district_id.isNullOrEmpty() && !d.district_name.isNullOrEmpty() && !d.mst_district_id.isNullOrEmpty() && !d.mst_country_id.isNullOrEmpty() && !d.mst_state_id.isNullOrEmpty()) {
                               // Log.d("districtIdTest", d.mst_district_id.toInt().toString()+" "+d.district_name+" "+d.mst_district_id+" "+d.mst_country_id.toInt()+" "+d.mst_state_id.toInt())
                            val districtEntity=DistrictEntity(d.mst_district_id.toInt(),d.district_name,d.mst_district_id.toInt(),d.mst_country_id.toInt(),d.mst_state_id.toInt())
                            districtEntityList.add(districtEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                UtilMethods.showToast(this@SyncDataActivity,"Some Data is Missing in mst_district, Please Correct it and Try Again")
                            }
                          }
                        AppDatabase.getInstance(this@SyncDataActivity).placesDao().insertAllDistricts(districtEntityList)
                    }
                    "mst_tehsil"->{
                        val tehsilEntities= arrayListOf<TehsilEntity>()
                        for (d in s.data){
                            if (!d.mst_tehsil_id.isNullOrEmpty() && !d.tehsil_name.isNullOrEmpty() && !d.mst_tehsil_id.isNullOrEmpty() && !d.mst_district_id.isNullOrEmpty() && !d.mst_country_id.isNullOrEmpty()&& !d.mst_state_id.isNullOrEmpty()) {
                                val tehsilEntity=TehsilEntity(d.mst_tehsil_id.toInt(),d.tehsil_name,d.mst_tehsil_id.toInt(),d.mst_district_id.toInt(),d.mst_country_id.toInt(),d.mst_state_id.toInt())
                            tehsilEntities.add(tehsilEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(this@SyncDataActivity,"Some Data is Missing in mst_tehsil, Please Correct it and Try Again")
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).placesDao().insertAllTehsils(tehsilEntities)
                    }
                    "mst_panchayat"->{
                        val panchayathEntities= arrayListOf<PanchayathEntity>()
                        for (d in s.data) {
                            if (!d.mst_panchayat_id.isNullOrEmpty() && !d.panchayat_name.isNullOrEmpty() && !d.mst_panchayat_id.isNullOrEmpty() && !d.mst_tehsil_id.isNullOrEmpty() && !d.mst_district_id.isNullOrEmpty() && !d.mst_country_id.isNullOrEmpty() && !d.mst_state_id.isNullOrEmpty()) {
                                val panchayathEntity = PanchayathEntity(
                                    d.mst_panchayat_id.toInt(),
                                    d.panchayat_name,
                                    d.mst_panchayat_id.toInt(),
                                    d.mst_tehsil_id.toInt(),
                                    d.mst_district_id.toInt(),
                                    d.mst_country_id.toInt(),
                                    d.mst_state_id.toInt()
                                )
                                panchayathEntities.add(panchayathEntity)
                                //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing in mst_panchayat, Please Correct it and Try Again"
                                )
                            }
                        }

                        AppDatabase.getInstance(this@SyncDataActivity).placesDao().insertAllPanchayaths(panchayathEntities)
                    }
                    "mst_village"->{
                        var villageEntities= arrayListOf<VillageEntity>()
                        for (d in s.data){
                            if (!d.mst_village_id.isNullOrEmpty() && !d.village_name.isNullOrEmpty() && !d.mst_village_id.isNullOrEmpty() && !d.mst_panchayat_id.isNullOrEmpty() && !d.mst_tehsil_id.isNullOrEmpty() && !d.mst_district_id.isNullOrEmpty() && !d.mst_country_id.isNullOrEmpty() && !d.mst_state_id.isNullOrEmpty()) {
                            val villageEntity=VillageEntity(d.mst_village_id.toInt(),d.village_name,d.mst_village_id.toInt(),d.mst_panchayat_id.toInt(),d.mst_tehsil_id.toInt(),d.mst_district_id.toInt(),d.mst_country_id.toInt(),d.mst_state_id.toInt())
                            villageEntities.add(villageEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing in mst_village, Please Correct it and Try Again"
                                )
                            }
                         }
                        AppDatabase.getInstance(this@SyncDataActivity).placesDao().insertAllVillages(villageEntities)
                    }
                    "tbl_forms"->{
                        val formEntityList= arrayListOf<FormEntity>()
                        for (d in s.data){
                            if (!d.tbl_forms_id.isNullOrEmpty() && !d.mst_categories_id.isNullOrEmpty() && !d.mst_divisions_id.isNullOrEmpty() && !d.tbl_forms_id.isNullOrEmpty()) {
                                val formEntity=FormEntity(d.tbl_forms_id.toInt(),d.mst_categories_id.toInt(),d.mst_divisions_id.toInt(),d.tbl_forms_id.toInt())
                            formEntityList.add(formEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing in tbl_forms, Please Correct it and Try Again"
                                )
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllForms(formEntityList)
                    }
                    "mst_question_group"->{
                        val formQuestionGroups= arrayListOf<FormQuestionGroupEntity>()
                        for (d in s.data){
                            if (!d.mst_question_group_id.isNullOrEmpty() && !d.mst_question_group_id.isNullOrEmpty() && !d.order_by.isNullOrEmpty()) {
                                val formQuestionGroup=FormQuestionGroupEntity(d.mst_question_group_id.toInt(),d.mst_question_group_id.toInt(),d.order_by.toInt())
                            formQuestionGroups.add(formQuestionGroup)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing in mst_question_group, Please Correct it and Try Again"
                                )
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllFormsQuestionGroup(formQuestionGroups)
                    }
                    "tbl_form_questions"->{
                        val formQuestions= arrayListOf<FormQuestionEntity>()
                        for (d in s.data){
                            if (!d.tbl_form_questions_id.isNullOrEmpty() && !d.mst_question_group_id.isNullOrEmpty() && !d.question_type.isNullOrEmpty() && !d.tbl_form_questions_id.isNullOrEmpty() && !d.tbl_forms_id.isNullOrEmpty()) {
                                val formQuestion=FormQuestionEntity(d.tbl_form_questions_id.toInt(),d.allowed_file_type,d.format,d.is_mandatory,d.is_special_char_allowed,d.is_validation_required,d.max_file_size,d.max_length,d.min_length,d.mst_question_group_id.toInt(),d.order_by.toInt(),d.question_type,d.tbl_form_questions_id.toInt(),d.tbl_forms_id.toInt())
                            formQuestions.add(formQuestion)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing in tbl_form_questions, Please Correct it and Try Again"
                                )
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllFormsQuestions(formQuestions)
                    }
                    "tbl_form_questions_option"->{
                        val formQuestionOptions= arrayListOf<FormQuestionOptionsEntity>()
                        for (d in s.data){
                            if (!d.tbl_form_questions_option_id.isNullOrEmpty() && !d.tbl_form_questions_id.isNullOrEmpty() && !d.tbl_form_questions_option_id.isNullOrEmpty() && !d.tbl_forms_id.isNullOrEmpty()) {
                                val formQuestionOption=FormQuestionOptionsEntity(d.tbl_form_questions_option_id.toInt(),d.tbl_form_questions_id.toInt(),d.tbl_form_questions_option_id.toInt(),d.tbl_forms_id.toInt())
                            formQuestionOptions.add(formQuestionOption)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing in tbl_form_questions_option, Please Correct it and Try Again"
                                )
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllFormsQuestionOptions(formQuestionOptions)
                    }
                    "mst_form_language"->{
                        val formLanguages= arrayListOf<FormLanguageEntity>()
                        for (d in s.data){
                            if (!d.mst_form_language_id.isNullOrEmpty()  /*&& !d.mst_form_language_id.isNullOrEmpty() && !d.mst_language_id.isNullOrEmpty() && !d.title.isNullOrEmpty()*/) {
                                val formLanguage=FormLanguageEntity(d.mst_form_language_id.toInt(),d.module,d.module_id.toInt(),d.mst_form_language_id.toInt(),d.mst_language_id.toInt(),d.title)
                            formLanguages.add(formLanguage)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing in mst_form_language, Please Correct it and Try Again"
                                )
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllFormsLanguages(formLanguages)
                    }
                    "mst_file_types"->{
                        val fileTypeEntityList= arrayListOf<FileTypeEntity>()
                        for (d in s.data){
                            if (!d.mst_file_types_id.isNullOrEmpty() && !d.extension.isNullOrEmpty() && !d.mst_file_types_id.isNullOrEmpty()) {

                                val fileTypeEntity=FileTypeEntity(d.mst_file_types_id.toInt(),d.extension,d.mst_file_types_id.toInt())
                            fileTypeEntityList.add(fileTypeEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing in mst_file_types, Please Correct it and Try Again"
                                )
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllFileTypes(fileTypeEntityList)
                    }
                    "tbl_project_phase"->{
                        val projectsPhaseList= arrayListOf<ProjectsPhase>()
                        for (d in s.data){
                            if (!d.tbl_project_phase_id.isNullOrEmpty() && !d.phase.isNullOrEmpty() && !d.tbl_forms_id.isNullOrEmpty() && !d.tbl_project_phase_id.isNullOrEmpty() && !d.tbl_projects_id.isNullOrEmpty() && !d.version.isNullOrEmpty()) {

                                val projectsPhase=ProjectsPhase(d.tbl_project_phase_id.toInt(),d.phase.toInt(),d.version.toString(),d.tbl_forms_id.toInt(),d.tbl_project_phase_id.toInt(),d.tbl_projects_id.toInt(),d.version.toInt(),d.is_released)
                            projectsPhaseList.add(projectsPhase)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing in tbl_project_phase, Please Correct it and Try Again"
                                )
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllProjectPhase(projectsPhaseList)
                    }
                    "tbl_projects"->{
                        val projectEntities= arrayListOf<ProjectEntity>()
                        for (d in s.data){
                            if (!d.tbl_projects_id.isNullOrEmpty() && !d.mst_categories_id.isNullOrEmpty() /*&& !d.mst_country_id.isNullOrEmpty() && !d.mst_divisions_id.isNullOrEmpty() && !d.mst_language_id.isNullOrEmpty() && !d.mst_state_id.isNullOrEmpty()  && !d.project_code.isNullOrEmpty() && !d.project_manager.isNullOrEmpty() && !d.tbl_projects_id.isNullOrEmpty()*/) {
                                val projectEntity=ProjectEntity(d.tbl_projects_id.toInt(),d.mst_categories_id.toInt(),d.mst_country_id.toInt(),d.mst_divisions_id.toInt(),d.mst_language_id,d.mst_state_id.toInt(),d.project_co_ordinator,d.project_code,d.project_manager,d.tbl_projects_id.toInt())
                            projectEntities.add(projectEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        } else {
                            navigate = false
                            UtilMethods.showToast(
                                this@SyncDataActivity,
                                "Some Data is Missing in tbl_projects, Please Correct it and Try Again"
                            )
                        }
                      }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllProjects(projectEntities)
                    }
                    "tbl_project_phase_question"->{
                        val projectsPhaseQuestionEntity= arrayListOf<ProjectPhaseQuestionEntity>()
                        for (d in s.data){
                            if (!d.tbl_project_phase_question_id.isNullOrEmpty() && !d.mst_question_group_id.isNullOrEmpty() && !d.tbl_form_questions_id.isNullOrEmpty() && !d.tbl_forms_id.isNullOrEmpty() && !d.tbl_project_phase_id.isNullOrEmpty() && !d.tbl_project_phase_question_id.isNullOrEmpty()&& !d.tbl_projects_id.isNullOrEmpty() && !d.version.isNullOrEmpty()) {
                                val projectEntity=ProjectPhaseQuestionEntity(d.tbl_project_phase_question_id.toInt(),d.mst_question_group_id.toInt(),d.tbl_form_questions_id.toInt(),d.tbl_forms_id.toInt(),d.tbl_project_phase_id.toInt(),d.tbl_project_phase_question_id.toInt(),d.tbl_projects_id.toInt(),d.version)
                            projectsPhaseQuestionEntity.add(projectEntity)
                                Log.d("printprojectentry",projectEntity.toString())
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing, Please Correct it and Try Again"
                                )
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllProjectPhaseQuestionEntities(projectsPhaseQuestionEntity)
                    }

                    "tbl_users_assigned_projects"->{
                        val assignedProjects= arrayListOf<AssignedFormEntry>()
                        for (d in s.data){
                            if (!d.tbl_users_assigned_projects_id.isNullOrEmpty() && !d.tbl_users_assigned_projects_id.isNullOrEmpty() && !d.tbl_projects_id.isNullOrEmpty() && !d.tbl_forms_id.isNullOrEmpty() && !d.tbl_users_id.isNullOrEmpty() && !d.tbl_project_phase_id.isNullOrEmpty()) {
                                val formEntity=AssignedFormEntry(d.tbl_users_assigned_projects_id.toInt(),d.tbl_users_assigned_projects_id.toInt(),d.tbl_projects_id.toInt(),d.tbl_forms_id.toInt(),d.tbl_users_id.toInt(),d.tbl_project_phase_id.toInt(),/*d.mst_country_id.toInt(),d.mst_state_id.toInt(),d.mst_district_id.toInt(),d.mst_tehsil_id.toInt(),d.mst_panchayat_id.toInt(),d.mst_village_id.toInt()d.created_by*/d.create_date,d.is_active.toInt(),d.is_delete)
                            assignedProjects.add(formEntity)
                                //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing, Please Correct it and Try Again"
                                )
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertassignedforms(assignedProjects)
                    }


                    "mst_divisions"->{
                        val divisionEntities= arrayListOf<DivisionEntity>()
                        for (d in s.data){
                            if (!d.mst_divisions_id.isNullOrEmpty() && !d.division_code.isNullOrEmpty() && !d.division_name.isNullOrEmpty() && !d.mst_divisions_id.isNullOrEmpty()) {
                                val divisionEntity=DivisionEntity(d.mst_divisions_id.toInt(),d.division_code,d.division_name,d.mst_divisions_id.toInt())
                            divisionEntities.add(divisionEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing, Please Correct it and Try Again"
                                )
                            }
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllDivisions(divisionEntities)
                    }
                    "mst_categories"->{
                        val categoryEntities= arrayListOf<CategoryEntity>()
                        for (d in s.data){
                            if (!d.mst_categories_id.isNullOrEmpty() && !d.category_name.isNullOrEmpty() && !d.mst_categories_id.isNullOrEmpty() && !d.mst_divisions_id.isNullOrEmpty()) {
                                val divisionEntity=CategoryEntity(d.mst_categories_id.toInt(),d.category_name,d.mst_categories_id.toInt(),d.mst_divisions_id.toInt())
                            categoryEntities.add(divisionEntity)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing, Please Correct it and Try Again"
                                )
                            }
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllCategories(categoryEntities)
                    }
                    "tbl_tutorials"->{
                        val tutorialEntity= arrayListOf<TutorialEntity>()
                        for (d in s.data){
                            val tutorials=TutorialEntity(d.tbl_tutorials,d.tbl_tutorials,d.tbl_forms_id.toInt(),d.tutorial_file,d.tutorial_code,d.last_update,d.is_delete,d.is_active,d.create_date,d.created_by)
                            tutorialEntity.add(tutorials)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        Log.d("tutorialsDetails", tutorialEntity.size.toString() + "-" + tutorialEntity.toString())
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllTutorials(tutorialEntity)
                    }
                    "tbl_tests" -> {
                        val testEntry = arrayListOf<TestEntry>()
                        for (d in s.data) {
                            if (!d.tbl_forms_id.isNullOrEmpty() && !d.mst_categories_id.isNullOrEmpty() ) {
                                val tests = TestEntry(
                                    d.tbl_tests_id,
                                    d.tbl_tests_id,
                                    d.tbl_forms_id.toInt(),
                                    d.created_by,
                                    d.test_code,
                                    d.passing_marks,
                                    d.create_date,
                                    d.is_active,
                                    d.is_delete,
                                    "1",
                                    d.mst_categories_id
                                )
                                testEntry.add(tests)
                            } else {
                                navigate = false
                                UtilMethods.showToast(
                                    this@SyncDataActivity,
                                    "Some Data is Missing in Test tabel, Please Correct it and Try Again"
                                )
                            }
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        Log.d(
                            "tutorialsDetails2",
                            testEntry.size.toString() + "-" + testEntry.toString()
                        )

                        AppDatabase.getInstance(this@SyncDataActivity).formDao()
                            .insertAllTests(testEntry)
                    }
                    "tbl_test_questions"->{
                        val testQuestionEntity= arrayListOf<TestQuestionsEntry>()
                        for (d in s.data){
                            val testQuestion=TestQuestionsEntry(d.tbl_test_questions_id.toInt(),d.tbl_test_questions_id.toInt(),d.tbl_tests_id.toInt(),d.tbl_forms_id.toInt(),d.created_by,d.question_type,d.is_mandatory,d.order_by,d.is_validation_required,d.is_special_char_allowed,d.min_length,d.max_length,d.format,d.answer,d.create_date,d.is_active,d.is_delete,d.last_update)
                            testQuestionEntity.add(testQuestion)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllTestQuestions(testQuestionEntity)
                    }
                    "tbl_test_questions_option"->{
                        val testQuestionEntity= arrayListOf<TestOptionsEntity>()
                        for (d in s.data){
                            val testQuestion=TestOptionsEntity(d.tbl_test_questions_option_id.toInt(),d.is_answer,d.tbl_forms_id.toInt(),d.tbl_test_questions_id.toInt(),d.tbl_test_questions_option_id.toInt(),d.tbl_tests_id)
                            testQuestionEntity.add(testQuestion)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllTestOptions(testQuestionEntity)
                    }
                }
            }
            val assignedSurveyList= arrayListOf<AssignedSurveyEntity>()
            Log.d("assignedSurvaytest",data.assigned_survey.toString())
            for (d in data.assigned_survey){
                var panchayathId=0
                var above15=""
                var below15=""
                if (d.mst_panchayat_id!=null && d.mst_district_id != ""){
                    panchayathId=d.mst_panchayat_id.toInt()
                    //Log.d("testpanchyatparemater",d.mst_panchayat_id)
                }
                if (d.family_member_above_15_year!=null){
                    above15=d.family_member_above_15_year!!
                }
                if (d.family_member_below_15_year!=null){
                    below15=d.family_member_below_15_year!!
                }
                try {
                    val assigned=AssignedSurveyEntity(d.parent_survey_id.toInt(),0,d.aadhar_card,d.annual_family_income,d.app_unique_code,d.banficary_name,d.electricity_connection_available,d.family_size,d.gender,d.house_type,d.is_cow_dung,d.is_lpg_using,d.mobile_number,d.mst_district_id.toInt(),panchayathId,d.mst_state_id.toInt(),
                        d.mst_tehsil_id.toInt(),d.mst_village_id.toInt(),d.next_form_id.toInt(),d.no_of_cattles_own,d.no_of_cylinder_per_year,d.device_serial_number, d.parent_survey_id,d.reason,d.system_approval,d.tbl_project_survey_common_data_id.toInt(),d.tbl_projects_id.toInt(),d.willing_to_contribute_clean_cooking,above15,below15,
                        d.wood_use_per_day_in_kg,
                        d.date_and_time_of_visit!!,d.did_the_met_person_allowed_for_data!!,d.gps_location!!,d.do_you_have_aadhar_card,d.font_photo_of_aadar_card,d.back_photo_of_aadhar_card,d.total_electricity_bill,d.frequency_of_bill_payment,d.photo_of_bill,d.cost_of_lpg_cyliner,d.do_you_have_ration_or_aadhar,d.farmland_is_owned_by_benficary,d.if_5m_area_is_available_near_by)
                    assignedSurveyList.add(assigned)
                } catch (e : NumberFormatException){

                }
            }
            AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllAssignedSurvey(assignedSurveyList)
            try{
                for (d in data.training_tutorials){
                    downloadPdf(d)
                }

            }catch (e:Exception){
                e.toString()
            }
            if (navigate == true) {
                navigateToNext()
                preferenceManager.saveSync(UtilMethods.getFormattedDate(Date()),true)
            }
        }
    }
    var answers= listOf<AnswerEntity>()
    fun getDataToSync(){
        lifecycleScope.launch {
            answers=AppDatabase.getInstance(this@SyncDataActivity).formDao().getAllUnsyncedAnswers()
            for (a in answers){
                val commonAns=AppDatabase.getInstance(this@SyncDataActivity).formDao().getCommonAnswers(a.id!!)
                val dynamicAns=AppDatabase.getInstance(this@SyncDataActivity).formDao().getDynamicAns(a.id!!)
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
                binding.btnContinue.visibility=View.GONE
                binding.tvText.text="Synchronizing the data to server"
                ApiInterface.getInstance()?.apply {
                    val response=syncSubmitForms(preferenceManager.getToken()!!,answers)
                    binding.llProgress.visibility=View.GONE
                    binding.btnContinue.visibility=View.VISIBLE
                    if (response.isSuccessful){
                        Log.e("response","${response.body()}")
                        val json=JSONObject(response.body().toString())
                        UtilMethods.showToast(this@SyncDataActivity,json.getString("message"))
                        if (json.getString("success")=="1"){
                            syncMedia()
                            for (ans in answers){
                                AppDatabase.getInstance(this@SyncDataActivity).formDao().updateSync(ans.id!!)
                            }
                           // getCounts()
                        }else {
                            val data=json.getJSONObject("data")
                            if (data.getBoolean("is_access_disable")){
                                preferenceManager.clear()
                                Intent(this@SyncDataActivity,LoginActivity::class.java).apply {
                                    finishAffinity()
                                    startActivity(this)
                                }
                            }
                        }
                    }
                }
            }else{
                syncMedia()
                //UtilMethods.showToast(this@SyncDataActivity,"No data to sync")
            }
        }
    }
    fun syncMedia(){
        lifecycleScope.launch {
            val answers=AppDatabase.getInstance(this@SyncDataActivity).formDao().getAllUnsyncedAnswers()
            for (a in answers){
                val commonAns=AppDatabase.getInstance(this@SyncDataActivity).formDao().getCommonAnswers(a.id!!)
                val dynamicAns=AppDatabase.getInstance(this@SyncDataActivity).formDao().getDynamicAns(a.id!!)
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
                    surveyImagesParts[i] = MultipartBody.Part.createFormData("files[]",mediaList[i].file_name, surveyBody)
                }
                val jsonData=gson.toJson(mediaList)
                Log.e("params",jsonData.toString())
                binding.llProgress.visibility=View.VISIBLE
                binding.btnContinue.visibility=View.GONE
                binding.tvText.text="Synchronizing the media to server"
                ApiInterface.getInstance()?.apply {
                    val datapart= RequestBody.create(MultipartBody.FORM, gson.toJson(mediaList))
                    val response=syncMediaFiles(preferenceManager.getToken()!!,datapart,surveyImagesParts)
                    binding.llProgress.visibility=View.GONE
                    binding.btnContinue.visibility=View.VISIBLE
                    if (response.isSuccessful){
                        val jsonObject=JSONObject(response.body().toString())
                        Log.e("response",jsonObject.toString())
                        UtilMethods.showToast(this@SyncDataActivity,jsonObject.getString("message"))
                        if (jsonObject.getString("success")=="1"){
                            for (ans in answers){
                                AppDatabase.getInstance(this@SyncDataActivity).formDao().updateMediaSync(ans.id!!)
                            }
                        }
                    }
                }
            }else{
                //UtilMethods.showToast(this@SyncDataActivity,"No media to sync")
            }
        }
    }
    var draftCount=0
    fun getCounts(){
        lifecycleScope.launch {
            draftCount=AppDatabase.getInstance(this@SyncDataActivity).formDao().getAllDraftSurveyCount()
        }
    }

}