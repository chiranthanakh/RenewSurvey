package com.renew.survey.views

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.renew.survey.databinding.ActivitySignUpDetailsBinding
import com.renew.survey.utilitys.UtilMethods

class SignUpDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpDetailsBinding
    val initCalendar=Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSubmit.setOnClickListener {
            formValidation()
        }
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
            UtilMethods.showToast(this,"Please enter email")
            return
        }
        if (binding.edtPassword.text.toString().isEmpty()){
            UtilMethods.showToast(this,"Please enter password")
            return
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
        if (!binding.rbMale.isChecked || !binding.rbFemale.isChecked){
            UtilMethods.showToast(this,"Please select gender")
            return
        }

    }
}