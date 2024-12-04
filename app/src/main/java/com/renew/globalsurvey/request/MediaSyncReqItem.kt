package com.renew.globalsurvey.request

data class MediaSyncReqItem(
    val app_unique_code: String,
    val file_name: String,
    val phase: String,
    val tbl_form_questions_id: String,
    val tbl_forms_id: String,
    val tbl_projects_id: String,
    val tbl_users_id: String,
    val version: String,
    @Transient
    val path:String
)