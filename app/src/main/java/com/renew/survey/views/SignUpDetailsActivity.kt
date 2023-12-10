package com.renew.survey.views

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.lifecycle.lifecycleScope
import com.google.gson.reflect.TypeToken
import com.renew.survey.adapter.DistrictSpinnerAdapter
import com.renew.survey.adapter.PanchayatSpinnerAdapter
import com.renew.survey.adapter.StateSpinnerAdapter
import com.renew.survey.adapter.TehsilSpinnerAdapter
import com.renew.survey.adapter.VillageSpinnerAdapter
import com.renew.survey.databinding.ActivitySignUpDetailsBinding
import com.renew.survey.response.DistrictModel
import com.renew.survey.response.PanchayathModel
import com.renew.survey.response.StateModel
import com.renew.survey.response.TehsilModel
import com.renew.survey.response.UserInfo
import com.renew.survey.response.VillageModel
import com.renew.survey.utilities.ApiInterface
import com.renew.survey.utilities.AppConstants
import com.renew.survey.utilities.UtilMethods
import kotlinx.coroutines.launch
import org.json.JSONObject

class SignUpDetailsActivity : BaseActivity() {
    lateinit var binding: ActivitySignUpDetailsBinding
    val initCalendar=Calendar.getInstance()
    var stateList= arrayListOf<StateModel>()
    var districtList= arrayListOf<DistrictModel>()
    var tehsilList= arrayListOf<TehsilModel>()
    var panchayathList= arrayListOf<PanchayathModel>()
    var villageList= arrayListOf<VillageModel>()
    var userInfo:UserInfo?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSubmit.setOnClickListener {
            formValidation()
        }
        userInfo=gson.fromJson(intent.getStringExtra("user_info"),UserInfo::class.java)
        binding.edtDob.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val calendar=Calendar.getInstance()
                calendar[Calendar.YEAR]=year
                calendar[Calendar.MONTH]=monthOfYear
                calendar[Calendar.DAY_OF_MONTH]=dayOfMonth
                binding.edtDob.setText(UtilMethods.getFormattedDate(calendar.time))

            }, initCalendar.get(Calendar.YEAR), initCalendar.get(Calendar.MONTH), initCalendar.get(Calendar.DAY_OF_MONTH))
            dpd.show()
        }
        if (userInfo!=null){
            binding.edtFullName.setText(userInfo!!.full_name)
            binding.edtUsername.setText(userInfo!!.username)
            binding.edtEmail.setText(userInfo!!.email)
            binding.edtPassword.isFocusable=false
            binding.edtPassword.setText("123456")
            binding.tilPassword.isPasswordVisibilityToggleEnabled=false
            binding.edtAddress.setText(userInfo!!.address)
            binding.edtPincode.setText(userInfo!!.pincode)
            binding.edtDob.setText(UtilMethods.getDisplayDateFormat(userInfo!!.date_of_birth))
            if (userInfo!!.gender=="MALE"){
                binding.rbMale.isChecked=true
            }else{
                binding.rbFemale.isChecked=true
            }

        }
        getState()
        spinnerSelectors()
    }
    fun spinnerSelectors(){
        binding.spState.onItemSelectedListener=object :OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getDistrict(stateList[p2].mst_state_id)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.spDistrict.onItemSelectedListener=object :OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getTehsil(districtList[p2].mst_district_id)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.spTehsil.onItemSelectedListener=object :OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getPanchayath(tehsilList[p2].mst_tehsil_id)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.spPanchayat.onItemSelectedListener=object :OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getVillage(panchayathList[p2].mst_panchayat_id)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }
    fun formValidation(){
        if (binding.edtFullName.text.toString().isEmpty()){
            UtilMethods.showToast(this,"Please enter full name")
            return
        }
        if (binding.edtUsername.text.toString().isEmpty()){
            UtilMethods.showToast(this,"Please enter user name")
            return
        }
        if (binding.edtEmail.text.toString().isEmpty()){
            UtilMethods.showToast(this,"Please enter email address")
            return
        }
        if (!isEmailValid(binding.edtEmail.text.toString())){
            UtilMethods.showToast(this,"Please enter valid email address")
            return
        }
        if (userInfo==null){
            if (binding.edtPassword.text.toString().isEmpty()){
                UtilMethods.showToast(this,"Please enter password")
                return
            }
        }
        if (binding.edtAddress.text.toString().isEmpty()){
            UtilMethods.showToast(this,"Please enter full name")
            return
        }
        if (binding.edtPincode.text.toString().isEmpty()){
            UtilMethods.showToast(this,"Please enter pincode")
            return
        }
        if (binding.edtDob.text.toString().isEmpty()){
            UtilMethods.showToast(this,"Please enter date of birth")
            return
        }
        if (!(binding.rbMale.isChecked || binding.rbFemale.isChecked)){
            UtilMethods.showToast(this,"Please select gender")
            return
        }
        if (binding.spState.selectedItemPosition==0){
            UtilMethods.showToast(this,"Please select state")
            return
        }
        if (binding.spDistrict.selectedItemPosition==0){
            UtilMethods.showToast(this,"Please select district")
            return
        }
        if (binding.spTehsil.selectedItemPosition==0){
            UtilMethods.showToast(this,"Please select tehsil")
            return
        }
        if (binding.spPanchayat.selectedItemPosition==0){
            UtilMethods.showToast(this,"Please select panchayath")
            return
        }
        if (binding.spVillage.selectedItemPosition==0){
            UtilMethods.showToast(this,"Please select village")
            return
        }
        signUpApi()
    }
    fun signUpApi(){
        binding.progressLayout.visibility=View.VISIBLE
        lifecycleScope.launch {
            ApiInterface.getInstance()?.apply {
                val fcmToken=preferenceManager.getFCMToken()
                var gender="MALE"
                if (binding.rbMale.isChecked){
                    gender="MALE"
                }else if (binding.rbFemale.isChecked){
                    gender="FEMALE"
                }
                var user_type="USER"
                var password:String?=null
                if (userInfo==null){
                    password=binding.edtPassword.text.toString()
                }
                Log.e("paramsss","premsssss  ${intent.getStringExtra("project_id")}   ${intent.getStringExtra("project_id")}  ")
                val response=register(
                    intent.getStringExtra("project_id")!!,
                    intent.getStringExtra("project")!!,
                    intent.getStringExtra("aadhar")!!,
                    intent.getStringExtra("mobile")!!,
                    password,
                    binding.edtFullName.text.toString(),
                    binding.edtUsername.text.toString(),
                    binding.edtAddress.text.toString(),
                    stateList[binding.spState.selectedItemPosition].mst_state_id,
                    villageList[binding.spVillage.selectedItemPosition].mst_villages_id,
                    districtList[binding.spDistrict.selectedItemPosition].mst_district_id,
                    tehsilList[binding.spTehsil.selectedItemPosition].mst_tehsil_id,
                    panchayathList[binding.spPanchayat.selectedItemPosition].mst_panchayat_id,
                    binding.edtPincode.text.toString(),
                    binding.edtEmail.text.toString(),
                    AppConstants.AppKey,AppConstants.DeviceType,
                    Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID),fcmToken!!,gender,
                    binding.edtDob.text.toString(),
                    intent.getStringExtra("coordinator_id")!!,
                    intent.getStringExtra("user_type")!!,
                )
                binding.progressLayout.visibility=View.GONE
                if(response.isSuccessful){
                    val jsonObject=JSONObject(response.body().toString())
                    Log.e("response",jsonObject.toString())
                    UtilMethods.showToast(this@SignUpDetailsActivity,jsonObject.getString("message"))
                    if (jsonObject.getString("success")=="1"){
                        Intent(this@SignUpDetailsActivity,ApprovalPendingActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(this)
                        }
                    }
                }

            }
        }
    }
    fun getState(){
        binding.progressLayout.visibility= View.VISIBLE
        ApiInterface.getInstance()?.apply {
            lifecycleScope.launch {
                val response=getStates(AppConstants.AppKey)
                binding.progressLayout.visibility= View.GONE
                if (response.isSuccessful){
                    val jsonObject=JSONObject(response.body().toString())
                    if (jsonObject.getString("success")=="0"){
                        val itemType = object : TypeToken<List<StateModel>>() {}.type
                        val itemList = gson.fromJson<ArrayList<StateModel>>(jsonObject.getJSONArray("data").toString(), itemType)
                        itemList.add(0,StateModel("0","Select State"))
                        stateList=itemList
                        binding.spState.adapter=StateSpinnerAdapter(this@SignUpDetailsActivity,stateList)
                        if (userInfo!=null){
                            var pos=0
                            stateList.forEachIndexed { index, stateModel ->
                                if (stateModel.mst_state_id== userInfo!!.mst_state_id){
                                    pos=index
                                   return@forEachIndexed
                                }
                            }
                            binding.spState.setSelection(pos)
                        }
                    }
                }
            }
        }
    }
    fun getDistrict(id:String){
        binding.progressLayout.visibility= View.VISIBLE
        ApiInterface.getInstance()?.apply {
            lifecycleScope.launch {
                val response=getDistricts(id,AppConstants.AppKey)
                binding.progressLayout.visibility= View.GONE
                if (response.isSuccessful){
                    val jsonObject=JSONObject(response.body().toString())
                    if (jsonObject.getString("success")=="0"){
                        val itemType = object : TypeToken<List<DistrictModel>>() {}.type
                        val itemList = gson.fromJson<ArrayList<DistrictModel>>(jsonObject.getJSONArray("data").toString(), itemType)
                        itemList.add(0,DistrictModel("Select District",""))
                        districtList=itemList
                        binding.spDistrict.adapter=DistrictSpinnerAdapter(this@SignUpDetailsActivity,districtList)
                        if (userInfo!=null){
                            var pos=0
                            districtList.forEachIndexed { index, stateModel ->
                                if (stateModel.mst_district_id== userInfo!!.mst_district_id){
                                    pos=index
                                    return@forEachIndexed
                                }
                            }
                            binding.spDistrict.setSelection(pos)
                        }
                    }
                }
            }
        }
    }
    fun getTehsil(id:String){
        binding.progressLayout.visibility= View.VISIBLE
        ApiInterface.getInstance()?.apply {
            lifecycleScope.launch {
                val response=getTehsil(id,AppConstants.AppKey)
                binding.progressLayout.visibility= View.GONE
                if (response.isSuccessful){
                    val jsonObject=JSONObject(response.body().toString())
                    if (jsonObject.getString("success")=="0"){
                        val itemType = object : TypeToken<List<TehsilModel>>() {}.type
                        val itemList = gson.fromJson<ArrayList<TehsilModel>>(jsonObject.getJSONArray("data").toString(), itemType)
                        itemList.add(0,TehsilModel("0","Select Tehsil"))
                        tehsilList=itemList
                        binding.spTehsil.adapter=TehsilSpinnerAdapter(this@SignUpDetailsActivity,tehsilList)
                        if (userInfo!=null){
                            var pos=0
                            tehsilList.forEachIndexed { index, stateModel ->
                                if (stateModel.mst_tehsil_id== userInfo!!.mst_tehsil_id){
                                    pos=index
                                    return@forEachIndexed
                                }
                            }
                            binding.spTehsil.setSelection(pos)
                        }
                    }
                }
            }
        }
    }
    fun getPanchayath(id:String){
        binding.progressLayout.visibility= View.VISIBLE
        ApiInterface.getInstance()?.apply {
            lifecycleScope.launch {
                val response=getPanchayat(id,AppConstants.AppKey)
                binding.progressLayout.visibility= View.GONE
                if (response.isSuccessful){
                    val jsonObject=JSONObject(response.body().toString())
                    if (jsonObject.getString("success")=="0"){
                        val itemType = object : TypeToken<List<PanchayathModel>>() {}.type
                        val itemList = gson.fromJson<ArrayList<PanchayathModel>>(jsonObject.getJSONArray("data").toString(), itemType)
                        itemList.add(0,PanchayathModel("0","Select Panchayath"))
                        panchayathList=itemList
                        binding.spPanchayat.adapter=PanchayatSpinnerAdapter(this@SignUpDetailsActivity,panchayathList)
                        if (userInfo!=null){
                            var pos=0
                            panchayathList.forEachIndexed { index, stateModel ->
                                if (stateModel.mst_panchayat_id== userInfo!!.mst_panchayat_id){
                                    pos=index
                                    return@forEachIndexed
                                }
                            }
                            binding.spPanchayat.setSelection(pos)
                        }
                    }
                }
            }
        }
    }
    fun getVillage(id:String){
        binding.progressLayout.visibility= View.VISIBLE
        ApiInterface.getInstance()?.apply {
            lifecycleScope.launch {
                val response=getVillage(id,AppConstants.AppKey)
                binding.progressLayout.visibility= View.GONE
                if (response.isSuccessful){
                    val jsonObject=JSONObject(response.body().toString())
                    if (jsonObject.getString("success")=="0"){
                        val itemType = object : TypeToken<List<VillageModel>>() {}.type
                        val itemList = gson.fromJson<ArrayList<VillageModel>>(jsonObject.getJSONArray("data").toString(), itemType)
                        itemList.add(0,VillageModel("0","Select Village"))
                        villageList=itemList
                        binding.spVillage.adapter=VillageSpinnerAdapter(this@SignUpDetailsActivity,villageList)
                        if (userInfo!=null){
                            var pos=0
                            villageList.forEachIndexed { index, stateModel ->
                                if (stateModel.mst_villages_id== userInfo!!.mst_village_id){
                                    pos=index
                                    return@forEachIndexed
                                }
                            }
                            binding.spVillage.setSelection(pos)
                        }
                    }
                }
            }
        }
    }
    fun isEmailValid(email:String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}