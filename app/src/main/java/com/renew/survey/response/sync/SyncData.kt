package com.renew.survey.response.sync


data class SyncData(
    val is_access_disable: Boolean,
    val tables: List<Table>,
    val assigned_survey:List<AssignedSurvey>
)