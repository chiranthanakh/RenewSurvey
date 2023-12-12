package com.renew.survey.room.entities

data class QuestionGroupWithLanguage(
    val mst_question_group_id: Int,
    val order_by: Int,
    val title: String,
    var totalQuestions:Int?,
    var answeredQuestions:Int?,
    var selected: Boolean?
)
