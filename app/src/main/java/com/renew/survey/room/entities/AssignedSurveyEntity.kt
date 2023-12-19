package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AssignedSurveyEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int?,
    val status:Int?,
    val aadhar_card: String,
    val annual_family_income: String,
    val app_unique_code: String,
    val banficary_name: String,
    val electricity_connection_available: String,
    val family_size: String,
    val gender: String,
    val house_type: String,
    val is_cow_dung: String,
    val is_lpg_using: String,
    val mobile_number: String,
    val mst_district_id: Int,
    val mst_panchayat_id: Int?,
    val mst_state_id: Int,
    val mst_tehsil_id: Int,
    val mst_village_id: Int,
    val next_form_id: Int,
    val no_of_cattles_own: String,
    val no_of_cow_dung_per_day: String,
    val no_of_cylinder_per_year: String,
    val parent_survey_id: String,
    val reason: String,
    val system_approval: String,
    val tbl_project_survey_common_data_id: Int,
    val tbl_projects_id: Int,
    val willing_to_contribute_clean_cooking: String,
    val wood_use_per_day_in_kg: String
)