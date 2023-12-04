package com.renew.survey.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.renew.survey.response.LoginResponse
import com.renew.survey.databinding.ActivityLoginBinding
import com.renew.survey.databinding.DialogRegisterAsUserTeamBinding
import com.renew.survey.utilitys.ApiInterface
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
        if (BuildConfig.DEBUG){
            binding.etUserName.setText("9924618599")
            binding.etPassword.setText("123456")
        }

        val sharedPreferences2: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        val  rollid = sharedPreferences2.getString("rollid", "")!!

        if(rollid != ""){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnContinue.setOnClickListener {
            if (binding.etUserName.text.toString() != "") {
                if (binding.etPassword.text.toString() != "") {
                    callLoginapi()
                } else {
                    Toast.makeText(this@LoginActivity, "Enter your password", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(this@LoginActivity, "Enter your email-id", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun callLoginapi() {
        binding.progressLayout.visibility= View.VISIBLE
        lifecycleScope.launch {
            ApiInterface.getInstance()?.apply {
                val response=login(binding.etUserName.text.toString().trim(),binding.etPassword.text.toString(),"12345")
                binding.progressLayout.visibility= View.GONE
                if (response.isSuccessful){
                    val jsonObject=JSONObject(response.body().toString())
                    if (jsonObject.getString("success")=="1"){
                        val data=gson.fromJson(jsonObject.toString(), LoginResponse::class.java)
                        if (data!=null){
                            data.data?.accessToken?.let {
                                preferenceManager.saveToken(it)
                            }
                            data.data?.userId?.let {
                                preferenceManager.saveUserId(it)
                            }
                            Intent(this@LoginActivity,LanguageActivity::class.java).apply {
                                startActivity(this)
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