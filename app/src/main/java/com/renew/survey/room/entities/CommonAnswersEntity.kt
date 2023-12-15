package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class CommonAnswersEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var aadhar_card: String,
    var annual_family_income: String,
    var banficary_name: String,
    var electricity_connection_available: String,
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
    var no_of_cow_dung_per_day: String,
    var no_of_cylinder_per_year: String,
    var willing_to_contribute_clean_cooking: String,
    var wood_use_per_day_in_kg: String,
    var answer_id:Int,
)