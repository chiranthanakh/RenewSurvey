package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TutorialsDetailsEntry(
    @PrimaryKey
    val id: Int? = null,
    val tbl_tutorials_id: Int,
    val tbl_forms_id: Int,
    val tutorial_file: String,
    val tutorial_code: String,
    val last_update: String? = "",
    val is_delete: String,
    val is_active: String,
    val create_date: String,
    val created_by: String,
   // val title: String? = ""
)

