package com.renew.survey.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.renew.survey.room.entities.DistrictEntity
import com.renew.survey.room.entities.PanchayathEntity
import com.renew.survey.room.entities.StatesEntity
import com.renew.survey.room.entities.TehsilEntity
import com.renew.survey.room.entities.VillageEntity
@Dao
interface PlacesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStates(languageList: List<StatesEntity>)

    @Query("select * from StatesEntity")
    suspend fun getAllStates():List<StatesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDistricts(languageList: List<DistrictEntity>)

    @Query("select * from DistrictEntity where mst_state_id=:state")
    suspend fun getAllDistricts(state:Int):List<DistrictEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTehsils(languageList: List<TehsilEntity>)

    @Query("select * from TehsilEntity where mst_district_id=:district")
    suspend fun getAllTehsils(district:Int):List<TehsilEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPanchayaths(languageList: List<PanchayathEntity>)

    @Query("select * from PanchayathEntity where mst_tehsil_id=:tehsil")
    suspend fun getAllPanchayath(tehsil:Int):List<PanchayathEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVillages(languageList: List<VillageEntity>)

    @Query("select * from VillageEntity where mst_panchayat_id=:panchyath")
    suspend fun getAllVillages(panchyath:Int):List<VillageEntity>

    @Query("select village_name from VillageEntity where mst_village_id=:village")
    suspend fun getVillage(village:Int):String?

    @Query("select panchayat_name from PanchayathEntity where mst_panchayat_id =:panchayat")
    suspend fun getPanchayath(panchayat:Int):String?

    @Query("select tehsil_name from TehsilEntity where mst_tehsil_id = :tasil")
    suspend fun getTehsils(tasil:Int):String?

    @Query("select district_name from DistrictEntity where mst_district_id=:district")
    suspend fun getDistricts(district:Int):String?

    @Query("select state_name from StatesEntity where mst_state_id = :state ")
    suspend fun getStates(state : Int):String?
}