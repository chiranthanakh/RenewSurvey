package com.renew.survey.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.renew.survey.databinding.ActivitySignUpBinding
import com.renew.survey.response.LoginResponse
import com.renew.survey.response.UserData
import com.renew.survey.response.ValidationModel
import com.renew.survey.utilities.ApiInterface
import com.renew.survey.utilities.AppConstants.AppKey
import com.renew.survey.utilities.UtilMethods
import com.squareup.picasso.BuildConfig
import kotlinx.coroutines.launch
import org.json.JSONObject

class SignUpActivity : BaseActivity() {
    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.getBooleanExtra("user",false)){
            binding.layoutUniqueCode.visibility= View.GONE
            binding.llAadharcard.visibility= View.GONE
        }else{
            binding.llSerialnumber.visibility= View.GONE
            binding.layoutUniqueCode.visibility= View.VISIBLE
        }

        binding.tvSubmitDetails.setOnClickListener{
            formValidation()
        }
    }
    fun formValidation(){
        if(!intent.getBooleanExtra("user",false)){
            if (binding.edtUniqueCode.text.toString().isEmpty()){
                UtilMethods.showToast(this,"Please enter project code")
                return
            }

            if (binding.edtAadhaarCard.text.toString().isEmpty()){
                UtilMethods.showToast(this,"Please enter aadhar number")
                return
            }
            if (binding.edtAadhaarCard.text.toString().length!=12){
                UtilMethods.showToast(this,"Please enter valid aadhar number")
                return
            }

        } else {
            if (binding.edtSerialNumber.text.toString().isEmpty()){
                UtilMethods.showToast(this,"Please enter Serial Number")
                return
            }
        }
        if (binding.edtMobile.text.toString().isEmpty()){
            UtilMethods.showToast(this,"Please enter mobile number")
            return
        }

        if (UtilMethods.isNetworkAvailable(this)){
            validationAPI()
        }else{
            UtilMethods.showToast(this,"Please check your internet connection")
        }
    }
    private fun validationAPI(){
        binding.progressLayout.visibility=View.VISIBLE
        lifecycleScope.launch {
            ApiInterface.getInstance()?.apply {
                var user_type=""
                if(intent.getBooleanExtra("user",false)){
                    user_type="USER"
                } else {
                    user_type="MEMBER"
                }
                val response=validateProject(
                    binding.edtMobile.text.toString(),
                    binding.edtUniqueCode.text.toString(),
                    binding.edtAadhaarCard.text.toString(),
                    binding.edtSerialNumber.text.toString(),
                    AppKey,
                    user_type
                )
                binding.progressLayout.visibility=View.GONE
                if (response.isSuccessful){
                    val jsonObject=JSONObject(response.body().toString())
                        if (jsonObject.getString("success")=="1"){

                            val data=gson.fromJson(jsonObject.getString("data").toString(),ValidationModel::class.java)
                            Log.e("response",data.toString())
                            Toast.makeText(this@SignUpActivity,data.otp,Toast.LENGTH_LONG).show()
                            if (user_type == "USER") {
                                data.user_info.access_token.let {
                                    preferenceManager.saveToken(data.user_info.access_token)
                                }
                                data.user_info.tbl_users_id.let { preferenceManager.saveUserId(data.user_info.tbl_users_id) }

                                var data3 = UserData(data.user_info.aadhar_card,data.user_info.access_token,data.user_info.address,
                                    data.user_info.alt_moile,data.user_info.email,data.user_info.full_name,"",data.user_info.mobile,"",data.user_info.pincode,
                                    data.user_info.profile_photo,false,data.user_info.tbl_users_id,data.user_type,data.user_info.username)
                                preferenceManager.saveUserData(data3)
                            }

                            Intent(this@SignUpActivity, VerifyOTPActivity::class.java).apply {
                                putExtra("mobile",binding.edtMobile.text.toString())
                                putExtra("aadhar",binding.edtAadhaarCard.text.toString())
                                putExtra("serial_number",binding.edtAadhaarCard.text.toString())
                                putExtra("project",data.project_info.project_code)
                                putExtra("state_name",data.project_info.state_name)
                                putExtra("project_name",data.project_info.title)
                                putExtra("project_code",binding.edtUniqueCode.text.toString())
                                putExtra("project_id",data.project_info.tbl_projects_id)
                                putExtra("coordinator_id",data.project_info.co_ordinator_id)
                                putExtra("user_type",user_type)
                                putExtra("otp",data.otp)
                                putExtra("user_info",gson.toJson(data.user_info))
                                startActivity(this)
                            }
                        }else{
                            UtilMethods.showToast(this@SignUpActivity,jsonObject.getString("message"))
                        }
                }
            }
        }
    }
}