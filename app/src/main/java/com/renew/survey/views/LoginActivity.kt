package com.renew.survey.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.renew.survey.response.LoginResponse
import com.renew.survey.databinding.ActivityLoginBinding
import com.renew.survey.databinding.DialogRegisterAsUserTeamBinding
import com.renew.survey.response.UserData
import com.renew.survey.utilities.ApiInterface
import com.renew.survey.utilities.AppConstants
import com.renew.survey.utilities.UtilMethods
import com.squareup.picasso.BuildConfig
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvForgotPassword.setOnClickListener {
            Intent(this,ForgotPasswordActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.tvSignup.setOnClickListener {
            openRegisterAsDialog()
        }
        binding.btnContinue.setOnClickListener {
            if (binding.etUserName.text.toString() != "") {
                if (binding.passwordEditText.text.toString() != "") {
                    if (UtilMethods.isNetworkAvailable(this)){
                        callLoginApi()
                    }else{
                        UtilMethods.showToast(this,"Please check your internet connection")
                    }
                   // Intent(this@LoginActivity,TrainingActivity::class.java).apply {
                    //    startActivity(this)}
                } else {
                    Toast.makeText(this@LoginActivity, "Enter your password", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(this@LoginActivity, "Enter your mobile number", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun callLoginApi() {
        binding.progressLayout.visibility= View.VISIBLE
        lifecycleScope.launch {
            ApiInterface.getInstance()?.apply {
                val response=login(binding.etUserName.text.toString().trim(),binding.passwordEditText.text.toString(),AppConstants.AppKey)
                binding.progressLayout.visibility= View.GONE
                if (response.isSuccessful){
                    val jsonObject=JSONObject(response.body().toString())
                    Log.e("response",jsonObject.toString())
                    if (jsonObject.getString("success")=="1"){
                        val data=gson.fromJson(jsonObject.toString(), LoginResponse::class.java)
                        if (data!=null){
                            data.data?.access_token?.let {
                                preferenceManager.saveToken(it)
                            }
                            data.data?.tbl_users_id?.let {
                                preferenceManager.saveUserId(it)
                            }
                            preferenceManager.saveUserData(data.data!!)
                            if (BuildConfig.DEBUG){
                                UtilMethods.showToast(this@LoginActivity,"userid ${data.data!!.tbl_users_id}")
                            }
                            Intent(this@LoginActivity,SyncDataActivity::class.java).apply {
                                startActivity(this)
                                finish()
                            }
                        }
                    }else{
                        Toast.makeText(this@LoginActivity,jsonObject.getString("message"),Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    fun openRegisterAsDialog(){
        val dialogBinding=DialogRegisterAsUserTeamBinding.inflate(layoutInflater)
        val dialog=BottomSheetDialog(this).apply {
            setContentView(dialogBinding.root)
            show()
        }
        dialogBinding.btnUser.setOnClickListener {
            dialog.cancel()
            Intent(this,SignUpActivity::class.java).apply {
                putExtra("user",true)
                startActivity(this)
            }
        }
        dialogBinding.btnMember.setOnClickListener {
            dialog.cancel()
            Intent(this,SignUpActivity::class.java).apply {
                startActivity(this)
            }
        }

    }

}