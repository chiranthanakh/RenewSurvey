package com.renew.globalsurvey.response.sync


data class SyncData(
    val is_access_disable: Boolean,
    val tables: List<Table>,
    val assigned_survey:List<AssignedSurvey>,
    val training_tutorials:List<String>
)