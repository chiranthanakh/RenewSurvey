package com.renew.survey.response.sync

data class Table(
    val columns: List<Column>,
    val `data`: List<DataX>,
    val table_name: String,
    val title: String
)