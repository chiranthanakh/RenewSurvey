package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NbsAssignedFilterSurveyEntity(
    @PrimaryKey
    var id:Int?,
    var farmer_unique_id:String?,
    var date_and_time_of_visit: String,
    var gps_location : String,
    var banficary_name: String,
    var aadhar_card: String,
    var mobile_number : String,
    var alternate_mobile_number : String,
    var residential_address: String,
    var mst_district_id: Int,
    var mst_state_id: Int,
    var mst_tehsil_id: Int,
    var mst_panchayat_id: Int,
    var mst_village_id: Int,
    var mst_district_name: String? = "",
    var mst_panchayat_name: String? = "",
    var mst_state_name: String? = "",
    var mst_tehsil_name: String? = "",
    var mst_village_name: String? = "",
    var tbl_forms_id:Int?,
    var answer_id:Int?,
    val next_form_id: Int,
    val tbl_projects_id: Int,
    val status:Int?,
    var parent_survey_id: String,
    val tbl_project_survey_common_data_id: Int,
    )