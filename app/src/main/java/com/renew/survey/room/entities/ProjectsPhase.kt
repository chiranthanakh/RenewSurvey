package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class ProjectsPhase(
    @PrimaryKey
    val id: Int? = null,
    val phase: Int,
    val release_version: String,
    val tbl_forms_id: Int,
    val tbl_project_phase_id: Int,
    val tbl_projects_id: Int,
    val version: Int,
    val is_released: String
)