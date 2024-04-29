package com.renew.survey.response

data class UserInfo(
    val aadhar_card: String,
    val address: String,
    val alt_moile: Any,
    val email: String,
    val full_name: String,
    val mobile: String,
    val mst_district_id: String,
    val mst_tehsil_id: String,
    val mst_panchayat_id: String,
    val mst_state_id: String,
    val mst_village_id: String,
    val pincode: String,
    val gender: String,
    val username: String,
    val date_of_birth: String,
    val profile_photo: String,
    val access_token: String,
    val tbl_users_id: String
)