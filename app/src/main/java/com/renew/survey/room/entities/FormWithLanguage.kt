package com.renew.survey.room.entities

data class FormWithLanguage(
    val tbl_forms_id: Int,
    val mst_form_language_id: Int,
    val title: String,
    val tbl_project_phase_id:Int,
    val version:Int?

)