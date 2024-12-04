package com.renew.globalsurvey.room.entities

data class TestOptionLanguage(
    val is_answer: String,
    val tbl_forms_id: Int,
    val tbl_test_questions_id: Int,
    val tbl_test_questions_option_id: Int,
    val tbl_tests_id: Int,
    val title: String
)