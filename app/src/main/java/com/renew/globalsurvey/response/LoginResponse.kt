package com.renew.globalsurvey.response

import com.google.gson.annotations.SerializedName


data class LoginResponse (

    @SerializedName("success" ) var success : String? = null,
    @SerializedName("data"    ) var data    : UserData?   = null,
    @SerializedName("message" ) var message : String? = null

)