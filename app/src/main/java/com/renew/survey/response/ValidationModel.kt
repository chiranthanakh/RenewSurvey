package com.renew.survey.response

data class ValidationModel(
    val otp: String,
    val project_info: ProjectInfo,
    val user_info: UserInfo,
    val user_type: String,
    val access_token: String
)