package com.renew.globalsurvey.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.renew.globalsurvey.databinding.ActivityForgotPasswordBinding
import com.renew.globalsurvey.response.ValidateUserModel
import com.renew.globalsurvey.utilities.ApiInterface
import com.renew.globalsurvey.utilities.AppConstants
import com.renew.globalsurvey.utilities.UtilMethods
import kotlinx.coroutines.launch
import org.json.JSONObject

class ForgotPasswordActivity : BaseActivity() {
    lateinit var binding:ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvSubmitDetails.setOnClickListener {
            if (binding.edtMobile.text?.length != 10) {
                Toast.makeText(this@ForgotPasswordActivity,"please enter valid mobile number", Toast.LENGTH_LONG).show()
            } else {
                validationAPI()
            }
        }
    }

    private fun validationAPI(){
        binding.progressLayout.visibility= View.VISIBLE
        lifecycleScope.launch {
            ApiInterface.getInstance()?.apply {
                val response=validateUser(
                    binding.edtMobile.text.toString(),
                    AppConstants.AppKey
                )
                binding.progressLayout.visibility= View.GONE
                if (response.isSuccessful){
                    val jsonObject= JSONObject(response.body().toString())
                    if (jsonObject.getString("success")=="1"){
                        val data=gson.fromJson(jsonObject.getString("data").toString(),
                            ValidateUserModel::class.java)
                        Log.e("response",data.toString())
                        Toast.makeText(this@ForgotPasswordActivity,data.otp, Toast.LENGTH_LONG).show()
                        Intent(this@ForgotPasswordActivity, VerifyOTPActivity::class.java).apply {
                            putExtra("forgotPassword",true)
                            putExtra("mobile",binding.edtMobile.text.toString())
                            putExtra("project_id",data.tbl_users_id)
                            putExtra("otp",data.otp)
                            preferenceManager.saveToken(data.access_token)
                            startActivity(this)
                        }
                    }else{
                        UtilMethods.showToast(this@ForgotPasswordActivity,jsonObject.getString("message"))
                    }
                }
            }
        }
    }

}