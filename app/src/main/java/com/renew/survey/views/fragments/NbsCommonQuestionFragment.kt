package com.renew.survey.views.fragments

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
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.renew.survey.R
import com.renew.survey.adapter.DistrictSpinnerAdapter
import com.renew.survey.adapter.PanchayatSpinnerAdapter
import com.renew.survey.adapter.StateSpinnerAdapter
import com.renew.survey.adapter.TehsilSpinnerAdapter
import com.renew.survey.adapter.VillageSpinnerAdapter
import com.renew.survey.databinding.FragmentCommonQuestionBinding
import com.renew.survey.databinding.NbsCommonQuestionsBinding
import com.renew.survey.helper.compressor.Compressor
import com.renew.survey.response.Data
import com.renew.survey.response.DistrictModel
import com.renew.survey.response.DistrubutionOtpValidation
import com.renew.survey.response.PanchayathModel
import com.renew.survey.response.StateModel
import com.renew.survey.response.TehsilModel
import com.renew.survey.response.VillageModel
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.CommonAnswersEntity
import com.renew.survey.room.entities.DistrictEntity
import com.renew.survey.room.entities.NbsCommonAnswersEntity
import com.renew.survey.room.entities.PanchayathEntity
import com.renew.survey.room.entities.StatesEntity
import com.renew.survey.room.entities.TehsilEntity
import com.renew.survey.room.entities.VillageEntity
import com.renew.survey.utilities.ApiInterface
import com.renew.survey.utilities.AppConstants
import com.renew.survey.utilities.DataAllowMetPerson
import com.renew.survey.utilities.FileUtils
import com.renew.survey.utilities.PreferenceManager
import com.renew.survey.utilities.UtilMethods
import com.renew.survey.views.LoginActivity
import com.renew.survey.views.MapManagerActivity
import com.renew.survey.views.ProjectActivity
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


class NbsCommonQuestionFragment constructor(var commonAnswersEntity: NbsCommonAnswersEntity, val status:Int, val listener:DataAllowMetPerson) : Fragment() {
    lateinit var binding:NbsCommonQuestionsBinding
    var stateList= arrayListOf<StateModel>()
    var districtList= arrayListOf<DistrictModel>()
    var tehsilList= arrayListOf<TehsilModel>()
    var panchayathList= arrayListOf<PanchayathModel>()
    var villageList= arrayListOf<VillageModel>()
    var disableViews=false
    lateinit var preferenceManager:PreferenceManager
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val hour = c.get(Calendar.HOUR)
    val minute = c.get(Calendar.MINUTE)
    val gson= Gson()
    var listOfFragment= arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= NbsCommonQuestionsBinding.inflate(inflater,container,false)
        preferenceManager= PreferenceManager(requireContext())
        Log.e("status","$status")
        if (status==2||status==3||status==5||status==6||status==7||status==8 || status > 8){
            disableViews=true
        }

        if (preferenceManager.getForm().tbl_forms_id == 2) {
            listOfFragment.clear()
            val retrievedList = preferenceManager.getProjOtpVerification("projectVerify")
            retrievedList?.forEach {
                listOfFragment.add(it)
            }
        }

        initilize()
        getStateData()
        spinnerSelectors()

        val calendar: Calendar = Calendar.getInstance()
        val currentDate: Date = calendar.time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val formattedDateTime: String = dateFormat.format(currentDate)
        binding.edtDateAndTime.setText(formattedDateTime)
        commonAnswersEntity.date_and_time_of_visit=UtilMethods.getFormattedDate(calendar.time,"dd-MM-yyyy HH:mm")


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
            Log.d("testLocation",preferenceManager.getLocation().toString())
            binding.edtGpsLocation.setText(preferenceManager.getLocation())
            if (preferenceManager.getLocation()==""){
                UtilMethods.showToast(requireContext(),"Location not available. Please make sure that you enabled the location and internet in your device")
            }else{
                commonAnswersEntity.gps_location = preferenceManager.getLocation().toString()
            }
        }
        commonAnswersEntity.farmer_unique_id="1234"
        binding.edtUniqueFarmerId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.farmer_unique_id=p0.toString().trim()
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


        binding.edtFarmerName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.banficary_name=p0.toString().trim()
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

        binding.edtAltMobNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.alternate_mobile_number=p0.toString().trim()
            }
            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.edtResaddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.residential_address=p0.toString().trim()
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.edtIdProof.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.aadhar_card=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        return binding.root
    }

    private fun initilize() {
        if (status>0) {
            binding.edtUniqueFarmerId.setText(commonAnswersEntity.farmer_unique_id)
            binding.edtDateAndTime.setText(commonAnswersEntity.date_and_time_of_visit)
            binding.edtGpsLocation.setText(commonAnswersEntity.gps_location)
            binding.edtFarmerName.setText(commonAnswersEntity.banficary_name)
            binding.edtMobile.setText(commonAnswersEntity.mobile_number)
            binding.edtAltMobNo.setText(commonAnswersEntity.alternate_mobile_number)
            binding.edtIdProof.setText(commonAnswersEntity.aadhar_card)
            binding.edtResaddress.setText(commonAnswersEntity.residential_address)
        }
        if (disableViews) {
            binding.edtUniqueFarmerId.isEnabled=false
            binding.edtDateAndTime.isEnabled=false
            binding.edtGpsLocation.isEnabled=false
            binding.edtFarmerName.isEnabled=false
            binding.edtMobile.isEnabled=false
            binding.edtAltMobNo.isEnabled=false
            binding.edtIdProof.isEnabled=false
            binding.edtResaddress.isEnabled=false
        }
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
                   // getDistrict(stateList[p2].mst_state_id.toInt())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== AppCompatActivity.RESULT_OK){
            when(requestCode){

            }
        }
    }
}