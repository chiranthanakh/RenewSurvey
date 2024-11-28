package com.renew.globalsurvey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TestQuestionsEntry(
    @PrimaryKey
    val id: Int? = null,
    val tbl_test_questions_id : Int,
    val tbl_tests_id : Int,
    val tbl_forms_id : Int,
    val created_by : String,
    val question_type : String,
    val is_mandatory : String,
    val order_by : String,
    val is_validation_required : String,
    val is_special_char_allowed : String,
    val min_length : String,
    val max_length : String,
    val format : String? = "",
    val answer : String? = "",
    val create_date : String,
    val is_active : String,
    val is_delete : String,
    val last_update: String? = ""
 )
