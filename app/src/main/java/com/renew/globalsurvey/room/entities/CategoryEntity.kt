package com.renew.globalsurvey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class CategoryEntity(
    @PrimaryKey
    val id: Int? = null,
    val category_name: String,
    val mst_categories_id: Int,
    val mst_divisions_id: Int
)