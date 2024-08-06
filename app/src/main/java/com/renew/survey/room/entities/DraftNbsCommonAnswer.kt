package com.renew.survey.room.entities

data class DraftNbsCommonAnswer(
    var id:Int?,
    var farmer_unique_id:String?,
    var date_and_time_of_visit: String,
    var gps_location : String,
    var banficary_name: String,
    var aadhar_card: String,
    var mobile_number : String,
    var alternate_mobile_number : String,
    var residential_address: String,
    var mst_district_id: String,
    var mst_state_id: String,
    var mst_tehsil_id: String,
    var mst_panchayat_id: String,
    var mst_village_id: String,
    var tbl_forms_id:Int?,
    var parent_survey_id: String,
    var answer_id:Int?
)
