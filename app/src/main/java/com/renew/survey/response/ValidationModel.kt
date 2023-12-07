package com.renew.survey.response

data class ValidationModel(
    val otp: String,
    val project_info: ProjectInfo,
    val user_info: List<Any>,
    val user_type: String
)