package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class DistrictEntity(
    @PrimaryKey
    val id: Int? = null,
    val district_name: String,
    val mst_district_id: Int,
    val mst_country_id: Int,
    val mst_state_id: Int,
)
