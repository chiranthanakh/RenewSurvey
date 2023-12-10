package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class FormQuestionOptions(
    @PrimaryKey
    val id: Int? = null,
    val tbl_form_questions_id: Int,
    val tbl_form_questions_option_id: Int,
    val tbl_forms_id: Int
)