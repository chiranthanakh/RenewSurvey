package com.renew.survey.response

data class ValidateUserModel(
    val otp: String,
    val tbl_users_id: String,
    val access_token: String,
    val user_type: String
)