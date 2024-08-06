package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class FormQuestionLanguage (
    @PrimaryKey
    val id: Int? = null,
    val allowed_file_type: String,
    val format: String?,
    val is_mandatory: String,
    val is_special_char_allowed: String,
    val is_validation_required: String,
    val max_file_size: String,
    val max_length: String,
    val min_length: String,
    val mst_question_group_id: Int,
    val order_by: Int,
    val question_type: String,
    var tbl_form_questions_id: Int,
    val title: String,
    var answer:String?,
    val has_dependancy_question:String? = "",
    val parent_question_id: Int?,

    @Ignore
    var options: List<Options>
) {
    constructor(
        id: Int?,
        allowed_file_type: String,
        format: String?,
        is_mandatory: String,
        is_special_char_allowed: String,
        is_validation_required: String,
        max_file_size: String,
        max_length: String,
        min_length: String,
        mst_question_group_id: Int,
        order_by: Int,
        question_type: String,
        tbl_form_questions_id: Int,
        title: String,
        answer: String?,
        has_dependancy_question: String?,
        parent_question_id: Int,
    )
            : this(
        id,
        allowed_file_type,
        format,
        is_mandatory,
        is_special_char_allowed,
        is_validation_required,
        max_file_size,
        max_length,
        min_length,
        mst_question_group_id,
        order_by,
        question_type,
        tbl_form_questions_id,
        title,
        answer,
        has_dependancy_question,
        parent_question_id,
        listOf()
    )
}