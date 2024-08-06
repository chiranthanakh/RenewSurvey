package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class FormQuestionGroupEntity(
    @PrimaryKey
    val id: Int? = null,
    val mst_question_group_id: Int,
    val order_by: Int,
    val mst_divisions_id: Int? = null
)