package com.renew.survey.room.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class AnswerEntity(
    @PrimaryKey
    var id:Int?,
    val app_unique_code: String,
    val mst_language_id: String,
    var parent_survey_id: String,
    var tbl_project_survey_common_data_id: String,
    val phase: String,
    val tbl_forms_id: String,
    val tbl_project_phase_id: String,
    val tbl_projects_id: String,
    val tbl_users_id: String,
    val version: String,
    var sync: Int,
    var draft: Int,
    @Ignore
    @SerializedName("common_question_answer")
    var commonAnswersEntity: CommonAnswersEntity?,
    @Ignore
    @SerializedName("question_answer")
    var dynamicAnswersList : List<DynamicAnswersEntity>
) {

    constructor(
        id: Int?,
        app_unique_code: String,
        mst_language_id: String,
        parent_survey_id: String,
        phase: String,
        tbl_forms_id: String,
        tbl_project_phase_id: String,
        tbl_projects_id: String,
        tbl_users_id: String,
        version: String,
        sync: Int,
        draft: Int
    ) : this(
        id,
        app_unique_code,
        mst_language_id,
        parent_survey_id,
        "",
        phase,
        tbl_forms_id,
        tbl_project_phase_id,
        tbl_projects_id,
        tbl_users_id,
        version,
        sync,
        draft,
        null,
        listOf()
    )
}