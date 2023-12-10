package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class DivisionEntity(
    @PrimaryKey
    val id: Int? = null,
    val division_code: String,
    val division_name: String,
    val mst_divisions_id: Int
)