package com.renew.globalsurvey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PanchayathEntity (
    @PrimaryKey
    val id: Int? = null,
    val panchayat_name: String,
    val mst_panchayat_id: Int,
    val mst_tehsil_id: Int,
    val mst_district_id: Int,
    val mst_country_id: Int,
    val mst_state_id: Int,
)