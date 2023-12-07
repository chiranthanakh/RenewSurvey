package com.renew.survey.views

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import com.renew.survey.databinding.ActivityVerifyOtpactivityBinding
import com.renew.survey.utilities.UtilMethods
import com.squareup.picasso.BuildConfig

class VerifyOTPActivity : BaseActivity() {
    lateinit var binding: ActivityVerifyOtpactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVerifyOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOTPPins()
        if (BuildConfig.DEBUG){
            binding.edtAllOtp.setText(intent.getStringExtra("otp"))
        }
        binding.btnVerify.setOnClickListener {
            if (intent.getStringExtra("otp").equals(binding.edtAllOtp.text.toString())){
                Intent(this,SignUpDetailsActivity::class.java).apply {
                    putExtra("mobile",intent.getStringExtra("mobile"))
                    putExtra("aadhar",intent.getStringExtra("aadhar"))
                    putExtra("project",intent.getStringExtra("project"))
                    putExtra("project_id",intent.getStringExtra("project_id"))
                    putExtra("coordinator_id",intent.getStringExtra("coordinator_id"))
                    putExtra("user_type",intent.getStringExtra("user_type"))
                    startActivity(this)
                }
            }else{
                UtilMethods.showToast(this,"Please enter correct OTP")
            }

        }
    }

    fun setOTPPins(){
        binding.edtOtpOne.setOnClickListener(View.OnClickListener {
            binding.edtAllOtp.requestFocus()
            showSoftKeyboard(binding.edtAllOtp)
            binding.edtAllOtp.setSelection(binding.edtAllOtp.getText().toString().length)
        })
        binding.edtOtpTwo.setOnClickListener(View.OnClickListener {
            binding.edtAllOtp.requestFocus()
            showSoftKeyboard(binding.edtAllOtp)
            binding.edtAllOtp.setSelection(binding.edtAllOtp.getText().toString().length)
        })
        binding.edtOtpThree.setOnClickListener(View.OnClickListener {
            binding.edtAllOtp.requestFocus()
            showSoftKeyboard(binding.edtAllOtp)
            binding.edtAllOtp.setSelection(binding.edtAllOtp.getText().toString().length)
        })
        binding.edtOtpFour.setOnClickListener(View.OnClickListener {
            binding.edtAllOtp.requestFocus()
            showSoftKeyboard(binding.edtAllOtp)
            binding.edtAllOtp.setSelection(binding.edtAllOtp.getText().toString().length)
        })
       /* binding.edtOtpFive.setOnClickListener(View.OnClickListener {
            binding.edtAllOtp.requestFocus()
            showSoftKeyboard(binding.edtAllOtp)
            binding.edtAllOtp.setSelection(binding.edtAllOtp.getText().toString().length)
        })
        binding.edtOtpSix.setOnClickListener(View.OnClickListener {
            binding.edtAllOtp.requestFocus()
            showSoftKeyboard(binding.edtAllOtp)
            binding.edtAllOtp.setSelection(binding.edtAllOtp.getText().toString().length)
        })*/
        binding.edtAllOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvErrorText.setText("")
                if (s.length == 0) {
                    binding.edtOtpOne.text = ""
                    binding.edtOtpTwo.text = ""
                    binding.edtOtpThree.text = ""
                    binding.edtOtpFour.text = ""
                   /* binding.edtOtpFive.text = ""
                    binding.edtOtpSix.text = ""*/
                }
                if (s.length == 1) {
                    binding.edtOtpOne.text = s.toString()[0].toString()
                    binding.edtOtpTwo.text = ""
                    binding.edtOtpThree.text = ""
                    binding.edtOtpFour.text = ""
                   /* binding.edtOtpFive.text = ""
                    binding.edtOtpSix.setText("")*/
                }
                if (s.length == 2) {
                    binding.edtOtpOne.text = s.toString()[0].toString()
                    binding.edtOtpTwo.text = s.toString()[1].toString()
                    binding.edtOtpThree.text = ""
                    binding.edtOtpFour.text = ""
                   /* binding.edtOtpFive.text = ""
                    binding.edtOtpSix.text = ""*/
                }
                if (s.length == 3) {
                    binding.edtOtpOne.text = s.toString()[0].toString()
                    binding.edtOtpTwo.text = s.toString()[1].toString()
                    binding.edtOtpThree.text = s.toString()[2].toString()
                    binding.edtOtpFour.text = ""
                    binding.edtOtpFive.text = ""
                    binding.edtOtpSix.text = ""
                }
                if (s.length == 4) {
                    binding.edtOtpOne.text = s.toString()[0].toString()
                    binding.edtOtpTwo.text = s.toString()[1].toString()
                    binding.edtOtpThree.text = s.toString()[2].toString()
                    binding.edtOtpFour.text = s.toString()[3].toString()
                   /* binding.edtOtpFive.text = ""
                    binding.edtOtpSix.text = ""*/
                }
                if (s.length == 5) {
                    binding.edtOtpOne.text = s.toString()[0].toString()
                    binding.edtOtpTwo.text = s.toString()[1].toString()
                    binding.edtOtpThree.text = s.toString()[2].toString()
                    binding.edtOtpFour.text = s.toString()[3].toString()
                    /*binding.edtOtpFive.text = s.toString()[4].toString()
                    binding.edtOtpSix.text = ""*/
                }
                if (s.length == 6) {
                    binding.edtOtpOne.text = s.toString()[0].toString()
                    binding.edtOtpTwo.text = s.toString()[1].toString()
                    binding.edtOtpThree.text = s.toString()[2].toString()
                    binding.edtOtpFour.text = s.toString()[3].toString()
                    /*binding.edtOtpFive.text = s.toString()[4].toString()
                    binding.edtOtpSix.text = s.toString()[5].toString()*/
                    //verifyOTP()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.edtAllOtp.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action === KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                        val OTP: String =
                            binding.edtOtpOne.getText().toString() + binding.edtOtpTwo.getText()
                                .toString() + binding.edtOtpThree.getText()
                                .toString() + binding.edtOtpFour.getText().toString()
                        if (validateNumber(OTP)) {
                            //verifyOTP(OTP)
                            //startActivity(new Intent(OTPActivity.this, MainActivity.class));
                        }
                        return@OnKeyListener true
                    }
                    else -> {}
                }
            }
            false
        })
    }
    fun validateNumber(OTP: String): Boolean {
        if (OTP == "") {
            binding.tvErrorText.setText("Please enter OTP")
            return false
        }
        if (OTP.length < 4) {
            binding.tvErrorText.setText("Please enter valid OTP")
            return false
        }
        return true
    }
}