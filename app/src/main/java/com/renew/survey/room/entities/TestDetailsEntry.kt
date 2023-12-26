package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TestDetailsEntry(
    @PrimaryKey
    val id: Int? = null,
    val tbl_tests_id : Int,
    val tbl_forms_id : Int,
    val created_by : String,
    val test_code : String,
    val passing_marks : String? = "",
    val create_date : String,
    val is_active : String,
    val is_delete : String,
    val last_update: String? = "",
    val title: String? = ""
    )
