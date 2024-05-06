package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class AssignedFormEntry(
    @PrimaryKey
    val id: Int? = null,
    val tbl_users_assigned_projects_id: Int,
    val tbl_projects_id: Int,
    val tbl_forms_id: Int,
    val tbl_users_id: Int,
    val tbl_project_phase_id: Int,
   /* val mst_country_id: Int,
    val mst_state_id: Int,
    val mst_district_id: Int,
    val mst_tehsil_id: Int,
    val mst_panchayat_id: Int,
    val mst_village_id: Int,*/
    val created_by: String,
    //val create_date: String,
    val is_active: Int,
    val is_delete: String,
)