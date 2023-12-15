package com.renew.survey.views

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
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
import com.renew.survey.room.entities.CommonAnswersEntity
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
    var commonAnswersEntity: CommonAnswersEntity=CommonAnswersEntity(0,"","","","","","","","","","","","","","","","","","","","",0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFormsDetailsBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)
        getAllQuestionGroup()
        binding.recyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.btnContinue.setOnClickListener {
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

                val appUniqueCode="${preferenceManager.getUserId()}_${preferenceManager.getProject().id}_${preferenceManager.getForm().tbl_forms_id}_${questionGroupList[0].tbl_project_phase_id}_${UtilMethods.getFormattedDate(
                    Date(),"dd:MM:yyyy:HH:mm:ss")}"
                val ans=AnswerEntity(null,appUniqueCode,preferenceManager.getLanguage().toString(),"",questionGroupList[1].tbl_project_phase_id.toString(),preferenceManager.getForm().tbl_forms_id.toString(),questionGroupList[0].tbl_project_phase_id.toString(),preferenceManager.getProject().id.toString(),preferenceManager.getUserId().toString(),questionGroupList[0].version,0)
                val ansId=AppDatabase.getInstance(this@FormsDetailsActivity).formDao().insertAnswer(ans)
                commonAnswersEntity.answer_id=ansId.toInt()
                val commonAnsId=AppDatabase.getInstance(this@FormsDetailsActivity).formDao().insertCommonAnswer(commonAnswersEntity)
                for (qg in questionGroupList){
                    for(q in qg.questions){
                        val dynamicAnswersEntity=DynamicAnswersEntity(null,qg.mst_question_group_id,q.answer,q.tbl_form_questions_id,ansId.toInt())
                        AppDatabase.getInstance(this@FormsDetailsActivity).formDao().insertDynamicAnswer(dynamicAnswersEntity)
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
                        val fragment=CommonQuestionFragment(commonAnswersEntity)
                        listOfFragment.add(fragment)
                        supportFragmentManager.beginTransaction().add(R.id.container,fragment ).commit()
                    }else{
                        val fragment=QuestionsFragment(questionGroupWithLanguage.mst_question_group_id,index,questionGroupList)
                        listOfFragment.add(fragment)
                        supportFragmentManager.beginTransaction().add(R.id.container,fragment ).commit()
                    }
            }


            binding.recyclerView.adapter=QuestionGroupAdapter(this@FormsDetailsActivity,questionGroupList,this@FormsDetailsActivity)
            loadFragment(0)

        }
    }

    override fun onQuestionGroupSelected(questionGroup: QuestionGroupWithLanguage,pos:Int) {
        /*if (pos==0){
            loadCommonFragment()
        }else{
            loadQuestionFragment(questionGroup.mst_question_group_id)
        }*/
        loadFragment(pos)
    }
    fun loadFragment(pos:Int){
        listOfFragment.forEachIndexed { index, fragment ->
            if (index==pos){
                supportFragmentManager.beginTransaction().show(listOfFragment[index]).commit()
            }else{
                supportFragmentManager.beginTransaction().hide(listOfFragment[index]).commit()
            }
        }

    }

    private fun loadCommonFragment(){
        supportFragmentManager.beginTransaction().replace(R.id.container, listOfFragment[0]).commit();
    }
    private fun loadQuestionFragment(group:Int){
        /*for(q in questionGroupList){
            if (q.mst_question_group_id==group){

            }else{
                supportFragmentManager.beginTransaction().replace(R.id.container, QuestionsFragment(group,questionGroupList)).commit()
            }
        }*/

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