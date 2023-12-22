package com.renew.survey.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.renew.survey.R
import com.renew.survey.adapter.DistrictSpinnerAdapter
import com.renew.survey.adapter.PanchayatSpinnerAdapter
import com.renew.survey.adapter.StateSpinnerAdapter
import com.renew.survey.adapter.TehsilSpinnerAdapter
import com.renew.survey.adapter.VillageSpinnerAdapter
import com.renew.survey.databinding.FragmentCommonQuestionBinding
import com.renew.survey.response.DistrictModel
import com.renew.survey.response.PanchayathModel
import com.renew.survey.response.StateModel
import com.renew.survey.response.TehsilModel
import com.renew.survey.response.VillageModel
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.CommonAnswersEntity
import com.renew.survey.room.entities.DistrictEntity
import com.renew.survey.room.entities.PanchayathEntity
import com.renew.survey.room.entities.StatesEntity
import com.renew.survey.room.entities.TehsilEntity
import com.renew.survey.room.entities.VillageEntity
import kotlinx.coroutines.launch
import java.util.Locale


class CommonQuestionFragment constructor(var commonAnswersEntity: CommonAnswersEntity,val status:Int) : Fragment() {
    lateinit var binding:FragmentCommonQuestionBinding
    var stateList= arrayListOf<StateModel>();
    var districtList= arrayListOf<DistrictModel>()
    var tehsilList= arrayListOf<TehsilModel>()
    var panchayathList= arrayListOf<PanchayathModel>()
    var villageList= arrayListOf<VillageModel>()
    var disableViews=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentCommonQuestionBinding.inflate(inflater,container,false)
        Log.e("status","$status")
        if (status==2||status==3||status==5||status==6){
            disableViews=true
        }
        if (status>0){
            binding.edtBeneficiaryName.setText(commonAnswersEntity.banficary_name)
            binding.edtMobile.setText(commonAnswersEntity.mobile_number)
            binding.edtCattleOwn.setText(commonAnswersEntity.no_of_cattles_own)
            binding.edtAadhaarCard.setText(commonAnswersEntity.aadhar_card)
            binding.edtWoodUsed.setText(commonAnswersEntity.wood_use_per_day_in_kg)
            binding.edtNoCylinderYear.setText(commonAnswersEntity.no_of_cylinder_per_year)
            binding.edtAnnualFamilyIncome.setText(commonAnswersEntity.annual_family_income)
            binding.edtNoCowDungPerDay.setText(commonAnswersEntity.no_of_cow_dung_per_day)
            binding.edtFamilySize.setText(commonAnswersEntity.family_size)
            when(commonAnswersEntity.gender.uppercase(Locale.ROOT)){
                getString(R.string.male).uppercase(Locale.ROOT) ->{
                    binding.rbMale.isChecked=true
                }
                getString(R.string.female).uppercase(Locale.ROOT)->{
                    binding.rbFemale.isChecked=true
                }
                getString(R.string.other).uppercase(Locale.ROOT)->{
                    binding.rbOther.isChecked=true
                }
            }
            if (commonAnswersEntity.is_lpg_using==getString(R.string.yes).uppercase(Locale.ROOT)){
                binding.rbLpgYes.isChecked=true
            }else{
                binding.rbLpgNo.isChecked=true
            }
            if (commonAnswersEntity.is_cow_dung==getString(R.string.yes).uppercase(Locale.ROOT)){
                binding.rbCowDungYes.isChecked=true
            }else{
                binding.rbCowDungNo.isChecked=true
            }
            if (commonAnswersEntity.house_type==getString(R.string.own).uppercase(Locale.ROOT)){
                binding.rbOwn.isChecked=true
            }else{
                binding.rbRented.isChecked=true
            }
            if (commonAnswersEntity.willing_to_contribute_clean_cooking==getString(R.string.yes).uppercase(Locale.ROOT)){
                binding.rbCleanYes.isChecked=true
            }else{
                binding.rbCleanNo.isChecked=true
            }
            if (commonAnswersEntity.electricity_connection_available==getString(R.string.yes).uppercase(Locale.ROOT)){
                binding.rbElectricityYes.isChecked=true
            }else{
                binding.rbElectricityNo.isChecked=true
            }
            if (disableViews){
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
                for (i in 0 until binding.llLayout.getChildCount()) {
                    val child: View = binding.llLayout.getChildAt(i)
                    child.isEnabled = false
                }
            }
        }
        getStateData()
        spinnerSelectors()
        binding.edtBeneficiaryName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.banficary_name=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.rbMale.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.gender=compoundButton.text.toString()
            }
        }
        binding.rbFemale.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.gender=compoundButton.text.toString()
            }
        }
        binding.rbOther.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.gender=compoundButton.text.toString()
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
        binding.rbLpgYes.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.is_lpg_using=compoundButton.text.toString()
            }
        }
        binding.rbLpgNo.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.is_lpg_using=compoundButton.text.toString()
            }
        }
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
                commonAnswersEntity.is_cow_dung=compoundButton.text.toString()
            }
        }
        binding.rbCowDungNo.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.is_cow_dung=compoundButton.text.toString()
            }
        }
        binding.edtNoCowDungPerDay.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commonAnswersEntity.no_of_cow_dung_per_day=p0.toString().trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.rbOwn.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.house_type=compoundButton.text.toString()
            }
        }
        binding.rbRented.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.house_type=compoundButton.text.toString()
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
                commonAnswersEntity.willing_to_contribute_clean_cooking=compoundButton.text.toString()
            }
        }
        binding.rbCleanNo.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.willing_to_contribute_clean_cooking=compoundButton.text.toString()
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
        binding.rbElectricityYes.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.electricity_connection_available=compoundButton.text.toString()
            }
        }
        binding.rbElectricityNo.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                commonAnswersEntity.electricity_connection_available=compoundButton.text.toString()
            }
        }

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


        return binding.root
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
            villageList.add(0,VillageModel("","Select State"))
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
}