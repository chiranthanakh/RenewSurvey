package com.renew.survey.room.entities

data class NbsDraftCommonAnswer(
    var unique_farmer_id:Int?,
    var farmer_name: String,
    var aadhar_number: String,
    var mobile_number : String,
    var alt_mobile_number : String,
    var address: String,
    var mst_district_id: String,
    var mst_state_id: String,
    var mst_tehsil_id: String,
    var mst_panchayat_id: String,
    var mst_village_id: String,
    var answer_id:Int?
    )
