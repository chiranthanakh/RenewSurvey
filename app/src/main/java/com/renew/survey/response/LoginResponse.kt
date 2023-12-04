package com.renew.example

import com.google.gson.annotations.SerializedName


data class LoginResponse (

  @SerializedName("success" ) var success : String? = null,
  @SerializedName("data"    ) var data    : Data?   = Data(),
  @SerializedName("message" ) var message : String? = null

)