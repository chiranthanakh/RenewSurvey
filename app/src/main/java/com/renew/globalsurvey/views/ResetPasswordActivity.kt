package com.renew.globalsurvey.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.renew.globalsurvey.databinding.ActivityResetPasswordBinding
import com.renew.globalsurvey.utilities.ApiInterface
import com.renew.globalsurvey.utilities.UtilMethods
import kotlinx.coroutines.launch
import org.json.JSONObject

class ResetPasswordActivity : BaseActivity() {
    lateinit var binding:ActivityResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnContinue.setOnClickListener {
            if (binding.edtPassword.text!!.length<6){
                UtilMethods.showToast(this,"Password should atlease 6 digits")
                return@setOnClickListener
            }
            if (binding.edtNewPassword.text!!.length<6){
                UtilMethods.showToast(this,"Password should atlease 6 digits")
                return@setOnClickListener
            }
            if (binding.edtNewPassword.text!!.length!=binding.edtPassword.text!!.length){
                UtilMethods.showToast(this,"Password does not match")
                return@setOnClickListener
            }
            resetOtp()
        }
    }

    private fun resetOtp(){
        binding.progressLayout.visibility= View.VISIBLE
        lifecycleScope.launch {
            ApiInterface.getInstance()?.apply {
                val response=resetPassword(
                    intent.getStringExtra("mobile")!!,
                    intent.getStringExtra("project_id")!!,
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
                        Toast.makeText(this@ResetPasswordActivity,jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }else{
                        UtilMethods.showToast(this@ResetPasswordActivity,jsonObject.getString("message"))
                    }
                }
            }
        }
    }

}