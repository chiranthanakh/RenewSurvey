package com.renew.survey.views

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import com.google.gson.reflect.TypeToken
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.renew.survey.R
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
import com.renew.survey.utilities.FileUtils
import com.renew.survey.utilities.UtilMethods
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

class SignUpDetailsActivity : BaseActivity() {
    lateinit var binding: ActivitySignUpDetailsBinding
    val initCalendar=Calendar.getInstance()
    var stateList= arrayListOf<StateModel>()
    var districtList= arrayListOf<DistrictModel>()
    var tehsilList= arrayListOf<TehsilModel>()
    var panchayathList= arrayListOf<PanchayathModel>()
    var villageList= arrayListOf<VillageModel>()
    var userInfo:UserInfo?=null
    var imageUri: Uri? = null
    val PICKFILE_REQUEST_CODE=43
    val FROM_CAMERA=49
    var imageUri1: Uri? = null
    var filePath = ""



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
        binding.state.text= intent.getStringExtra("state_name")!!
        binding.tvRegistration.text= "Registration (${intent.getStringExtra("project")!!})"
        binding.projectName.text= "${intent.getStringExtra("project_name")!!}"
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

        binding.ivProfileImage.setOnClickListener {
            openCamera()
        }
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
        if (filePath==""){
            UtilMethods.showToast(this,"Please select profile image")
            return
        }
        if (!binding.cbTerms.isChecked){
            UtilMethods.showToast(this,"Please accept to terms and conditions")
            return
        }
        signUpApi()
    }
    fun signUpApi() {
        binding.progressLayout.visibility = View.VISIBLE
        lifecycleScope.launch {
            ApiInterface.getInstance()?.apply {
                val fcmToken = preferenceManager.getFCMToken()
                var gender = "MALE"
                if (binding.rbMale.isChecked) {
                    gender = "MALE"
                } else if (binding.rbFemale.isChecked) {
                    gender = "FEMALE"
                }
                var user_type = "USER"
                var password: String? = null
                if (userInfo == null) {
                    password = binding.edtPassword.text.toString()
                }

                    val file = File(FileUtils.getPathFromUri(context, imageUri1))
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val profilePhotoPart = MultipartBody.Part.createFormData("profile_photo", file.name, requestFile)
                    Log.e(
                        "paramsss",
                        "premsssss  ${intent.getStringExtra("project_id")}   ${
                            intent.getStringExtra("project")
                        }  "
                    )
                    val response = register(
                        RequestBody.create(MultipartBody.FORM, intent.getStringExtra("project_id")!!),
                        RequestBody.create(MultipartBody.FORM,intent.getStringExtra("project")!!),
                        RequestBody.create(MultipartBody.FORM, intent.getStringExtra("aadhar")!!),
                        RequestBody.create(MultipartBody.FORM, intent.getStringExtra("mobile")!!),
                        RequestBody.create(MultipartBody.FORM, password!!),
                        RequestBody.create(MultipartBody.FORM, binding.edtFullName.text.toString()),
                        RequestBody.create(MultipartBody.FORM, binding.edtUsername.text.toString()),
                        RequestBody.create(MultipartBody.FORM, binding.edtAddress.text.toString()),
                        RequestBody.create(MultipartBody.FORM, stateList[binding.spState.selectedItemPosition].mst_state_id),
                        RequestBody.create(MultipartBody.FORM, villageList[binding.spVillage.selectedItemPosition].mst_villages_id),
                        RequestBody.create(MultipartBody.FORM, districtList[binding.spDistrict.selectedItemPosition].mst_district_id),
                        RequestBody.create(MultipartBody.FORM, tehsilList[binding.spTehsil.selectedItemPosition].mst_tehsil_id,),
                        RequestBody.create(MultipartBody.FORM, panchayathList[binding.spPanchayat.selectedItemPosition].mst_panchayat_id,),
                        RequestBody.create(MultipartBody.FORM, binding.edtPincode.text.toString(),),
                        RequestBody.create(MultipartBody.FORM,  binding.edtEmail.text.toString(),),
                        RequestBody.create(MultipartBody.FORM, AppConstants.AppKey,),
                        RequestBody.create(MultipartBody.FORM, AppConstants.DeviceType,),
                        RequestBody.create(MultipartBody.FORM, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID),),
                        RequestBody.create(MultipartBody.FORM, fcmToken!!),
                        RequestBody.create(MultipartBody.FORM, gender),
                        RequestBody.create(MultipartBody.FORM, binding.edtDob.text.toString()),
                        RequestBody.create(MultipartBody.FORM, intent.getStringExtra("coordinator_id")!!,),
                        RequestBody.create(MultipartBody.FORM, intent.getStringExtra("user_type")!!,),
                        profilePhotoPart,
                    )

                    binding.progressLayout.visibility = View.GONE
                    if (response.isSuccessful) {
                        val jsonObject = JSONObject(response.body().toString())
                        Log.e("response", jsonObject.toString())
                        UtilMethods.showToast(
                            this@SignUpDetailsActivity,
                            jsonObject.getString("message")
                        )
                        if (jsonObject.getString("success") == "1") {
                            Intent(
                                this@SignUpDetailsActivity,
                                ApprovalPendingActivity::class.java
                            ).apply {
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

    fun showImagePickerDialog(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        val dialogView: View = inflater.inflate(R.layout.option_dialog, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        dialogView.findViewById<View>(R.id.btnGallery).setOnClickListener {
            openGallery()
            alertDialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnCamera).setOnClickListener {
            openCamera()
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FROM_CAMERA -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    filePath = FileUtils.getRealPathFromURI(
                        this,
                        imageUri1
                    )
                    binding.ivProfileImage.setImageURI(imageUri1)
                }
            }

            PICKFILE_REQUEST_CODE -> {
                if (data != null) {
                    val uri = data.data
                    imageUri1 = data.data
                    filePath = FileUtils.getPathFromUri(this, uri)
                    binding.ivProfileImage.setImageURI(uri)
                }
            }
        }
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICKFILE_REQUEST_CODE)
    }

    fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")

        imageUri1 = getContentResolver().insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri1)
        startActivityForResult(intent, FROM_CAMERA)
    }
}