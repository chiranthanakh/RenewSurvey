package com.renew.survey.views

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
import com.renew.survey.databinding.ActivitySyncDataBinding
import com.renew.survey.request.MediaSyncReqItem
import com.renew.survey.response.sync.SyncData
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.AnswerEntity
import com.renew.survey.room.entities.AssignedSurveyEntity
import com.renew.survey.room.entities.CategoryEntity
import com.renew.survey.room.entities.DistrictEntity
import com.renew.survey.room.entities.DivisionEntity
import com.renew.survey.room.entities.FileTypeEntity
import com.renew.survey.room.entities.FormEntity
import com.renew.survey.room.entities.FormLanguageEntity
import com.renew.survey.room.entities.FormQuestionEntity
import com.renew.survey.room.entities.FormQuestionGroupEntity
import com.renew.survey.room.entities.FormQuestionOptionsEntity
import com.renew.survey.room.entities.LanguageEntity
import com.renew.survey.room.entities.PanchayathEntity
import com.renew.survey.room.entities.ProjectEntity
import com.renew.survey.room.entities.ProjectPhaseQuestionEntity
import com.renew.survey.room.entities.ProjectsPhase
import com.renew.survey.room.entities.StatesEntity
import com.renew.survey.room.entities.TehsilEntity
import com.renew.survey.room.entities.TestEntry
import com.renew.survey.room.entities.TestOptionsEntity
import com.renew.survey.room.entities.TestQuestionsEntry
import com.renew.survey.room.entities.TutorialEntity
import com.renew.survey.room.entities.VillageEntity
import com.renew.survey.utilities.ApiInterface
import com.renew.survey.utilities.MyCustomDialog
import com.renew.survey.utilities.UtilMethods
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
                            val languageEntity=LanguageEntity(d.mst_language_id.toInt(),d.title,d.symbol,d.mst_language_id.toInt())
                            languageList.add(languageEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertAllLanguage(languageList)
                    }
                    "mst_state"->{
                        val statesEntities= arrayListOf<StatesEntity>()
                        for (d in s.data){
                            val statesEntity=StatesEntity(d.mst_state_id.toInt(),d.state_name,d.state_code,d.mst_state_id.toInt(),d.mst_country_id.toInt())
                            statesEntities.add(statesEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).placesDao().insertAllStates(statesEntities)
                    }
                    "mst_district"->{
                        val districtEntityList= arrayListOf<DistrictEntity>()
                        for (d in s.data){
                            val districtEntity=DistrictEntity(d.mst_district_id.toInt(),d.district_name,d.mst_district_id.toInt(),d.mst_country_id.toInt(),d.mst_state_id.toInt())
                            districtEntityList.add(districtEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).placesDao().insertAllDistricts(districtEntityList)
                    }
                    "mst_tehsil"->{
                        val tehsilEntities= arrayListOf<TehsilEntity>()
                        for (d in s.data){
                            val tehsilEntity=TehsilEntity(d.mst_tehsil_id.toInt(),d.tehsil_name,d.mst_tehsil_id.toInt(),d.mst_district_id.toInt(),d.mst_country_id.toInt(),d.mst_state_id.toInt())
                            tehsilEntities.add(tehsilEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).placesDao().insertAllTehsils(tehsilEntities)
                    }
                    "mst_panchayat"->{
                        val panchayathEntities= arrayListOf<PanchayathEntity>()
                        for (d in s.data){
                            val panchayathEntity=PanchayathEntity(d.mst_panchayat_id.toInt(),d.panchayat_name,d.mst_panchayat_id.toInt(),d.mst_tehsil_id.toInt(),d.mst_district_id.toInt(),d.mst_country_id.toInt(),d.mst_state_id.toInt())
                            panchayathEntities.add(panchayathEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).placesDao().insertAllPanchayaths(panchayathEntities)
                    }
                    "mst_village"->{
                        val villageEntities= arrayListOf<VillageEntity>()
                        for (d in s.data){
                            val villageEntity=VillageEntity(d.mst_village_id.toInt(),d.village_name,d.mst_village_id.toInt(),d.mst_panchayat_id.toInt(),d.mst_tehsil_id.toInt(),d.mst_district_id.toInt(),d.mst_country_id.toInt(),d.mst_state_id.toInt())
                            villageEntities.add(villageEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).placesDao().insertAllVillages(villageEntities)
                    }
                    "tbl_forms"->{
                        val formEntityList= arrayListOf<FormEntity>()
                        for (d in s.data){
                            val formEntity=FormEntity(d.tbl_forms_id.toInt(),d.mst_categories_id.toInt(),d.mst_divisions_id.toInt(),d.tbl_forms_id.toInt())
                            formEntityList.add(formEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllForms(formEntityList)
                    }
                    "mst_question_group"->{
                        val formQuestionGroups= arrayListOf<FormQuestionGroupEntity>()
                        for (d in s.data){
                            val formQuestionGroup=FormQuestionGroupEntity(d.mst_question_group_id.toInt(),d.mst_question_group_id.toInt(),d.order_by.toInt())
                            formQuestionGroups.add(formQuestionGroup)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllFormsQuestionGroup(formQuestionGroups)
                    }
                    "tbl_form_questions"->{
                        val formQuestions= arrayListOf<FormQuestionEntity>()
                        for (d in s.data){
                            val formQuestion=FormQuestionEntity(d.tbl_form_questions_id.toInt(),d.allowed_file_type,d.format,d.is_mandatory,d.is_special_char_allowed,d.is_validation_required,d.max_file_size,d.max_length,d.min_length,d.mst_question_group_id.toInt(),d.order_by.toInt(),d.question_type,d.tbl_form_questions_id.toInt(),d.tbl_forms_id.toInt())
                            formQuestions.add(formQuestion)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllFormsQuestions(formQuestions)
                    }
                    "tbl_form_questions_option"->{
                        val formQuestionOptions= arrayListOf<FormQuestionOptionsEntity>()
                        for (d in s.data){
                            val formQuestionOption=FormQuestionOptionsEntity(d.tbl_form_questions_option_id.toInt(),d.tbl_form_questions_id.toInt(),d.tbl_form_questions_option_id.toInt(),d.tbl_forms_id.toInt())
                            formQuestionOptions.add(formQuestionOption)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllFormsQuestionOptions(formQuestionOptions)
                    }
                    "mst_form_language"->{
                        val formLanguages= arrayListOf<FormLanguageEntity>()
                        for (d in s.data){
                            val formLanguage=FormLanguageEntity(d.mst_form_language_id.toInt(),d.module,d.module_id.toInt(),d.mst_form_language_id.toInt(),d.mst_language_id.toInt(),d.title)
                            formLanguages.add(formLanguage)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllFormsLanguages(formLanguages)
                    }
                    "mst_file_types"->{
                        val fileTypeEntityList= arrayListOf<FileTypeEntity>()
                        for (d in s.data){
                            val fileTypeEntity=FileTypeEntity(d.mst_file_types_id.toInt(),d.extension,d.mst_file_types_id.toInt())
                            fileTypeEntityList.add(fileTypeEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllFileTypes(fileTypeEntityList)
                    }
                    "tbl_project_phase"->{
                        val projectsPhaseList= arrayListOf<ProjectsPhase>()
                        for (d in s.data){
                            val projectsPhase=ProjectsPhase(d.tbl_project_phase_id.toInt(),d.phase.toInt(),d.version.toString(),d.tbl_forms_id.toInt(),d.tbl_project_phase_id.toInt(),d.tbl_projects_id.toInt(),d.version.toInt())
                            projectsPhaseList.add(projectsPhase)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllProjectPhase(projectsPhaseList)
                    }
                    "tbl_projects"->{
                        val projectEntities= arrayListOf<ProjectEntity>()
                        for (d in s.data){
                            val projectEntity=ProjectEntity(d.tbl_projects_id.toInt(),d.mst_categories_id.toInt(),d.mst_country_id.toInt(),d.mst_divisions_id.toInt(),d.mst_language_id,d.mst_state_id.toInt(),d.project_co_ordinator,d.project_code,d.project_manager,d.tbl_projects_id.toInt())
                            projectEntities.add(projectEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllProjects(projectEntities)
                    }
                    "tbl_project_phase_question"->{
                        val projectsPhaseQuestionEntity= arrayListOf<ProjectPhaseQuestionEntity>()
                        for (d in s.data){
                            val projectEntity=ProjectPhaseQuestionEntity(d.tbl_project_phase_question_id.toInt(),d.mst_question_group_id.toInt(),d.tbl_form_questions_id.toInt(),d.tbl_forms_id.toInt(),d.tbl_project_phase_id.toInt(),d.tbl_project_phase_question_id.toInt(),d.tbl_projects_id.toInt(),d.version)
                            projectsPhaseQuestionEntity.add(projectEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllProjectPhaseQuestionEntities(projectsPhaseQuestionEntity)
                    }
                    "mst_divisions"->{
                        val divisionEntities= arrayListOf<DivisionEntity>()
                        for (d in s.data){
                            val divisionEntity=DivisionEntity(d.mst_divisions_id.toInt(),d.division_code,d.division_name,d.mst_divisions_id.toInt())
                            divisionEntities.add(divisionEntity)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllDivisions(divisionEntities)
                    }
                    "mst_categories"->{
                        val categoryEntities= arrayListOf<CategoryEntity>()
                        for (d in s.data){
                            val divisionEntity=CategoryEntity(d.mst_categories_id.toInt(),d.category_name,d.mst_categories_id.toInt(),d.mst_divisions_id.toInt())
                            categoryEntities.add(divisionEntity)
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
                    "tbl_tests"->{
                        val testEntry= arrayListOf<TestEntry>()
                        for (d in s.data){
                            val tests=TestEntry(d.tbl_tests_id,d.tbl_tests_id,d.tbl_forms_id.toInt(),d.created_by,d.test_code,d.passing_marks,d.create_date,d.is_active,d.is_delete,d.last_update)
                            testEntry.add(tests)
                            //AppDatabase.getInstance(this@SyncDataActivity).languageDao().insertLanguage(languageEntity)
                        }
                        Log.d("tutorialsDetails2", testEntry.size.toString() + "-" + testEntry.toString())

                        AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllTests(testEntry)
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
            for (d in data.assigned_survey){
                var panchayathId=0
                var above15=""
                var below15=""
                if (d.mst_panchayat_id!=null){
                    panchayathId=d.mst_panchayat_id.toInt()
                }
                if (d.family_member_above_15_year!=null){
                    above15=d.family_member_above_15_year!!
                }
                if (d.family_member_below_15_year!=null){
                    below15=d.family_member_below_15_year!!
                }
                try {
                    val assigned=AssignedSurveyEntity(d.parent_survey_id.toInt(),0,d.aadhar_card,d.annual_family_income,d.app_unique_code,d.banficary_name,d.electricity_connection_available,d.family_size,d.gender,d.house_type,d.is_cow_dung,d.is_lpg_using,d.mobile_number,d.mst_district_id.toInt(),panchayathId,d.mst_state_id.toInt(),
                        d.mst_tehsil_id.toInt(),d.mst_village_id.toInt(),d.next_form_id.toInt(),d.no_of_cattles_own,d.no_of_cow_dung_per_day,d.no_of_cylinder_per_year,d.parent_survey_id,d.reason,d.system_approval,d.tbl_project_survey_common_data_id.toInt(),d.tbl_projects_id.toInt(),d.willing_to_contribute_clean_cooking,above15,below15,
                        d.wood_use_per_day_in_kg,d.date_and_time_of_visit,d.did_the_met_person_allowed_for_data,d.gps_location,d.do_you_have_aadhar_card,d.font_photo_of_aadar_card,d.back_photo_of_aadhar_card,d.total_electricity_bill,d.frequency_of_bill_payment,d.photo_of_bill,d.cost_of_lpg_cyliner,d.do_you_have_ration_or_aadhar)
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
            navigateToNext()
            preferenceManager.saveSync(UtilMethods.getFormattedDate(Date()),true)
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