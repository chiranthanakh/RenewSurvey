package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StatesEntity(
    @PrimaryKey
    val id: Int? = null,
    val state_name: String,
    val state_code: String,
    val mst_state_id: Int,
    val mst_country_id: Int,
)
