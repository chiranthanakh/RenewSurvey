package com.renew.globalsurvey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CommonAnswersEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int?,
    var aadhar_card: String,
    var date_and_time_of_visit: String,
    var did_the_met_person_allowed_for_data : String,
    var gps_location : String,
    var annual_family_income: String,
    var banficary_name: String,
    var do_you_have_aadhar_card: String,
    var font_photo_of_aadar_card: String,
    var back_photo_of_aadhar_card: String,
    var electricity_connection_available: String,
    var total_electricity_bill: String,
    var frequency_of_bill_payment: String,
    var photo_of_bill: String,
    var family_size: String,
    var gender: String,
    var house_type: String,
    var is_cow_dung: String,
    var is_lpg_using: String,
    var mobile_number: String,
    var mst_district_id: String,
    var mst_state_id: String,
    var mst_tehsil_id: String,
    var mst_panchayat_id: String,
    var mst_village_id: String,
    var no_of_cattles_own: String,
    var no_of_cylinder_per_year: String,
    var device_serial_number: String,
    var cost_of_lpg_cyliner: String,
    var willing_to_contribute_clean_cooking: String,
    var wood_use_per_day_in_kg: String,
    var parent_survey_id: String,
    var tbl_project_survey_common_data_id: String,
    var family_member_below_15_year: String?,
    var family_member_above_15_year: String?,
    var do_you_have_ration_or_aadhar: String,
    var farmland_is_owned_by_benficary: String,
    var if_5m_area_is_available_near_by: String,
    var answer_id:Int?,
)