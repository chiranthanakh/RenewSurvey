package com.renew.globalsurvey.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class DynamicAnswersEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val mst_question_group_id: Int,
    var answer:String?,
    val tbl_form_questions_id: Int,
    val answer_id:Int,
)