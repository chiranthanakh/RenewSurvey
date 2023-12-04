package com.renew.survey.response

import com.google.gson.annotations.SerializedName

data class Language (
    @SerializedName("mst_language_id" ) var mst_language_id : String?  = null,
    @SerializedName("title" ) var title : String?  = null,
    @SerializedName("symbol" ) var symbol : String?  = null,
)