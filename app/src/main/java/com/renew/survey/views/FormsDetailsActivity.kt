package com.renew.survey.views

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.renew.survey.R
import com.renew.survey.adapter.QuestionGroupAdapter
import com.renew.survey.databinding.ActivityFormsDetailsBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.AnswerEntity
import com.renew.survey.room.entities.AssignedSurveyEntity
import com.renew.survey.room.entities.CommonAnswersEntity
import com.renew.survey.room.entities.DraftCommonAnswer
import com.renew.survey.room.entities.DynamicAnswersEntity
import com.renew.survey.room.entities.QuestionGroupWithLanguage
import com.renew.survey.utilities.UtilMethods
import com.renew.survey.views.fragments.CommonQuestionFragment
import com.renew.survey.views.fragments.QuestionsFragment
import kotlinx.coroutines.launch
import java.util.Date


class FormsDetailsActivity : BaseActivity() ,QuestionGroupAdapter.ClickListener{
    lateinit var binding: ActivityFormsDetailsBinding
    private var questionGroupList= arrayListOf<QuestionGroupWithLanguage>()
    private var listOfFragment= arrayListOf<Fragment>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    var status=0
    var previouslySelected=0
    lateinit var adapterQuestionGroup:QuestionGroupAdapter
    var tbl_project_survey_common_data_id=""
    var commonAnswersEntity: CommonAnswersEntity=CommonAnswersEntity(0,"","","","","","","","","","","","","","","","","","","","","","",0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFormsDetailsBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)
        if (intent.hasExtra("assigned")){

            val a=gson.fromJson(intent.getStringExtra("assigned"),AssignedSurveyEntity::class.java)
            commonAnswersEntity= CommonAnswersEntity(
                null,a.aadhar_card,a.annual_family_income,a.banficary_name,a.electricity_connection_available,a.family_size,a.gender,a.house_type,a.is_cow_dung,
                a.is_lpg_using,a.mobile_number,a.mst_district_id.toString(),a.mst_state_id.toString(),a.mst_tehsil_id.toString(),a.mst_panchayat_id.toString(),a.mst_village_id.toString(),a.no_of_cattles_own,a.no_of_cow_dung_per_day,a.no_of_cylinder_per_year,a.willing_to_contribute_clean_cooking,a.wood_use_per_day_in_kg,a.parent_survey_id,a.tbl_project_survey_common_data_id.toString(),null
            )
            tbl_project_survey_common_data_id=a.tbl_project_survey_common_data_id.toString()
            status=a.next_form_id
        }else if (intent.hasExtra("draft")){

            val a=gson.fromJson(intent.getStringExtra("draft"),DraftCommonAnswer::class.java)
            status=3+a.tbl_forms_id!!

            commonAnswersEntity= CommonAnswersEntity(
                null,a.aadhar_card,a.annual_family_income,a.banficary_name,a.electricity_connection_available,a.family_size,a.gender,a.house_type,a.is_cow_dung,
                a.is_lpg_using,a.mobile_number,a.mst_district_id.toString(),a.mst_state_id.toString(),a.mst_tehsil_id.toString(),a.mst_panchayat_id.toString(),a.mst_village_id.toString(),a.no_of_cattles_own,a.no_of_cow_dung_per_day,a.no_of_cylinder_per_year,a.willing_to_contribute_clean_cooking,a.wood_use_per_day_in_kg,a.parent_survey_id,a.tbl_project_survey_common_data_id.toString(),null
            )
        }
        getAllQuestionGroup()
        binding.tvProject.text=preferenceManager.getForm().title
        binding.recyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.btnSaveDraft.setOnClickListener {
            if (!validateCommonQuestions()){
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val appUniqueCode="${preferenceManager.getUserId()}_${preferenceManager.getProject().id}_${preferenceManager.getForm().tbl_forms_id}_${questionGroupList[1].tbl_project_phase_id}_${UtilMethods.getFormattedDate(
                    Date(),"dd:MM:yyyy:HH:mm:ss")}"
                val ans=AnswerEntity(null,appUniqueCode,preferenceManager.getLanguage().toString(),commonAnswersEntity.parent_survey_id,questionGroupList[1].tbl_project_phase_id.toString(),preferenceManager.getForm().tbl_forms_id.toString(),questionGroupList[1].tbl_project_phase_id.toString(),preferenceManager.getProject().id.toString(),preferenceManager.getUserId().toString(),questionGroupList[1].version,0,1)
                var ansId=0
                if (status>3){
                    ans.id=preferenceManager.getDraft()
                    ansId= preferenceManager.getDraft()
                    AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateAnswer(ans)
                }else{
                    ansId=AppDatabase.getInstance(this@FormsDetailsActivity).formDao().insertAnswer(ans).toInt()
                }
                val cm=AppDatabase.getInstance(this@FormsDetailsActivity).formDao().getCommonAnswers(ansId)

                commonAnswersEntity.answer_id=ansId.toInt()
                if (status>3){
                    commonAnswersEntity.id=cm.id
                    AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateCommonAnswer(commonAnswersEntity)
                }else{
                    AppDatabase.getInstance(this@FormsDetailsActivity).formDao().insertCommonAnswer(commonAnswersEntity)
                }

                for (qg in questionGroupList){
                    for(q in qg.questions){
                        if(status>3){
                            Log.d("UpdatingQeuery","ans ${q.answer} group=${qg.mst_question_group_id} question=${q.tbl_form_questions_id}")
                            AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateDynamicAnswer(q.answer!!,qg.mst_question_group_id,q.tbl_form_questions_id,ansId)
                        }else{
                            val dynamicAnswersEntity=DynamicAnswersEntity(null,qg.mst_question_group_id,q.answer,q.tbl_form_questions_id,ansId.toInt())
                            AppDatabase.getInstance(this@FormsDetailsActivity).formDao().insertDynamicAnswer(dynamicAnswersEntity)
                        }
                    }
                }
            }
            UtilMethods.showToast(this,"Form saved in draft")
            finish()
        }
        binding.btnNext.setOnClickListener {
            loadFragment(previouslySelected+1)
        }
        binding.btnPrevious.setOnClickListener {
            if (previouslySelected>0){
                loadFragment(previouslySelected-1)
            }

        }
        binding.btnContinue.setOnClickListener {
            Log.d("question",gson.toJson(questionGroupList[1]))
            if (!validateCommonQuestions()){
                return@setOnClickListener
            }
            for (qg in questionGroupList){
                for(q in qg.questions){
                    if (q.is_mandatory.equals("yes",ignoreCase = true)){
                        if (q.answer.equals("")){
                            UtilMethods.showToast(this@FormsDetailsActivity,"Please add/select ${q.title} in qustion group ${qg.title}")
                            return@setOnClickListener
                        }
                    }
                }
            }
            lifecycleScope.launch {

                val appUniqueCode="${preferenceManager.getUserId()}_${preferenceManager.getProject().id}_${preferenceManager.getForm().tbl_forms_id}_${questionGroupList[1].tbl_project_phase_id}_${UtilMethods.getFormattedDate(
                    Date(),"dd:MM:yyyy:HH:mm:ss")}"
                val ans=AnswerEntity(null,appUniqueCode,preferenceManager.getLanguage().toString(),commonAnswersEntity.parent_survey_id,questionGroupList[1].tbl_project_phase_id.toString(),preferenceManager.getForm().tbl_forms_id.toString(),questionGroupList[1].tbl_project_phase_id.toString(),preferenceManager.getProject().id.toString(),preferenceManager.getUserId().toString(),questionGroupList[1].version,0,0)
                ans.tbl_project_survey_common_data_id=tbl_project_survey_common_data_id
                var ansId=0
                if (status>3){
                    ans.id=preferenceManager.getDraft()
                    ansId= preferenceManager.getDraft()
                    Log.e("patedsj",gson.toJson(ans))
                    AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateAnswer(ans)
                }else{
                    ansId=AppDatabase.getInstance(this@FormsDetailsActivity).formDao().insertAnswer(ans).toInt()
                }
                val cm=AppDatabase.getInstance(this@FormsDetailsActivity).formDao().getCommonAnswers(ansId)
                commonAnswersEntity.answer_id=ansId.toInt()
                if (status>3){
                    commonAnswersEntity.id=cm.id
                    AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateCommonAnswer(commonAnswersEntity)
                }else{
                    AppDatabase.getInstance(this@FormsDetailsActivity).formDao().insertCommonAnswer(commonAnswersEntity)
                }

                commonAnswersEntity.answer_id=ansId.toInt()
                for (qg in questionGroupList){
                    for(q in qg.questions){
                        if(status>3){
                            Log.d("UpdatingQeuery","ans ${q.answer} group=${qg.mst_question_group_id} question=${q.tbl_form_questions_id}")
                            AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateDynamicAnswer(q.answer!!,qg.mst_question_group_id,q.tbl_form_questions_id,ansId)
                        }else{
                            val dynamicAnswersEntity=DynamicAnswersEntity(null,qg.mst_question_group_id,q.answer,q.tbl_form_questions_id,ansId.toInt())
                            AppDatabase.getInstance(this@FormsDetailsActivity).formDao().insertDynamicAnswer(dynamicAnswersEntity)
                        }
                    }
                }
            }
            UtilMethods.showToast(this,"Form saved successfully")
            finish()
        }
        turnOnLocation()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationRequest = LocationRequest.create()
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest!!.setInterval(20 * 1000)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location!=null){
                        preferenceManager.saveLocation("${location.latitude},${location.longitude}")
                    }
                }
            }
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location!=null){
                    preferenceManager.saveLocation("${location.latitude},${location.longitude}")
                }
            }


    }
    fun getAllQuestionGroup(){
        lifecycleScope.launch {
            questionGroupList=AppDatabase.getInstance(this@FormsDetailsActivity).formDao().getAllFormsQuestionGroup(preferenceManager.getLanguage(),preferenceManager.getProject().id!!,preferenceManager.getForm().tbl_forms_id) as ArrayList<QuestionGroupWithLanguage>
            Log.e("roomData","data=$questionGroupList")

            questionGroupList.add(0,QuestionGroupWithLanguage(0,0,"1",0,getString(R.string.basic_info),12,0,true,
                listOf()
            ))
            questionGroupList.forEachIndexed { index, questionGroupWithLanguage ->
                    if (questionGroupWithLanguage.mst_question_group_id==0){
                        val fragment=CommonQuestionFragment(commonAnswersEntity,status)
                        listOfFragment.add(fragment)
                        supportFragmentManager.beginTransaction().add(R.id.container,fragment ).commit()
                    }else{
                        val fragment=QuestionsFragment(questionGroupWithLanguage.mst_question_group_id,index,questionGroupList,status)
                        listOfFragment.add(fragment)
                        supportFragmentManager.beginTransaction().add(R.id.container,fragment ).commit()
                    }
            }

            adapterQuestionGroup=QuestionGroupAdapter(this@FormsDetailsActivity,questionGroupList,this@FormsDetailsActivity,previouslySelected)
            binding.recyclerView.adapter=adapterQuestionGroup
            loadFragment(0)

        }
    }

    override fun onQuestionGroupSelected(questionGroup: QuestionGroupWithLanguage,pos:Int) {
        loadFragment(pos)
    }
    fun loadFragment(pos:Int){
        binding.recyclerView.post(Runnable { binding.recyclerView.scrollToPosition(pos) })
        if (pos==questionGroupList.size-1){
            binding.btnNext.visibility= View.GONE
            binding.btnContinue.visibility= View.VISIBLE
        }else{
            binding.btnNext.visibility= View.VISIBLE
            binding.btnContinue.visibility= View.GONE
        }
        questionGroupList[previouslySelected].selected=false
        adapterQuestionGroup.notifyItemChanged(previouslySelected)
        previouslySelected=pos
        questionGroupList[pos].selected=true
        adapterQuestionGroup.notifyItemChanged(pos)
        listOfFragment.forEachIndexed { index, fragment ->
            if (index==pos){
                supportFragmentManager.beginTransaction().show(listOfFragment[index]).commit()
            }else{
                supportFragmentManager.beginTransaction().hide(listOfFragment[index]).commit()
            }
        }

    }

    fun turnOnLocation(){
        try {
            val googleApiClient=GoogleApiClient.Builder(this).addApi(LocationServices.API).build()
            val req=LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
            req.setAlwaysShow(false)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun validateCommonQuestions():Boolean{
        if (commonAnswersEntity.banficary_name==""){
            UtilMethods.showToast(this,"Please add beneficiary name")
            return false
        }
        if (commonAnswersEntity.mst_state_id==""){
            UtilMethods.showToast(this,"Please select state")
            return false
        }
        if (commonAnswersEntity.mst_district_id==""){
            UtilMethods.showToast(this,"Please select district")
            return false
        }
        if (commonAnswersEntity.mst_tehsil_id==""){
            UtilMethods.showToast(this,"Please select tehsil")
            return false
        }
        if (commonAnswersEntity.mst_panchayat_id==""){
            UtilMethods.showToast(this,"Please select panchayath")
            return false
        }
        if (commonAnswersEntity.mst_village_id==""){
            UtilMethods.showToast(this,"Please select village")
            return false
        }
        if (commonAnswersEntity.gender==""){
            UtilMethods.showToast(this,"Please select gender")
            return false
        }
        if (commonAnswersEntity.family_size==""){
            UtilMethods.showToast(this,"Please add family size")
            return false
        }
        if (commonAnswersEntity.is_lpg_using==""){
            UtilMethods.showToast(this,"Please select is using LPG")
            return false
        }
        if (commonAnswersEntity.no_of_cylinder_per_year==""){
            UtilMethods.showToast(this,"Please add no of cylinder used")
            return false
        }
        if (commonAnswersEntity.is_cow_dung==""){
            UtilMethods.showToast(this,"Please select is cow dung is used")
            return false
        }
        if (commonAnswersEntity.no_of_cow_dung_per_day==""){
            UtilMethods.showToast(this,"Please add no of cow dung used")
            return false
        }
        if (commonAnswersEntity.house_type==""){
            UtilMethods.showToast(this,"Please select house type")
            return false
        }
        if (commonAnswersEntity.annual_family_income==""){
            UtilMethods.showToast(this,"Please select annual family income")
            return false
        }
        if (commonAnswersEntity.willing_to_contribute_clean_cooking==""){
            UtilMethods.showToast(this,"Please select weather he is willing to contribute to clean cooking")
            return false
        }
        if (commonAnswersEntity.wood_use_per_day_in_kg==""){
            UtilMethods.showToast(this,"Please select wood used per day")
            return false
        }
        if (commonAnswersEntity.electricity_connection_available==""){
            UtilMethods.showToast(this,"Please select electricity connection is available or not")
            return false
        }
        if (commonAnswersEntity.no_of_cattles_own==""){
            UtilMethods.showToast(this,"Please select no of cattle owned")
            return false
        }
        if (commonAnswersEntity.mobile_number==""){
            UtilMethods.showToast(this,"Please add mobile number")
            return false
        }
        if (commonAnswersEntity.mobile_number.length!=10){
            UtilMethods.showToast(this,"Please enter valid mobile number")
            return false
        }
        if (commonAnswersEntity.aadhar_card==""){
            UtilMethods.showToast(this,"Please add aadhar number")
            return false
        }
        if (commonAnswersEntity.aadhar_card.length!=12){
            UtilMethods.showToast(this,"Please enter valid aadhar number")
            return false
        }
        return true
    }
}