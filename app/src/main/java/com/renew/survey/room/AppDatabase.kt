package com.renew.survey.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.renew.survey.room.dao.FormsDao
import com.renew.survey.room.dao.LanguageDao
import com.renew.survey.room.dao.PlacesDao
import com.renew.survey.room.entities.AnswerEntity
import com.renew.survey.room.entities.CategoryEntity
import com.renew.survey.room.entities.CommonAnswersEntity
import com.renew.survey.room.entities.DistrictEntity
import com.renew.survey.room.entities.DivisionEntity
import com.renew.survey.room.entities.DynamicAnswersEntity
import com.renew.survey.room.entities.FileTypeEntity
import com.renew.survey.room.entities.FormEntity
import com.renew.survey.room.entities.FormLanguageEntity
import com.renew.survey.room.entities.FormQuestionEntity
import com.renew.survey.room.entities.FormQuestionGroupEntity
import com.renew.survey.room.entities.FormQuestionOptionsEntity
import com.renew.survey.room.entities.LanguageEntity
import com.renew.survey.room.entities.PanchayathEntity
import com.renew.survey.room.entities.ProjectEntity
import com.renew.survey.room.entities.ProjectPhaseQuestionEntity
import com.renew.survey.room.entities.ProjectsPhase
import com.renew.survey.room.entities.StatesEntity
import com.renew.survey.room.entities.TehsilEntity
import com.renew.survey.room.entities.VillageEntity

@Database(entities = [LanguageEntity::class,StatesEntity::class,DistrictEntity::class,TehsilEntity::class,PanchayathEntity::class,
    VillageEntity::class,FileTypeEntity::class,FormLanguageEntity::class,FormEntity::class,FormQuestionEntity::class,
    FormQuestionGroupEntity::class,FormQuestionOptionsEntity::class, ProjectEntity::class,ProjectsPhase::class,
    ProjectPhaseQuestionEntity::class,DivisionEntity::class,CategoryEntity::class, AnswerEntity::class,
    CommonAnswersEntity::class,DynamicAnswersEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun languageDao(): LanguageDao
    abstract fun placesDao(): PlacesDao
    abstract fun formDao(): FormsDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): AppDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, AppDatabase::class.java, "survey_database")
                    .fallbackToDestructiveMigration()
                    .build()

            return instance!!

        }
    }

}