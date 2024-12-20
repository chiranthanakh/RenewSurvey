package com.renew.globalsurvey.views.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.renew.globalsurvey.R
import com.renew.globalsurvey.adapter.DistrictSpinnerAdapter
import com.renew.globalsurvey.adapter.PanchayatSpinnerAdapter
import com.renew.globalsurvey.adapter.StateSpinnerAdapter
import com.renew.globalsurvey.adapter.TehsilSpinnerAdapter
import com.renew.globalsurvey.adapter.VillageSpinnerAdapter
import com.renew.globalsurvey.databinding.FragmentCommonQuestionBinding
import com.renew.globalsurvey.helper.compressor.Compressor
import com.renew.globalsurvey.response.Data
import com.renew.globalsurvey.response.DistrictModel
import com.renew.globalsurvey.response.PanchayathModel
import com.renew.globalsurvey.response.StateModel
import com.renew.globalsurvey.response.TehsilModel
import com.renew.globalsurvey.response.VillageModel
import com.renew.globalsurvey.room.AppDatabase
import com.renew.globalsurvey.room.entities.CommonAnswersEntity
import com.renew.globalsurvey.room.entities.DistrictEntity
import com.renew.globalsurvey.room.entities.PanchayathEntity
import com.renew.globalsurvey.room.entities.StatesEntity
import com.renew.globalsurvey.room.entities.TehsilEntity
import com.renew.globalsurvey.room.entities.VillageEntity
import com.renew.globalsurvey.utilities.ApiInterface
import com.renew.globalsurvey.utilities.AppConstants
import com.renew.globalsurvey.utilities.DataAllowMetPerson
import com.renew.globalsurvey.utilities.FileUtils
import com.renew.globalsurvey.utilities.PreferenceManager
import com.renew.globalsurvey.utilities.UtilMethods
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


class CommonQuestionFragment constructor(var commonAnswersEntity: CommonAnswersEntity,val status:Int,val listener:DataAllowMetPerson) : Fragment() {
    lateinit var binding:FragmentCommonQuestionBinding
    var stateList= arrayListOf<StateModel>();
    var frequancy= arrayListOf<String>();
    var districtList= arrayListOf<DistrictModel>()
    var tehsilList= arrayListOf<TehsilModel>()
    var panchayathList= arrayListOf<PanchayathModel>()
    var villageList= arrayListOf<VillageModel>()
    var disableViews=false
    var serialNumber : String? = ""
    lateinit var preferenceManager:PreferenceManager
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val hour = c.get(Calendar.HOUR)
    val minute = c.get(Calendar.MINUTE)
    private var otp: Int? = null
    private lateinit var countDownTimer: CountDownTimer
    private var timerRunning = false
    private val totalTimeInMillis: Long = 60000 // 1 minute
    val gson= Gson()
    var listOfFragment= arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentCommonQuestionBinding.inflate(inflater,container,false)
        preferenceManager= PreferenceManager(requireContext())
        Log.e("status","$status")
        if (status==2||status==3||status==5||status==6||status==7||status==8){
            disableViews=true
        }
        hideviews()
        if (status==0){
            commonAnswersEntity.do_you_have_aadhar_card = "YES"
            commonAnswersEntity.did_the_met_person_allowed_for_data = "YES"
           // commonAnswersEntity.frequency_of_bill_payment = "YES"
            commonAnswersEntity.electricity_connection_available = "YES"
            commonAnswersEntity.is_lpg_using = "YES"
            commonAnswersEntity.frequency_of_bill_payment = "Once/Month"
        }

        if(preferenceManager.getForm().tbl_forms_id == 1) {
            binding.llDeviceSerialNumber.visibility = View.GONE
        }
        if(preferenceManager.getForm().tbl_forms_id == 3) {
            Log.d("serialNumberTest6",commonAnswersEntity.device_serial_number)

            serialNumber = commonAnswersEntity.device_serial_number
            commonAnswersEntity.device_serial_number = ""
        }
        if (preferenceManager.getForm().tbl_forms_id == 2) {
            listOfFragment.clear()
            val retrievedList = preferenceManager.getProjOtpVerification("projectVerify")
            retrievedList?.forEach {
                listOfFragment.add(it)
            }
            Log.d("checkOtpver",listOfFragment.toString() +"--"+ commonAnswersEntity.aadhar_card)
            if (listOfFragment.contains(commonAnswersEntity.aadhar_card ?: "")) {
                binding.llOtp.visibility = View.GONE
            } else {
                binding.llOtp.visibility = View.VISIBLE
            }
        }

        binding.btnSend.setOnClickListener {
            if (UtilMethods.isNetworkAvailable(requireContext())){
                callVerifyOtp()
                startTimer()
                binding.btnVerify.visibility = View.VISIBLE
                binding.btnSend.visibility = View.GONE
            }else{
                UtilMethods.showToast(requireContext(),"Please check your internet connection")
            }
        }

        binding.btnVerify.setOnClickListener {
            if (!binding.edtVerifyOtp.text.toString().isNullOrBlank()) {
                verifyOtp()
            } else {
                UtilMethods.showToast(requireContext(),"Please enter OTP")
            }
        }

        val calendar: Calendar = Calendar.getInstance()
        val currentDate: Date = calendar.time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val formattedDateTime: String = dateFormat.format(currentDate)
        binding.edtDateAndTime.setText(formattedDateTime)
        commonAnswersEntity.date_and_time_of_visit=UtilMethods.getFormattedDate(calendar.time,"dd-MM-yyyy HH:mm")


        binding.rbLpgYes.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.is_lpg_using=AppConstants.yes
                binding.llCylinderCost.visibility = View.VISIBLE
                binding.llNoOfCylinder.visibility = View.VISIBLE

            }
        }
        binding.rbLpgNo.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.is_lpg_using=AppConstants.no
                binding.llCylinderCost.visibility = View.GONE
                binding.llNoOfCylinder.visibility = View.GONE
            }
        }
        binding.rbData.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { arg0, id ->
            when (id) {
                R.id.rb_data_no -> {
                    listener.dataAllowed(false)
                    binding.llDataState.visibility = View.GONE
                    commonAnswersEntity.did_the_met_person_allowed_for_data = "NO"
                }
                R.id.rb_data_yes -> {
                    listener.dataAllowed(true)
                    binding.llDataState.visibility = View.VISIBLE
                    commonAnswersEntity.did_the_met_person_allowed_for_data = "YES"
                }
            }
        })

        binding.rbAadhar.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { arg0, id ->
            when (id) {
                R.id.rb_aadhar_no -> {
                    binding.edtAadharText.visibility = View.GONE
                    binding.edtAadhaarCard.visibility = View.GONE
                    binding.llABack.visibility = View.GONE
                    binding.llAFront.visibility = View.GONE
                    commonAnswersEntity.do_you_have_aadhar_card = "NO"
                }
                R.id.rb_aadhar_yes -> {
                    binding.edtAadharText.visibility = View.VISIBLE
                    binding.edtAadhaarCard.visibility = View.VISIBLE
                    binding.llABack.visibility = View.VISIBLE
                    binding.llAFront.visibility = View.VISIBLE
                    commonAnswersEntity.do_you_have_aadhar_card = "YES"
                }
            }
        })

        binding.rbAadharOrRation.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { arg0, id ->
            when (id) {
                R.id.rb_a_or_r_yes -> {
                    commonAnswersEntity.do_you_have_ration_or_aadhar = "YES"
                }
                R.id.rb_a_or_r_no -> {
                    commonAnswersEntity.do_you_have_ration_or_aadhar = "NO"
                }
            }
        })

        binding.rbFarmlandIsOwnedBy.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { arg0, id ->
            when (id) {
                R.id.rb_farmland_yes -> {
                    commonAnswersEntity.farmland_is_owned_by_benficary = "YES"
                }
                R.id.rb_farmland_no -> {
                    commonAnswersEntity.farmland_is_owned_by_benficary = "NO"
                }
            }
        })

        binding.rbAreaIsAvailableNearBy.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { arg0, id ->
            when (id) {
                R.id.rb_available_near_yes -> {
                    commonAnswersEntity.if_5m_area_is_available_near_by = "YES"
                }
                R.id.rb_available_near_no -> {
                    commonAnswersEntity.if_5m_area_is_available_near_by = "NO"
                }
            }
        })
        binding.rbElectricityYes.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.electricity_connection_available=AppConstants.yes
                commonAnswersEntity.frequency_of_bill_payment = "Once/Month"
                binding.llEBill.visibility = View.VISIBLE
                binding.eConnectionBill.visibility = View.VISIBLE
                binding.edtTotalEBill.visibility = View.VISIBLE
                binding.tvEFrequancy.visibility = View.VISIBLE
                binding.llFrequancy.visibility = View.VISIBLE
            }
        }
        binding.rbElectricityNo.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.electricity_connection_available=AppConstants.no
                binding.llEBill.visibility = View.GONE
                binding.eConnectionBill.visibility = View.GONE
                binding.edtTotalEBill.visibility = View.GONE
                binding.tvEFrequancy.visibility = View.GONE
                binding.llFrequancy.visibility = View.GONE
                binding.edtTotalEBill.setText("")
                binding.tvEFrequancy.visibility = View.GONE
                binding.llFrequancy.visibility = View.GONE
            }
        }

        binding.rgLpg.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { arg0, id ->
            when (id) {
                R.id.rb_lpg_no -> {
                    binding.llNoOfCylinder.visibility=View.GONE
                    binding.llCylinderCost.visibility = View.GONE
                }
                R.id.rb_lpg_yes -> {
                    binding.llNoOfCylinder.visibility=View.VISIBLE
                    binding.llCylinderCost.visibility = View.VISIBLE
                }
            }
        })

        if (status>0){
            binding.edtBeneficiaryName.setText(commonAnswersEntity.banficary_name)
            binding.edtMobile.setText(commonAnswersEntity.mobile_number)
            binding.edtCattleOwn.setText(commonAnswersEntity.no_of_cattles_own)
            binding.edtAadhaarCard.setText(commonAnswersEntity.aadhar_card)
            binding.edtWoodUsed.setText(commonAnswersEntity.wood_use_per_day_in_kg)
            binding.edtNoCylinderYear.setText(commonAnswersEntity.no_of_cylinder_per_year)
            binding.edtAnnualFamilyIncome.setText(commonAnswersEntity.annual_family_income)
            binding.edtFamilySize.setText(commonAnswersEntity.family_size)
            binding.edtAbove15.setText(commonAnswersEntity.family_member_above_15_year)
            binding.edtBelow15.setText(commonAnswersEntity.family_member_below_15_year)
            binding.edtGpsLocation.setText(commonAnswersEntity.gps_location)
            binding.edtDateAndTime.setText(commonAnswersEntity.date_and_time_of_visit)
            binding.edtCylinderCost.setText(commonAnswersEntity.cost_of_lpg_cyliner)
            binding.edtTotalEBill.setText(commonAnswersEntity.total_electricity_bill)
            binding.tvAadharBack.setText(commonAnswersEntity.back_photo_of_aadhar_card.substring(commonAnswersEntity.back_photo_of_aadhar_card.lastIndexOf("/")+1))
            binding.tvAadharFront.setText(commonAnswersEntity.font_photo_of_aadar_card.substring(commonAnswersEntity.font_photo_of_aadar_card.lastIndexOf("/")+1))
            binding.tvBillPhoto.setText(commonAnswersEntity.photo_of_bill.substring(commonAnswersEntity.photo_of_bill.lastIndexOf("/")+1))
            if (commonAnswersEntity.device_serial_number.equals("null")) {
                binding.edtDeviceSerialNumber.setText(" ")
            } else {
                binding.edtDeviceSerialNumber.setText(commonAnswersEntity.device_serial_number)
               // binding.edtDeviceSerialNumber.isEnabled = false
            }

            when(commonAnswersEntity.gender.uppercase(Locale.ROOT)){
                AppConstants.male.uppercase(Locale.ROOT) ->{
                    binding.rbMale.isChecked=true
                }
                AppConstants.female.uppercase(Locale.ROOT)->{
                    binding.rbFemale.isChecked=true
                }
                AppConstants.other.uppercase(Locale.ROOT)->{
                    binding.rbOther.isChecked=true
                }
            }
            if (commonAnswersEntity.is_lpg_using.toUpperCase(Locale.ROOT)==AppConstants.yes.uppercase(Locale.ROOT)){
                binding.rbLpgYes.isChecked=true
            }else{
                binding.rbLpgNo.isChecked=true
            }
            if (commonAnswersEntity.is_cow_dung==AppConstants.yes.uppercase(Locale.ROOT)){
                binding.rbCowDungYes.isChecked=true
            }else{
                binding.rbCowDungNo.isChecked=true
            }
            if (commonAnswersEntity.house_type==getString(R.string.own).uppercase(Locale.ROOT)){
                binding.rbOwn.isChecked=true
            }else{
                binding.rbRented.isChecked=true
            }
            if (commonAnswersEntity.willing_to_contribute_clean_cooking==AppConstants.yes.uppercase(Locale.ROOT)){
                binding.rbCleanYes.isChecked=true
            }else{
                binding.rbCleanNo.isChecked=true
            }
            if (commonAnswersEntity.electricity_connection_available==AppConstants.yes.uppercase(Locale.ROOT)){
                binding.rbElectricityYes.isChecked=true
            }else{
                binding.rbElectricityNo.isChecked=true
            }
            if (commonAnswersEntity.did_the_met_person_allowed_for_data==AppConstants.yes.uppercase(Locale.ROOT)){
                binding.rbDataYes.isChecked=true
            }else{
                binding.rbDataNo.isChecked=true
            }
            if (commonAnswersEntity.do_you_have_aadhar_card==AppConstants.yes.uppercase(Locale.ROOT)){
                binding.rbAadharYes.isChecked=true
            }else{
                binding.rbAadharNo.isChecked=true
            }
            if (commonAnswersEntity.do_you_have_ration_or_aadhar==AppConstants.yes.uppercase(Locale.ROOT)){
                binding.rbAOrRYes.isChecked=true
            }else{
                binding.rbAOrRNo.isChecked=true
            }
            if (commonAnswersEntity.farmland_is_owned_by_benficary==AppConstants.yes.uppercase(Locale.ROOT)){
                binding.rbFarmlandYes.isChecked=true
            }else{
                binding.rbFarmlandNo.isChecked=true
            }
            if (commonAnswersEntity.if_5m_area_is_available_near_by==AppConstants.yes.uppercase(Locale.ROOT)){
                binding.rbAvailableNearYes.isChecked=true
            }else{
                binding.rbAvailableNearNo.isChecked=true
            }
            if (disableViews){
                binding.rbFarmlandYes.isEnabled=false
                binding.rbFarmlandNo.isEnabled=false
                binding.rbAvailableNearYes.isEnabled=false
                binding.rbAvailableNearNo.isEnabled=false
                binding.rbAadharNo.isEnabled=false
                binding.rbAadharYes.isEnabled=false
                binding.rbDataYes.isEnabled=false
                binding.rbDataNo.isEnabled=false
                binding.rbAOrRYes.isEnabled=false
                binding.rbAOrRNo.isEnabled=false
                binding.rbMale.isEnabled=false
                binding.rbFemale.isEnabled=false
                binding.rbOther.isEnabled=false
                binding.rbLpgYes.isEnabled=false
                binding.rbLpgNo.isEnabled=false
                binding.rbCowDungYes.isEnabled=false
                binding.rbCowDungNo.isEnabled=false
                binding.rbRented.isEnabled=false
                binding.rbOwn.isEnabled=false
                binding.rbCleanYes.isEnabled=false
                binding.rbCleanNo.isEnabled=false
                binding.rbElectricityYes.isEnabled=false
                binding.rbElectricityNo.isEnabled=false
                binding.edtBelow15.isEnabled=false
                binding.edtAbove15.isEnabled=false
                for (i in 0 until binding.llLayout.getChildCount()) {
                    val child: View = binding.llLayout.getChildAt(i)
                    child.isEnabled = false
                }
                for (i in 0 until binding.llDataState.getChildCount()) {
                    val child: View = binding.llDataState.getChildAt(i)
                    child.isEnabled = false
                }
                binding.edtNoCylinderYear.isEnabled=false
                binding.edtCylinderCost.isEnabled=false
                binding.spFrequancy.isEnabled=false
                if (preferenceManager.getForm().tbl_forms_id == 4){
                    binding.edtGpsLocation.isEnabled = false
                    binding.edtDateAndTime.isEnabled = false
                    binding.edtDeviceSerialNumber.isEnabled = false
                } else {
                    binding.edtGpsLocation.isEnabled = true
                    binding.edtDateAndTime.isEnabled = true
                }
            }
        }
        getStateData()
        geFrequancyData()
        spinnerSelectors()

        binding.edtDateAndTime.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val calendar= Calendar.getInstance()
                calendar[Calendar.DAY_OF_MONTH]=dayOfMonth
                calendar[Calendar.MONTH]=month
                calendar[Calendar.YEAR]=year
                val tpd = TimePickerDialog(context,
                    TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                    calendar[Calendar.HOUR]=i
                    calendar[Calendar.MINUTE]=i2
                    binding.edtDateAndTime.setText(UtilMethods.getFormattedDate(calendar.time,"dd-MM-yyyy HH:mm"))
                    commonAnswersEntity.date_and_time_of_visit=UtilMethods.getFormattedDate(calendar.time,"dd-MM-yyyy HH:mm")
                },hour,minute,false)
                tpd.show()
                binding.edtDateAndTime.setText(UtilMethods.getFormattedDate(calendar.time,"dd-MM-yyyy HH:mm"))
            }, year, month, day)
            dpd.show()
        }
        binding.edtGpsLocation.setOnClickListener {
            binding.edtGpsLocation.setText(preferenceManager.getLocation())
            if (preferenceManager.getLocation()==""){
                UtilMethods.showToast(requireContext(),"Location not available. Please make sure that you enabled the location and internet in your device")
            }else{
                commonAnswersEntity.gps_location = preferenceManager.getLocation().toString()
            }
        }
        binding.llAadharBack.setOnClickListener {
            if (!disableViews)
                openCamera(AADHAR_BACK)
        }
        binding.llAadharFront.setOnClickListener {
            if (!disableViews)
                openCamera(AADHAR_FRONT)
        }
        binding.llBillImage.setOnClickListener {
            if (!disableViews)
                openCamera(BILL_IMAGE)
        }

        binding.rgCowDung.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { arg0, id ->
            when (id) {
                R.id.rb_cow_dung_no -> {
                    binding.llNoCowDung.visibility=View.GONE
                }
                R.id.rb_cow_dung_yes -> {
                    binding.llNoCowDung.visibility=View.VISIBLE
                }
            }
        })
        binding.edtAbove15.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.family_member_above_15_year=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.edtBelow15.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.family_member_below_15_year=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.edtBeneficiaryName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.banficary_name=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.edtDeviceSerialNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("serialNumberTest1",serialNumber.toString()+"  "+ preferenceManager.getForm().tbl_forms_id)
                if (preferenceManager.getForm().tbl_forms_id == 3) {
                    Log.d("serialNumberTest2",p0.toString())

                    if (serialNumber.equals(p0.toString())) {
                        Log.d("serialNumberTest3",p0.toString())
                        commonAnswersEntity.device_serial_number=p0.toString().trim()
                    }
                } else {
                    commonAnswersEntity.device_serial_number=p0.toString().trim()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.rbMale.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.gender=AppConstants.male
            }
        }
        binding.rbFemale.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.gender=AppConstants.female
            }
        }
        binding.rbOther.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.gender=AppConstants.other
            }
        }
        binding.edtFamilySize.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.family_size=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.edtTotalEBill.addTextChangedListener(object  : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.total_electricity_bill=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.edtCylinderCost.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.cost_of_lpg_cyliner=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.edtNoCylinderYear.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.no_of_cylinder_per_year=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.rbCowDungYes.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.is_cow_dung=AppConstants.yes
            }
        }
        binding.rbCowDungNo.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.is_cow_dung=AppConstants.no
            }
        }
        binding.edtNoCowDungPerDay.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //commonAnswersEntity.no_of_cow_dung_per_day=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.rbOwn.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.house_type=AppConstants.owned
            }
        }
        binding.rbRented.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.house_type=AppConstants.rented
            }
        }
        binding.edtAnnualFamilyIncome.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.annual_family_income=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.rbCleanYes.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.willing_to_contribute_clean_cooking=AppConstants.yes
            }
        }
        binding.rbCleanNo.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.willing_to_contribute_clean_cooking=AppConstants.no
            }
        }
        binding.edtWoodUsed.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.wood_use_per_day_in_kg=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })


        binding.edtCattleOwn.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.no_of_cattles_own=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.edtMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.mobile_number=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.edtAadhaarCard.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.aadhar_card=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        countDownTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimerText(millisUntilFinished)
            }

            override fun onFinish() {
                timerRunning = false
                updateTimerText(0)
                binding.btnSend.visibility = View.VISIBLE
                binding.btnSend.setText("Resend OTP")
            }
        }


        return binding.root
    }

    private fun hideviews() {
        if(preferenceManager.getProject().mst_categories_id == 1){
            binding.llCylinderCost.visibility = View.GONE
            binding.llElectricity.visibility = View.GONE

        } else if(preferenceManager.getProject().mst_categories_id == 9){
            binding.llCylinderCost.visibility = View.GONE
            binding.llCattel.visibility = View.GONE

        } else if(preferenceManager.getProject().mst_categories_id == 10){
            binding.llCylinderCost.visibility = View.GONE
            binding.llEleonly.visibility = View.GONE

        }
    }

    fun geFrequancyData(){
        frequancy.add("Once/Month")
        frequancy.add("Once/2Month")
        frequancy.add("Once/3Month")
        frequancy.add("Once/4Month")
        frequancy.add("Annualy")

        lifecycleScope.launch {
            if (status>0){
                frequancy.forEachIndexed { index, stateModel ->
                    if (frequancy.get(index) == commonAnswersEntity.frequency_of_bill_payment){
                        binding.spFrequancy.setSelection(index)
                        return@forEachIndexed
                    }
                }
                if (disableViews)
                    binding.spFrequancy.isEnabled=false
            }
        }
    }

    private fun callVerifyOtp() {
        ApiInterface.getInstance()?.apply {
            lifecycleScope.launch {
                val responce = sendOtpForDistrubution(
                    preferenceManager.getToken()!!,
                    preferenceManager.getUserId()!!
                )
                if (responce.isSuccessful) {
                    val jsonObject= JSONObject(responce.body().toString())
                    if (jsonObject.getString("success")=="1") {
                        val data = gson.fromJson(
                            jsonObject.getString("data").toString(),
                            Data::class.java
                        )
                        otp = data.otp
                        UtilMethods.showToast(requireContext(),"OTP: " + data.otp)
                        Log.e("response", data.toString())
                    }
                }
            }
        }
    }

    private fun verifyOtp() {
        if (otp == binding.edtVerifyOtp.text.toString().toInt()) {
            listOfFragment.add(commonAnswersEntity.aadhar_card)
            Log.d("checkOtpver",listOfFragment.size.toString())
            preferenceManager.saveProjOtpVerification("projectVerify",listOfFragment)
            UtilMethods.showToast(requireContext(),"OTP Verification Successful" )
            binding.btnVerify.visibility = View.GONE
            binding.btnSend.visibility = View.GONE
            binding.tvTimmer.visibility = View.GONE
            binding.edtVerifyOtp.isEnabled = false
        } else {
            UtilMethods.showToast(requireContext(),"OTP Verification Failed, please enter proper OTP")
        }
    }

    private fun startTimer() {
        countDownTimer.start()
        timerRunning = true
    }

    private fun updateTimerText(timeInMillis: Long) {
        val minutes = (timeInMillis / 1000) / 60
        val seconds = (timeInMillis / 1000) % 60

        val timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)
        binding.tvTimmer?.text = "Resend OTP in : " + timeLeftFormatted + " sec"
    }
    fun getStateData(){
        lifecycleScope.launch {
            stateList=AppDatabase.getInstance(requireContext()).placesDao().getAllStates().transformState() as ArrayList<StateModel>
            stateList.add(0,StateModel("","Select State"))
            binding.spState.adapter=StateSpinnerAdapter(requireContext(),stateList)
            if (status>0){
                stateList.forEachIndexed { index, stateModel ->
                    if (stateModel.mst_state_id==commonAnswersEntity.mst_state_id){
                        binding.spState.setSelection(index)
                        return@forEachIndexed
                    }
                }
                if (disableViews)
                    binding.spState.isEnabled=false
            }
        }
    }

    fun getDistrict(state: Int){
        lifecycleScope.launch {
            districtList=AppDatabase.getInstance(requireContext()).placesDao().getAllDistricts(state).transformDistrict() as ArrayList<DistrictModel>
            districtList.add(0, DistrictModel("Select District",""))
            binding.spDistrict.adapter=DistrictSpinnerAdapter(requireContext(),districtList)
            if (status>0){
                districtList.forEachIndexed { index, districtModel ->
                    if (districtModel.mst_district_id==commonAnswersEntity.mst_district_id){
                        binding.spDistrict.setSelection(index)
                        return@forEachIndexed
                    }
                }
                if (disableViews)
                    binding.spDistrict.isEnabled=false
            }
        }
    }
    fun getTehsil(state: Int){
        lifecycleScope.launch {
            tehsilList=AppDatabase.getInstance(requireContext()).placesDao().getAllTehsils(state).transformTehsil() as ArrayList<TehsilModel>
            tehsilList.add(0,TehsilModel("","Select Tehsil"))
            binding.spTehsil.adapter=TehsilSpinnerAdapter(requireContext(),tehsilList)
            if (status>0){
                tehsilList.forEachIndexed { index, tehsilModel ->
                    if (tehsilModel.mst_tehsil_id==commonAnswersEntity.mst_tehsil_id){
                        binding.spTehsil.setSelection(index)
                        return@forEachIndexed
                    }
                }
                if (disableViews)
                    binding.spTehsil.isEnabled=false
            }
        }
    }
    fun getPanchayath(state: Int){
        lifecycleScope.launch {
            panchayathList=AppDatabase.getInstance(requireContext()).placesDao().getAllPanchayath(state).transformPanchayath() as ArrayList<PanchayathModel>
            panchayathList.add(0, PanchayathModel("","Select Panchayath"))
            binding.spPanchayat.adapter=PanchayatSpinnerAdapter(requireContext(),panchayathList)
            if (status>0){
                panchayathList.forEachIndexed { index, panchayathModel ->
                    if (panchayathModel.mst_panchayat_id==commonAnswersEntity.mst_panchayat_id){
                        binding.spPanchayat.setSelection(index)
                        return@forEachIndexed
                    }
                }
                if (disableViews)
                    binding.spPanchayat.isEnabled=false
            }
        }
    }
    fun getVillage(state: Int){
        lifecycleScope.launch {
            villageList=AppDatabase.getInstance(requireContext()).placesDao().getAllVillages(state).transformVillage() as ArrayList<VillageModel>
            villageList.add(0,VillageModel("","Select Village"))
            binding.spVillage.adapter=VillageSpinnerAdapter(requireContext(),villageList)
            if (status>0){
                villageList.forEachIndexed { index, villageModel ->
                    if (villageModel.mst_villages_id==commonAnswersEntity.mst_village_id){
                        binding.spVillage.setSelection(index)
                        return@forEachIndexed
                    }
                    if (disableViews)
                        binding.spVillage.isEnabled=false
                }
            }
        }
    }


    fun spinnerSelectors(){
        binding.spState.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getDistrict(stateList[p2].mst_state_id.toInt())
                    commonAnswersEntity.mst_state_id=stateList[p2].mst_state_id
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.spFrequancy.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            val frequencyArray = resources.getStringArray(R.array.frequancy)
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                   // getDistrict(stateList[p2].mst_state_id.toInt())
                    commonAnswersEntity.frequency_of_bill_payment = frequencyArray.get(p2)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        binding.spDistrict.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getTehsil(districtList[p2].mst_district_id.toInt())
                    commonAnswersEntity.mst_district_id=districtList[p2].mst_district_id
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.spTehsil.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getPanchayath(tehsilList[p2].mst_tehsil_id.toInt())
                    commonAnswersEntity.mst_tehsil_id=tehsilList[p2].mst_tehsil_id
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.spPanchayat.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getVillage(panchayathList[p2].mst_panchayat_id.toInt())
                    commonAnswersEntity.mst_panchayat_id=panchayathList[p2].mst_panchayat_id
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.spVillage.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    commonAnswersEntity.mst_village_id=villageList[p2].mst_villages_id
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }
    fun List<StatesEntity>.transformState(): List<StateModel> {
        return this.map {
            it.transform1()
        }
    }
    fun StatesEntity.transform1(): StateModel {
        this.apply {
            return StateModel(
                mst_state_id.toString(), state_name
            )
        }
    }

    fun List<DistrictEntity>.transformDistrict(): List<DistrictModel> {
        return this.map {
            it.transform1()
        }
    }
    fun DistrictEntity.transform1(): DistrictModel {
        this.apply {
            return DistrictModel(
                district_name,mst_district_id.toString()
            )
        }
    }

    fun List<TehsilEntity>.transformTehsil(): List<TehsilModel> {
        return this.map {
            it.transform1()
        }
    }
    fun TehsilEntity.transform1(): TehsilModel {
        this.apply {
            return TehsilModel(
                mst_tehsil_id.toString(),tehsil_name
            )
        }
    }

    fun List<PanchayathEntity>.transformPanchayath(): List<PanchayathModel> {
        return this.map {
            it.transform()
        }
    }
    fun PanchayathEntity.transform(): PanchayathModel {
        this.apply {
            return PanchayathModel(
                mst_panchayat_id.toString(),panchayat_name
            )
        }
    }

    fun List<VillageEntity>.transformVillage(): List<VillageModel> {
        return this.map {
            it.transform()
        }
    }
    fun VillageEntity.transform(): VillageModel {
        this.apply {
            return VillageModel(
                mst_village_id.toString(),village_name
            )
        }
    }
    var imageUri1: Uri? = null
    private fun openCamera(from:Int) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri1 = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri1)
        startActivityForResult(intent, from)
    }

    val AADHAR_BACK=345;
    val AADHAR_FRONT=565;
    val BILL_IMAGE=656;
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== AppCompatActivity.RESULT_OK){
            when(requestCode){
                AADHAR_BACK->{
                    val path = FileUtils.getRealPathFromURI(requireContext(), imageUri1)
                    val bmOptions = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeFile(path, bmOptions)
                    val scaledBitmap=UtilMethods.getResizedBitmap(bitmap,1500,1500)
                    val bmTimeStamp=drawTextToBitmap(scaledBitmap,18,UtilMethods.getFormattedDate(
                        Date(),"dd-MM-yyyy HH:mm:ss"))
                    val newPath=saveImageToExternal(bmTimeStamp,requireContext())
                    lifecycleScope.launch {
                        val compressed= Compressor.compress(requireContext(), File(newPath!!.path))
                        commonAnswersEntity.back_photo_of_aadhar_card= compressed.path
                        binding.tvAadharBack.setText(commonAnswersEntity.back_photo_of_aadhar_card.substring(commonAnswersEntity.back_photo_of_aadhar_card.lastIndexOf("/")+1))
                        deleteFile(path)
                    }
                }
                AADHAR_FRONT->{
                    val path = FileUtils.getRealPathFromURI(requireContext(), imageUri1)
                    val bmOptions = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeFile(path, bmOptions)
                    val scaledBitmap=UtilMethods.getResizedBitmap(bitmap,1500,1500)
                    val bmTimeStamp=drawTextToBitmap(scaledBitmap,18,UtilMethods.getFormattedDate(
                        Date(),"dd-MM-yyyy HH:mm:ss"))
                    val newPath=saveImageToExternal(bmTimeStamp,requireContext())
                    lifecycleScope.launch {
                        val compressed= Compressor.compress(requireContext(), File(newPath!!.path))
                        commonAnswersEntity.font_photo_of_aadar_card= compressed.path
                        binding.tvAadharFront.setText(commonAnswersEntity.font_photo_of_aadar_card.substring(commonAnswersEntity.font_photo_of_aadar_card.lastIndexOf("/")+1))
                        deleteFile(path)
                    }
                }
                BILL_IMAGE->{
                    val path = FileUtils.getRealPathFromURI(requireContext(), imageUri1)
                    val bmOptions = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeFile(path, bmOptions)
                    val scaledBitmap=UtilMethods.getResizedBitmap(bitmap,1500,1500)
                    val bmTimeStamp=drawTextToBitmap(scaledBitmap,18,UtilMethods.getFormattedDate(Date(),"dd-MM-yyyy HH:mm:ss"))
                    val newPath=saveImageToExternal(bmTimeStamp,requireContext())
                    lifecycleScope.launch {
                        val compressed= Compressor.compress(requireContext(), File(newPath!!.path))
                        commonAnswersEntity.photo_of_bill= compressed.path
                        binding.tvBillPhoto.setText(commonAnswersEntity.photo_of_bill.substring(commonAnswersEntity.photo_of_bill.lastIndexOf("/")+1))
                        deleteFile(path)
                    }
                }

            }
        }
    }

    private fun drawTextToBitmap(bitmap: Bitmap, textSize: Int = 78, text: String): Bitmap {
        val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        // new antialised Paint - empty constructor does also work
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED

        // text size in pixels
        val scale = requireContext().resources.displayMetrics.density
        paint.textSize = (textSize * scale).roundToInt().toFloat()

        //custom fonts or a default font
        val fontFace = ResourcesCompat.getFont(requireContext(), R.font.opensans_bold)
        paint.typeface = Typeface.create(fontFace, Typeface.NORMAL)


        // draw text to the Canvas center
        val bounds = Rect()
        //draw the text
        paint.getTextBounds(text, 0, text.length, bounds)
        val height: Float = paint.measureText("yY")

        //x and y defines the position of the text, starting in the top left corner
        canvas.drawText(text, 20F, height+15F, paint)
        return mutableBitmap
    }

    @Throws(IOException::class)
    fun saveImageToExternal(bm: Bitmap, context: Context?): Uri? {
        // Create Path to save Image
        val directory = requireContext().filesDir.path
        val path = File(directory) // Creates app specific folder
        if (!path.exists()) {
            if (!path.mkdirs()) {
                Toast.makeText(context, "Unable to create directory", Toast.LENGTH_SHORT).show()
            }
        }
        val imageFile = File(path,  UtilMethods.getFormattedDate(Date(),"yyyyMMddHHmmssSSS") + ".jpg") // Imagename.png
        val out = FileOutputStream(imageFile)
        return try {
            // bm = Bitmap.createScaledBitmap(bm, 600, 400, false);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out) // Compress Image
            out.flush()
            out.close()
            Uri.fromFile(imageFile)
        } catch (e: Exception) {
            throw IOException()
        }
    }
    fun deleteFile(path: String){
        try {
            val file: File = File(path)
            file.delete()
            if (file.exists()) {
                file.canonicalFile.delete()
                if (file.exists()) {
                    requireContext().deleteFile(file.name)
                }
            }
        }catch (e:Exception){
            e.toString()
        }

    }
}