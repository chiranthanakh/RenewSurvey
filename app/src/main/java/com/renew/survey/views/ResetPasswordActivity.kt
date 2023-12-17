package com.renew.survey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.renew.survey.R
import com.renew.survey.databinding.ActivityResetPasswordBinding
import com.renew.survey.response.ValidationModel
import com.renew.survey.utilities.ApiInterface
import com.renew.survey.utilities.AppConstants
import com.renew.survey.utilities.UtilMethods
import kotlinx.coroutines.launch
import org.json.JSONObject

class ResetPasswordActivity : BaseActivity() {
    lateinit var binding:ActivityResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnContinue.setOnClickListener {
            resetOtp()
        }
    }

    private fun resetOtp(){
        binding.progressLayout.visibility= View.VISIBLE
        lifecycleScope.launch {
            ApiInterface.getInstance()?.apply {
                val response=resetPassword(
                    intent.getStringExtra("mobile")!!,
                    "8",
                    binding.edtNewPassword.text.toString(),
                    binding.edtPassword.text.toString(),
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