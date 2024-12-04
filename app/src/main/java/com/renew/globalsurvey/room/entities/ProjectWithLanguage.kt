package com.renew.globalsurvey.room.entities

import androidx.room.PrimaryKey
data class ProjectWithLanguage(
    @PrimaryKey
    val id: Int? = null,
    val title:String,
    val mst_categories_id: Int,
    val mst_country_id: Int,
    val mst_divisions_id: Int,
    val mst_languages_id: String?,
    val mst_state_id: Int,
    val project_co_ordinator: String,
    val project_code: String,
    val project_manager: String,
    val tbl_projects_id: Int
)