package com.renew.survey.response

import com.google.gson.annotations.SerializedName


data class LoginResponse (

    @SerializedName("success" ) var success : String? = null,
    @SerializedName("data"    ) var data    : LoginData?   = LoginData(),
    @SerializedName("message" ) var message : String? = null

)