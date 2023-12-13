package com.renew.survey.room.entities

import androidx.room.Ignore

data class QuestionGroupWithLanguage(
    val mst_question_group_id: Int,
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
        order_by: Int,
        title: String,
    ):this(
        mst_question_group_id,
        order_by,
        title,
        0,
        0,
        false,
        listOf()
    )
}
