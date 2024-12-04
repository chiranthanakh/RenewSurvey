package com.renew.globalsurvey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LanguageEntity (
    @PrimaryKey
    val id: Int? = null,
    val title: String,
    val symbol: String,
    val mst_language_id: Int,
)