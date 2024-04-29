package com.renew.survey.views

import android.Manifest
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.renew.survey.R
import com.renew.survey.adapter.QuestionGroupAdapter
import com.renew.survey.databinding.ActivityFormsDetailsBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.AnswerEntity
import com.renew.survey.room.entities.AssignedFilterSurveyEntity
import com.renew.survey.room.entities.AssignedSurveyEntity
import com.renew.survey.room.entities.CommonAnswersEntity
import com.renew.survey.room.entities.DraftCommonAnswer
import com.renew.survey.room.entities.DynamicAnswersEntity
import com.renew.survey.room.entities.QuestionGroupWithLanguage
import com.renew.survey.room.entities.TestQuestionLanguage
import com.renew.survey.utilities.AppConstants
import com.renew.survey.utilities.DataAllowMetPerson
import com.renew.survey.utilities.UtilMethods
import com.renew.survey.views.fragments.CommonQuestionFragment
import com.renew.survey.views.fragments.QuestionsFragment
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale


class FormsDetailsActivity : BaseActivity() ,QuestionGroupAdapter.ClickListener,
    DataAllowMetPerson {
    lateinit var binding: ActivityFormsDetailsBinding
    private var questionGroupList= arrayListOf<QuestionGroupWithLanguage>()
    private var testquestionList : List<TestQuestionLanguage> = listOf()
    private var listOfFragment= arrayListOf<Fragment>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    var status=0
    var previouslySelected=0
    lateinit var adapterQuestionGroup:QuestionGroupAdapter
    var tbl_project_survey_common_data_id=""
    var assigned: AssignedFilterSurveyEntity?=null
    var draftAnsId:Int?=null
    var commonAnswersEntity: CommonAnswersEntity=CommonAnswersEntity(null,"","","","","","","","","","","","","","","","","","","","","","","","","","",
        "","","","","","","","","",0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormsDetailsBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)
        if (intent.hasExtra("assigned")){

            assigned=gson.fromJson(intent.getStringExtra("assigned"),AssignedFilterSurveyEntity::class.java)
            commonAnswersEntity= CommonAnswersEntity(
                null,assigned!!.aadhar_card,assigned!!.date_and_time_of_visit,assigned!!.did_the_met_person_allowed_for_data,assigned!!.gps_location,assigned!!.annual_family_income,assigned!!.banficary_name,assigned!!.do_you_have_aadhar_card,assigned!!.font_photo_of_aadar_card,assigned!!.back_photo_of_aadhar_card,assigned!!.electricity_connection_available,assigned!!.total_electricity_bill,assigned!!.frequency_of_bill_payment,assigned!!.photo_of_bill,assigned!!.family_size,assigned!!.gender,assigned!!.house_type,assigned!!.is_cow_dung,
                assigned!!.is_lpg_using,assigned!!.mobile_number,assigned!!.mst_district_id.toString(),assigned!!.mst_state_id.toString(),assigned!!.mst_tehsil_id.toString(),assigned!!.mst_panchayat_id.toString(),assigned!!.mst_village_id.toString(),assigned!!.no_of_cattles_own,assigned!!.no_of_cylinder_per_year,assigned!!.device_serial_number.toString(),assigned!!.cost_of_lpg_cyliner,assigned!!.willing_to_contribute_clean_cooking,
                assigned?.wood_use_per_day_in_kg!!,assigned!!.parent_survey_id,assigned!!.tbl_project_survey_common_data_id.toString(),assigned!!.family_member_below_15_year,
                assigned?.family_member_above_15_year,assigned!!.do_you_have_ration_or_aadhar,null
            )
            tbl_project_survey_common_data_id=assigned!!.tbl_project_survey_common_data_id.toString()
            if (assigned!!.next_form_id == 4) {
                status=assigned!!.next_form_id-1
            } else {
                status=assigned!!.next_form_id
            }
        }else if (intent.hasExtra("draft")){

            val a=gson.fromJson(intent.getStringExtra("draft"),DraftCommonAnswer::class.java)
            if(a.tbl_forms_id==4) {
                status=4+a.tbl_forms_id!!
            } else {
                status=3+a.tbl_forms_id!!
            }


            commonAnswersEntity= CommonAnswersEntity(
                null,a.aadhar_card,a.date_and_time_of_visit,a.did_the_met_person_allowed_for_data,a.gps_location,a.annual_family_income,a.banficary_name,a.do_you_have_aadhar_card,a.font_photo_of_aadar_card,a.back_photo_of_aadhar_card,a.electricity_connection_available,a.total_electricity_bill,a.frequency_of_bill_payment,a.photo_of_bill,a.family_size,a.gender,a.house_type,a.is_cow_dung,
                a.is_lpg_using,a.mobile_number,a.mst_district_id.toString(),a.mst_state_id.toString(),a.mst_tehsil_id.toString(),a.mst_panchayat_id.toString(),a.mst_village_id.toString(),a.no_of_cattles_own,a.no_of_cylinder_per_year,a.device_serial_number.toString(), a.cost_of_lpg_cyliner,a.willing_to_contribute_clean_cooking,a.wood_use_per_day_in_kg,a.parent_survey_id,a.tbl_project_survey_common_data_id.toString(),a.family_member_below_15_year,a.family_member_above_15_year,a.do_you_have_ration_or_aadhar,null
            )
        }
        if (intent.getBooleanExtra("training",false)) {
            lifecycleScope.launch {
                testquestionList = AppDatabase.getInstance(this@FormsDetailsActivity).formDao().getAllTestQuestions(preferenceManager.getLanguage(), 1)
            }
            val fragment=QuestionsFragment(0,0,questionGroupList,testquestionList,status,true)
            listOfFragment.add(fragment)
            supportFragmentManager.beginTransaction().add(R.id.container,fragment ).commit()
            binding.btnSaveDraft.visibility = View.GONE
            binding.btnNext.visibility = View.GONE
            binding.btnPrevious.visibility = View.GONE
            binding.btnContinue.visibility = View.VISIBLE
            binding.btnContinue.setOnClickListener {
                Log.d("testprojectdetails",intent.getStringExtra("trainingInfo").toString())
                fragment.onSubmit(intent.getStringExtra("trainingInfo"))
            }
        } else {
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
                val ans=AnswerEntity(null,appUniqueCode,preferenceManager.getLanguage().toString(),commonAnswersEntity.parent_survey_id,questionGroupList[1].tbl_project_phase_id.toString(),preferenceManager.getForm().tbl_forms_id.toString(),questionGroupList[1].tbl_project_phase_id.toString(),preferenceManager.getProject().id.toString(),preferenceManager.getUserId().toString(),questionGroupList[1].version,0,0,1)
                var ansId=0
                if (status>3){
                    ans.id=preferenceManager.getDraft()
                    ansId= preferenceManager.getDraft()
                    AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateAnswer(ans)
                } else {
                    ansId = AppDatabase.getInstance(this@FormsDetailsActivity).formDao()
                        .insertAnswer(ans).toInt()
                }
                val cm = AppDatabase.getInstance(this@FormsDetailsActivity).formDao().getCommonAnswers(ansId)

                commonAnswersEntity.answer_id=ansId.toInt()
                if (status>3){
                    commonAnswersEntity.id=cm.id
                    AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateCommonAnswer(commonAnswersEntity)
                } else {
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
                if (assigned!=null){
                    AppDatabase.getInstance(this@FormsDetailsActivity).formDao().deleteAssigned(assigned!!.id!!)
                }
                UtilMethods.showToast(this@FormsDetailsActivity, "Form saved in draft")
                finish()
            }

        }
        binding.btnNext.setOnClickListener {
            if (!validateCommonQuestions()){
                return@setOnClickListener
            }
            if (!validateDynamicQuestions()){
                return@setOnClickListener
            }
            saveInDraftEveryStep()
            loadFragment(previouslySelected+1)
        }
        binding.btnPrevious.setOnClickListener {
            if (previouslySelected>0){
                loadFragment(previouslySelected-1)
            }

        }
        binding.btnContinue.setOnClickListener {
            Log.d("question",gson.toJson(questionGroupList[1]))
            if (commonAnswersEntity.did_the_met_person_allowed_for_data== AppConstants.yes.uppercase(
                    Locale.ROOT)){
                if (!validateCommonQuestions()){
                    return@setOnClickListener
                }
                if (!validateDynamicQuestions()){
                    return@setOnClickListener
                }
            }

            lifecycleScope.launch {

                val appUniqueCode="${preferenceManager.getUserId()}_${preferenceManager.getProject().id}_${preferenceManager.getForm().tbl_forms_id}_${questionGroupList[1].tbl_project_phase_id}_${UtilMethods.getFormattedDate(
                    Date(),"dd:MM:yyyy:HH:mm:ss")}"
                val ans=AnswerEntity(null,appUniqueCode,preferenceManager.getLanguage().toString(),commonAnswersEntity.parent_survey_id,questionGroupList[1].tbl_project_phase_id.toString(),preferenceManager.getForm().tbl_forms_id.toString(),questionGroupList[1].tbl_project_phase_id.toString(),preferenceManager.getProject().id.toString(),preferenceManager.getUserId().toString(),questionGroupList[1].version,0,0,0)
                ans.tbl_project_survey_common_data_id=tbl_project_survey_common_data_id
                var ansId=0
                if (status>3){
                    ans.id=preferenceManager.getDraft()
                    ansId= preferenceManager.getDraft()
                    Log.e("patedsj",gson.toJson(ans))
                    AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateAnswer(ans)
                } else {
                    ansId = AppDatabase.getInstance(this@FormsDetailsActivity).formDao()
                        .insertAnswer(ans).toInt()
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
                if (intent.hasExtra("assigned")){
                    AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateAssignedStatus(assigned!!.id!!)
                }
                UtilMethods.showToast(this@FormsDetailsActivity, "Form saved successfully")
                finish()
            }
        }

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
            enableLoc()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        preferenceManager.saveLocation("${location.latitude},${location.longitude}")
                    }
                }
            }
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    preferenceManager.saveLocation("${location.latitude},${location.longitude}")
                }
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
                        val fragment=CommonQuestionFragment(commonAnswersEntity,status,this@FormsDetailsActivity)
                        listOfFragment.add(fragment)
                        supportFragmentManager.beginTransaction().add(R.id.container,fragment).commit()
                    }else{
                        val fragment=QuestionsFragment(
                            questionGroupWithLanguage.mst_question_group_id,
                            index,
                            questionGroupList,
                            testquestionList,
                            status,
                            false
                        )
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
        if (preferenceManager.getForm().tbl_forms_id == 2 && pos != 0) {
            val retrievedList = preferenceManager.getProjOtpVerification("projectVerify")
            if (retrievedList?.contains(commonAnswersEntity.aadhar_card ?: "") == false || retrievedList.isNullOrEmpty()) {
            UtilMethods.showToast(this,"Please complete OTP verification")
            } else {
                loadScreen(pos)
            }
        } else {
            loadScreen(pos)
        }

    }

    fun loadScreen(pos:Int) {
        binding.recyclerView.post(Runnable { binding.recyclerView.scrollToPosition(pos) })
        if (pos==questionGroupList.size-1){
            binding.btnNext.visibility= View.GONE
            binding.btnContinue.visibility= View.VISIBLE
        }else{
            binding.btnNext.visibility= View.VISIBLE
            binding.btnContinue.visibility= View.GONE
        }
        Log.d("checkscroll",pos.toString())
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

    fun validateCommonQuestions():Boolean{
        if (commonAnswersEntity.date_and_time_of_visit==""){
            UtilMethods.showToast(this,"Please add date and time of visit")
            return false
        }
        if (commonAnswersEntity.did_the_met_person_allowed_for_data==""){
            UtilMethods.showToast(this,"Please Select person allowed for data")
            return false
        }
        if (commonAnswersEntity.gps_location==""){
            UtilMethods.showToast(this,"Please add GPS Location")
            return false
        }
        if (commonAnswersEntity.banficary_name==""){
            UtilMethods.showToast(this,"Please add beneficiary name")
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


        if (commonAnswersEntity.do_you_have_aadhar_card == "YES") {
            if (commonAnswersEntity.aadhar_card=="" ){
                UtilMethods.showToast(this,"Please add aadhar number")
                return false
            }

            if (commonAnswersEntity.aadhar_card.length!=12){
                UtilMethods.showToast(this,"Please enter valid aadhar number")
                return false
            }
        }

        if (commonAnswersEntity.do_you_have_aadhar_card==""){
            UtilMethods.showToast(this,"Please select aadhar status")
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

        if (commonAnswersEntity.family_member_above_15_year==""){
            UtilMethods.showToast(this,"Please add family member below 15")
            return false
        }
        if (commonAnswersEntity.family_member_below_15_year==""){
            UtilMethods.showToast(this,"Please add family member above 15")
            return false
        }

        if (commonAnswersEntity.is_lpg_using==""){
            UtilMethods.showToast(this,"Please select is using LPG")
            return false
        }
        if (commonAnswersEntity.no_of_cylinder_per_year==getString(R.string.yes)){
            if (commonAnswersEntity.no_of_cylinder_per_year==""){
                UtilMethods.showToast(this,"Please add no of cylinder used")
                return false
            }
        }
        /*if (commonAnswersEntity.is_cow_dung==""){
            UtilMethods.showToast(this,"Please select is cow dung is used")
            return false
        }*/
        /*if (commonAnswersEntity.is_cow_dung==getString(R.string.yes)){
            if (commonAnswersEntity.no_of_cow_dung_per_day==""){
                UtilMethods.showToast(this,"Please add no of cow dung used")
                return false
            }
        }*/
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
       /* if (commonAnswersEntity.wood_use_per_day_in_kg==""){
            UtilMethods.showToast(this,"Please select wood used per day")
            return false
        }*/
        if (commonAnswersEntity.electricity_connection_available==""){
            UtilMethods.showToast(this,"Please select electricity connection is available or not")
            return false
        }
        if (commonAnswersEntity.no_of_cattles_own==""){
            UtilMethods.showToast(this,"Please select no of cattle owned")
            return false
        }


        if (preferenceManager.getForm().tbl_forms_id==1){
            val totalFamily=commonAnswersEntity.family_member_above_15_year!!.toInt() + commonAnswersEntity.family_member_below_15_year!!.toInt()-commonAnswersEntity.family_size.toInt()
            if(totalFamily!=0){
                UtilMethods.showToast(this,"Please enter valid family number")
                return false
            }
        }

        if (preferenceManager.getForm().tbl_forms_id==2 || preferenceManager.getForm().tbl_forms_id==3 ) {
            Log.d("testSerial",commonAnswersEntity.device_serial_number)
            if (commonAnswersEntity.device_serial_number == "" || commonAnswersEntity.device_serial_number.isNullOrEmpty()) {
                UtilMethods.showToast(this,"Please enter serial number")
                return false
            }
        }

        return true
    }
    interface ClickListener{
        fun onSubmit(stringExtra: String?)
    }

    fun validateDynamicQuestions():Boolean{
        questionGroupList.forEachIndexed { index, qg ->
            if (index <= previouslySelected){
                for (q in qg.questions) {
                    if (q.is_mandatory.equals("yes", ignoreCase = true)) {
                        if (q.answer.equals("")) {
                            UtilMethods.showToast(
                                this@FormsDetailsActivity,
                                "Please add/select ${q.title} in question group ${qg.title}"
                            )
                            return false
                        }
                        if (q.is_validation_required.equals("yes",ignoreCase = true)){
                            if (q.question_type=="TEXT" || q.question_type=="NUMBER"||q.question_type=="EMAIL"){
                                if (q.answer!!.length>q.max_length.toInt() ||q.answer!!.length<q.min_length.toInt()){
                                    UtilMethods.showToast(
                                        this@FormsDetailsActivity,
                                        "Please add valid answer ${q.title} in question group ${qg.title}"
                                    )
                                    return false
                                }
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    private fun enableLoc() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (30 * 1000).toLong()
        locationRequest.fastestInterval = (5 * 1000).toLong()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        result.addOnCompleteListener(OnCompleteListener<LocationSettingsResponse?> { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                         // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                this@FormsDetailsActivity,
                                LOCATION_SETTINGS_REQUEST
                            )
                        } catch (e: SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        })
    }
    val LOCATION_SETTINGS_REQUEST=88;


    fun saveInDraftEveryStep(){
        lifecycleScope.launch {
            val appUniqueCode="${preferenceManager.getUserId()}_${preferenceManager.getProject().id}_${preferenceManager.getForm().tbl_forms_id}_${questionGroupList[1].tbl_project_phase_id}_${UtilMethods.getFormattedDate(
                Date(),"dd:MM:yyyy:HH:mm:ss")}"
            val ans=AnswerEntity(draftAnsId,appUniqueCode,preferenceManager.getLanguage().toString(),commonAnswersEntity.parent_survey_id,questionGroupList[1].tbl_project_phase_id.toString(),preferenceManager.getForm().tbl_forms_id.toString(),questionGroupList[1].tbl_project_phase_id.toString(),preferenceManager.getProject().id.toString(),preferenceManager.getUserId().toString(),questionGroupList[1].version,0,0,1)
            if (status>3){
                ans.id=preferenceManager.getDraft()
                draftAnsId= preferenceManager.getDraft()

            } else {
                draftAnsId = AppDatabase.getInstance(this@FormsDetailsActivity).formDao()
                    .insertAnswer(ans).toInt()
            }
            val cm = AppDatabase.getInstance(this@FormsDetailsActivity).formDao().getCommonAnswers(draftAnsId!!)

            commonAnswersEntity.answer_id=draftAnsId!!.toInt()
            if (status>3){
                commonAnswersEntity.id=cm.id
                AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateCommonAnswer(commonAnswersEntity)
            } else {
                AppDatabase.getInstance(this@FormsDetailsActivity).formDao().insertCommonAnswer(commonAnswersEntity)
            }

            for (qg in questionGroupList){
                for(q in qg.questions){
                    if(status>3){
                        Log.d("UpdatingQeuery","ans ${q.answer} group=${qg.mst_question_group_id} question=${q.tbl_form_questions_id}")
                        AppDatabase.getInstance(this@FormsDetailsActivity).formDao().updateDynamicAnswer(q.answer!!,qg.mst_question_group_id,q.tbl_form_questions_id,draftAnsId!!)
                    } else{
                        val dynamicAnswersEntity=DynamicAnswersEntity(null,qg.mst_question_group_id,q.answer,q.tbl_form_questions_id,draftAnsId!!.toInt())
                        AppDatabase.getInstance(this@FormsDetailsActivity).formDao().insertDynamicAnswer(dynamicAnswersEntity)
                    }
                }
            }
            if (status < 4){
                status=3 + ans.tbl_forms_id.toInt()
                preferenceManager.saveDraft(draftAnsId!!)
            }
            if (assigned!=null){
                AppDatabase.getInstance(this@FormsDetailsActivity).formDao().deleteAssigned(assigned!!.id!!)
            }
        }
    }

    override fun dataAllowed(boolean: Boolean) {
        allowedByPerson=boolean
        if (boolean){
            binding.llItem.visibility=View.GONE
            binding.btnNext.visibility=View.VISIBLE
            binding.btnContinue.visibility=View.GONE
            binding.recyclerView.visibility=View.VISIBLE
        }else{
            binding.llItem.visibility=View.VISIBLE
            binding.recyclerView.visibility=View.GONE
            binding.btnNext.visibility=View.GONE
            binding.btnContinue.visibility=View.VISIBLE
        }
    }
    var allowedByPerson=true
}