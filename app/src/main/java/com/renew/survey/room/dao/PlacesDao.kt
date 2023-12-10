package com.renew.survey.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.renew.survey.room.entities.DistrictEntity
import com.renew.survey.room.entities.PanchayathEntity
import com.renew.survey.room.entities.StatesEntity
import com.renew.survey.room.entities.TehsilEntity
import com.renew.survey.room.entities.VillageEntity
@Dao
interface PlacesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStates(languageList: List<StatesEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDistricts(languageList: List<DistrictEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTehsils(languageList: List<TehsilEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPanchayaths(languageList: List<PanchayathEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVillages(languageList: List<VillageEntity>)


}