package com.renew.globalsurvey.room.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class TestQuestionLanguage(
    @PrimaryKey
    val id: Int? = null,
    val format: String? = "",
    val is_mandatory: String,
    val is_special_char_allowed: String,
    val is_validation_required: String,
    val max_length: String,
    val min_length: String,
    val order_by: Int,
    val question_type: String,
    val tbl_test_questions_id: Int,
    val title: String,
    var answer:String?,
    var answer2:String? = "",
    @Ignore
    var answers:ArrayList<TestOptionLanguage> = arrayListOf(),
    @Ignore
    var options: List<TestOptionLanguage>
) {
    constructor(
        id: Int?,
        format: String?,
        is_mandatory: String,
        is_special_char_allowed: String,
        is_validation_required: String,
        max_length: String,
        min_length: String,
        order_by: Int,
        question_type: String,
        tbl_test_questions_id: Int,
        title: String,
        answer: String?,
        answer2: String?,
    )
            : this(
        id,
        format,
        is_mandatory,
        is_special_char_allowed,
        is_validation_required,
        max_length,
        min_length,
        order_by,
        question_type,
        tbl_test_questions_id,
        title,
        answer,
        answer2,
        arrayListOf(),
        listOf(),
    )
}