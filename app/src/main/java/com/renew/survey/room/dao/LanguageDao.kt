package com.renew.survey.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session.User
import com.renew.survey.room.entities.LanguageEntity


@Dao
interface LanguageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguage(languageEntity: LanguageEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLanguage(languageList: List<LanguageEntity>)

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg users: User?)*/
}