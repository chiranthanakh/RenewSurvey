package com.proteam.renewsurvey.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.renewsurvey.databinding.ActivityLoginBinding
import com.proteam.renewsurvey.MainActivity
import com.proteam.renewsurvey.utilitys.ApiInterface
import com.proteam.renewsurvey.utilitys.OnResponseListener
import com.proteam.renewsurvey.utilitys.WebServices
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity(), OnResponseListener<Any?> {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        lifecycleScope.launch {
            val response=ApiInterface.retrofitService.getAllMovies()
            if (response.isSuccessful){
                if (response.body()?.success=="1"){
                    if (response.body()?.data!=null){
                        response.body()?.data?.accessToken?.let {
                            preferenceManager.saveToken(it)
                        }
                        response.body()?.data?.userId?.let {
                            preferenceManager.saveUserId(it)
                        }
                    }
                }
            }
        }

    }

    override fun onResponse(
        response: Any?,
        URL: WebServices.ApiType?,
        isSucces: Boolean,
        code: Int
    ) {
        when (URL) {
            /*WebServices.ApiType.login -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces && response != null) {
                    val loginResponse = response as LoginResponse
                    if (loginResponse?.status == "200") {
                        val prefs = getSharedPreferences("myPref", MODE_PRIVATE)
                        val editor = prefs.edit()
                        editor.putBoolean("login", true)
                        editor.putString("rollid",loginResponse.role_id)
                        editor.putString("userid",loginResponse.user_id)
                        editor.commit()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Check your login credentials ", Toast.LENGTH_SHORT)
                        .show()
                }
            }*/

            else -> {}
        }
    }

}