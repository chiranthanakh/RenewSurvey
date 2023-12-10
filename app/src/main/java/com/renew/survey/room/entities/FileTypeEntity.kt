package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class FileTypeEntity(
    @PrimaryKey
    val id: Int? = null,
    val extension: String,
    val mst_file_types_id: Int
)