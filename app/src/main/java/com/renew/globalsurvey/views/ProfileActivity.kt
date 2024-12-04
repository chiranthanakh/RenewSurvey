package com.renew.globalsurvey.views

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.renew.globalsurvey.databinding.ActivityProfileBinding

class ProfileActivity : BaseActivity() {
    lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this).load(preferenceManager.getUserdata().profile_photo).into(binding.profileImage)
        binding.usernameText.setText(preferenceManager.getUserdata().full_name)
        binding.phonenumber.setText("Mobile Number : "+preferenceManager.getUserdata().mobile)
        binding.altMobile.setText("Alternative Number : "+preferenceManager.getUserdata().alt_moile.toString())
        binding.aadhar.setText("Aadhar card : "+preferenceManager.getUserdata().aadhar_card)
        binding.userid.setText("User ID : "+preferenceManager.getUserdata().tbl_users_id)
        binding.UserType.setText("User Type : "+preferenceManager.getUserdata().user_type)
        binding.userid.visibility = View.GONE
        binding.UserType.visibility = View.GONE
        binding.pincode.setText("Pincode : "+preferenceManager.getUserdata().pincode)
        binding.address.setText("Address : "+preferenceManager.getUserdata().address)


    }
}