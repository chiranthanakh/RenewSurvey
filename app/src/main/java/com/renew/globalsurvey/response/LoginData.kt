package com.renew.globalsurvey.response

import com.google.gson.annotations.SerializedName


data class LoginData (

  @SerializedName("access_token" ) var accessToken : String?  = null,
  @SerializedName("status"       ) var status      : Boolean? = null,
  @SerializedName("tbl_users_id" ) var userId      : String?  = null,
  @SerializedName("message"      ) var message     : String?  = null,
  @SerializedName("note"         ) var note        : String?  = null

)