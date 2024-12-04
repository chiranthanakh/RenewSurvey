package com.renew.globalsurvey.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.renew.globalsurvey.room.dao.FormsDao
import com.renew.globalsurvey.room.dao.LanguageDao
import com.renew.globalsurvey.room.dao.PlacesDao
import com.renew.globalsurvey.room.entities.AnswerEntity
import com.renew.globalsurvey.room.entities.AssignedFormEntry
import com.renew.globalsurvey.room.entities.AssignedSurveyEntity
import com.renew.globalsurvey.room.entities.CategoryEntity
import com.renew.globalsurvey.room.entities.CommonAnswersEntity
import com.renew.globalsurvey.room.entities.DistrictEntity
import com.renew.globalsurvey.room.entities.DivisionEntity
import com.renew.globalsurvey.room.entities.DynamicAnswersEntity
import com.renew.globalsurvey.room.entities.FileTypeEntity
import com.renew.globalsurvey.room.entities.FormEntity
import com.renew.globalsurvey.room.entities.FormLanguageEntity
import com.renew.globalsurvey.room.entities.FormQuestionEntity
import com.renew.globalsurvey.room.entities.FormQuestionGroupEntity
import com.renew.globalsurvey.room.entities.FormQuestionOptionsEntity
import com.renew.globalsurvey.room.entities.LanguageEntity
import com.renew.globalsurvey.room.entities.PanchayathEntity
import com.renew.globalsurvey.room.entities.ProjectEntity
import com.renew.globalsurvey.room.entities.ProjectPhaseQuestionEntity
import com.renew.globalsurvey.room.entities.ProjectsPhase
import com.renew.globalsurvey.room.entities.StatesEntity
import com.renew.globalsurvey.room.entities.TehsilEntity
import com.renew.globalsurvey.room.entities.TestEntry
import com.renew.globalsurvey.room.entities.TestOptionsEntity
import com.renew.globalsurvey.room.entities.TestQuestionsEntry
import com.renew.globalsurvey.room.entities.TutorialEntity
import com.renew.globalsurvey.room.entities.VillageEntity

@Database(entities = [LanguageEntity::class,StatesEntity::class,DistrictEntity::class,TehsilEntity::class,PanchayathEntity::class,
    VillageEntity::class,FileTypeEntity::class,FormLanguageEntity::class,FormEntity::class,FormQuestionEntity::class,
    FormQuestionGroupEntity::class,FormQuestionOptionsEntity::class, ProjectEntity::class,ProjectsPhase::class,
    ProjectPhaseQuestionEntity::class,DivisionEntity::class,CategoryEntity::class, AnswerEntity::class, TestEntry::class, TutorialEntity::class,
    TestQuestionsEntry::class, CommonAnswersEntity::class,DynamicAnswersEntity::class,AssignedSurveyEntity::class, TestOptionsEntity::class, AssignedFormEntry::class], version = 1)
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