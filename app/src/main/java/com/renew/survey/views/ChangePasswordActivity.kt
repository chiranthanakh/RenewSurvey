package com.renew.survey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.renew.survey.R
import com.renew.survey.databinding.ActivityChangePasswordBinding
import com.renew.survey.databinding.ActivityResetPasswordBinding
import com.renew.survey.utilities.ApiInterface
import com.renew.survey.utilities.UtilMethods
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChangePasswordActivity : BaseActivity() {
    lateinit var binding:ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnContinue.setOnClickListener {
            if (binding.edtOldPassword.text!!.length<6){
                UtilMethods.showToast(this,"Password should atlease 6 digits")
                return@setOnClickListener
            }
            if (binding.edtPassword.text!!.length<6){
                UtilMethods.showToast(this,"Password should atlease 6 digits")
                return@setOnClickListener
            }
            if (binding.edtNewPassword.text!!.length<6){
                UtilMethods.showToast(this,"Password should atlease 6 digits")
                return@setOnClickListener
            }
            if (binding.edtNewPassword.text.toString()!! != binding.edtPassword.text.toString()!!){
                UtilMethods.showToast(this,"Password does not match")
                return@setOnClickListener
            }
            resetOtp()
        }

    }
    private fun resetOtp(){
        binding.progressLayout.visibility= View.VISIBLE
        lifecycleScope.launch {
            Log.e("params","${preferenceManager.getUserdata().mobile}  ${preferenceManager.getUserId().toString()} ${binding.edtOldPassword.text.toString()}" )
            ApiInterface.getInstance()?.apply {
                val response=changePassword(
                    preferenceManager.getUserdata().mobile,
                    preferenceManager.getUserId().toString(),
                    binding.edtOldPassword.text.toString(),
                    binding.edtNewPassword.text.toString(),
                    binding.edtPassword.text.toString(),
                    preferenceManager.getToken()!!
                )
                binding.progressLayout.visibility= View.GONE
                if (response.isSuccessful){
                    val jsonObject= JSONObject(response.body().toString())
                    binding.edtNewPassword.text?.clear()
                    binding.edtPassword.text?.clear()
                    if (jsonObject.getString("success")=="1"){
                        Log.e("response",jsonObject.getString("message"))
                        Toast.makeText(this@ChangePasswordActivity,jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                        finish()
                    }else{
                            val data=jsonObject.getJSONObject("data")
                            if (data.getBoolean("is_access_disable")){
                                preferenceManager.clear()
                                Intent(this@ChangePasswordActivity,LoginActivity::class.java).apply {
                                    finishAffinity()
                                    startActivity(this)
                                }
                            }
                        UtilMethods.showToast(this@ChangePasswordActivity,jsonObject.getString("message"))
                    }
                }
            }
        }
    }
}