package com.renew.globalsurvey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FormEntity(
    @PrimaryKey
    val id: Int? = null,
    val mst_categories_id: Int,
    val mst_divisions_id: Int,
    val tbl_forms_id: Int
)