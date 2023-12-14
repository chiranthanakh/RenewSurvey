package com.renew.survey.room.entities

import androidx.room.Ignore
import java.util.stream.IntStream

data class QuestionGroupWithLanguage(
    val mst_question_group_id: Int,
    val tbl_project_phase_id: Int,
    val version: String,
    val order_by: Int,
    val title: String,
    var totalQuestions:Int?,
    var answeredQuestions:Int?,
    var selected: Boolean?,
    @Ignore
    var questions:List<FormQuestionLanguage>
) {
    constructor(
        mst_question_group_id: Int,
        tbl_project_phase_id: Int,
        version: String,
        order_by: Int,
        title: String,
    ):this(
        mst_question_group_id,
        tbl_project_phase_id,
        version,
        order_by,
        title,
        0,
        0,
        false,
        listOf()
    )
}
