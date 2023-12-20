package com.renew.survey.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.renew.survey.databinding.ActivitySyncDataBinding
import com.renew.survey.response.sync.SyncData
import com.renew.survey.room.AppDatabase
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
import com.renew.survey.room.entities.VillageEntity
import com.renew.survey.utilities.ApiInterface
import com.renew.survey.utilities.UtilMethods
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Date

class SyncDataActivity : BaseActivity() {
    lateinit var binding:ActivitySyncDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySyncDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnContinue.setOnClickListener {
            showProgress()
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
                                }
                            }
                            val assignedSurveyList= arrayListOf<AssignedSurveyEntity>()
                            for (d in data.assigned_survey){
                                var panchayathId=0
                                if (d.mst_panchayat_id!=null){
                                    panchayathId=d.mst_panchayat_id.toInt()
                                }
                                val assigned=AssignedSurveyEntity(d.parent_survey_id.toInt(),0,d.aadhar_card,d.annual_family_income,d.app_unique_code,d.banficary_name,d.electricity_connection_available,d.family_size,d.gender,d.house_type,d.is_cow_dung,d.is_lpg_using,d.mobile_number,d.mst_district_id.toInt(),panchayathId,d.mst_state_id.toInt(),d.mst_tehsil_id.toInt(),d.mst_village_id.toInt(),d.next_form_id.toInt(),d.no_of_cattles_own,d.no_of_cow_dung_per_day,d.no_of_cylinder_per_year,d.parent_survey_id,d.reason,d.system_approval,d.tbl_project_survey_common_data_id.toInt(),d.tbl_projects_id.toInt(),d.willing_to_contribute_clean_cooking,d.wood_use_per_day_in_kg)
                                assignedSurveyList.add(assigned)
                            }
                            AppDatabase.getInstance(this@SyncDataActivity).formDao().insertAllAssignedSurvey(assignedSurveyList)
                            runOnUiThread(Runnable {
                                navigateToNext()
                                preferenceManager.saveSync(UtilMethods.getFormattedDate(Date()),true)
                            })
                        }

                    }else{
                        Log.e("response","response code ${response.code()}")
                        hideProgress()
                    }

                }
            }
            /*Intent(this,LanguageActivity::class.java).apply {
                startActivity(this)
            }*/
        }
    }
    fun navigateToNext(){
        Intent(this,LanguageActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
    fun showProgress(){
        binding.llProgress.visibility= View.VISIBLE
        binding.btnContinue.visibility= View.GONE
    }
    fun hideProgress(){
        binding.llProgress.visibility=View.GONE
        binding.btnContinue.visibility=View.VISIBLE
    }
}