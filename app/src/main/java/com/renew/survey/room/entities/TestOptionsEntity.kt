package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TestOptionsEntity(
    @PrimaryKey
    val id:Int,
    val is_answer: String,
    val tbl_forms_id: Int,
    val tbl_test_questions_id: Int,
    val tbl_test_questions_option_id: Int,
    val tbl_tests_id: Int
)