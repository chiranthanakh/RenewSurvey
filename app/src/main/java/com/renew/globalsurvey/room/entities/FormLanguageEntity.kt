package com.renew.globalsurvey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class FormLanguageEntity(
    @PrimaryKey
    val id: Int? = null,
    val module: String,
    val module_id: Int,
    val mst_form_language_id: Int,
    val mst_language_id: Int,
    val title: String
)