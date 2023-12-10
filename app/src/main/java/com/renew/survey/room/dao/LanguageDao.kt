package com.renew.survey.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.renew.survey.room.entities.LanguageEntity


@Dao
interface LanguageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguage(languageEntity: LanguageEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLanguage(languageList: List<LanguageEntity>)

    @Query("SELECT * FROM languageentity")
    suspend fun getAll(): List<LanguageEntity>

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg users: User?)*/
}