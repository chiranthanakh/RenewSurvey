package com.renew.survey.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.renew.survey.room.entities.CategoryEntity
import com.renew.survey.room.entities.DivisionEntity
import com.renew.survey.room.entities.FileType
import com.renew.survey.room.entities.FormEntity
import com.renew.survey.room.entities.FormLanguage
import com.renew.survey.room.entities.FormQuestion
import com.renew.survey.room.entities.FormQuestionGroup
import com.renew.survey.room.entities.FormQuestionOptions
import com.renew.survey.room.entities.ProjectEntity
import com.renew.survey.room.entities.ProjectPhaseQuestionEntity
import com.renew.survey.room.entities.ProjectsPhase

@Dao
interface FormsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllForms(formList: List<FormEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormsQuestions(formQuestions: List<FormQuestion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormsQuestionGroup(formList: List<FormQuestionGroup>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormsQuestionOptions(formList: List<FormQuestionOptions>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormsLanguages(formList: List<FormLanguage>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFileTypes(formList: List<FileType>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjectPhase(formList: List<ProjectsPhase>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjects(formList: List<ProjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjectPhaseQuestionEntities(formList: List<ProjectPhaseQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDivisions(formList: List<DivisionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(formList: List<CategoryEntity>)


}