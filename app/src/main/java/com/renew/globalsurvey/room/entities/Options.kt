package com.renew.globalsurvey.room.entities

import androidx.room.Ignore

data class Options (
    val title:String,
    @Ignore
    var selected: Boolean
){
    constructor(
        title: String,
    ):this(
        title,
        false
    )
}