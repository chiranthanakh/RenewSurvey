package com.renew.globalsurvey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VillageEntity (
    @PrimaryKey
    val id: Int? = null,
    val village_name: String,
    var mst_village_id: Int,
    val mst_panchayat_id: Int,
    val mst_tehsil_id: Int,
    val mst_district_id: Int,
    val mst_country_id: Int,
    val mst_state_id: Int,
)