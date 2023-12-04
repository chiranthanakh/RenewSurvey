package com.renew.example

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("access_token" ) var accessToken : String?  = null,
  @SerializedName("status"       ) var status      : Boolean? = null,
  @SerializedName("user_id"      ) var userId      : String?  = null,
  @SerializedName("message"      ) var message     : String?  = null,
  @SerializedName("note"         ) var note        : String?  = null

)