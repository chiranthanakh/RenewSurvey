package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class ProjectPhaseQuestionEntity(
    @PrimaryKey
    val id: Int? = null,
    val mst_question_group_id: Int,
    val tbl_form_questions_id: Int,
    val tbl_forms_id: Int,
    val tbl_project_phase_id: Int,
    val tbl_project_phase_question_id: Int,
    val tbl_projects_id: Int,
    val version: String
)