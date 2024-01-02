package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class FormQuestionEntity(
    @PrimaryKey
    val id: Int? = null,
    val allowed_file_type: String,
    val format: String? = "",
    val is_mandatory: String,
    val is_special_char_allowed: String,
    val is_validation_required: String,
    val max_file_size: String,
    val max_length: String,
    val min_length: String,
    val mst_question_group_id: Int,
    val order_by: Int,
    val question_type: String,
    val tbl_form_questions_id: Int,
    val tbl_forms_id: Int,
    val answer:String=""
)