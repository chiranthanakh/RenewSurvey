package com.renew.survey.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.renew.survey.room.entities.CategoryEntity
import com.renew.survey.room.entities.DivisionEntity
import com.renew.survey.room.entities.FileTypeEntity
import com.renew.survey.room.entities.FormEntity
import com.renew.survey.room.entities.FormLanguageEntity
import com.renew.survey.room.entities.FormQuestionEntity
import com.renew.survey.room.entities.FormQuestionGroupEntity
import com.renew.survey.room.entities.FormQuestionOptionsEntity
import com.renew.survey.room.entities.FormWithLanguage
import com.renew.survey.room.entities.ProjectEntity
import com.renew.survey.room.entities.ProjectPhaseQuestionEntity
import com.renew.survey.room.entities.ProjectsPhase

@Dao
interface FormsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllForms(formList: List<FormEntity>)

    @Query("Select tbl_forms_id,mst_form_language_id,title from FormEntity inner join FormLanguageEntity on FormEntity.tbl_forms_id=FormLanguageEntity.module_id where FormLanguageEntity.module='tbl_forms' and FormLanguageEntity.mst_language_id=:language")
    suspend fun getAllFormsWithLanguage(language:Int):List<FormWithLanguage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormsQuestions(formQuestions: List<FormQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormsQuestionGroup(formList: List<FormQuestionGroupEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormsQuestionOptions(formList: List<FormQuestionOptionsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormsLanguages(formList: List<FormLanguageEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFileTypes(formList: List<FileTypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjectPhase(formList: List<ProjectsPhase>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjects(formList: List<ProjectEntity>)

    @Query("Select * from ProjectEntity")
    suspend fun getAllProjects(): List<ProjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjectPhaseQuestionEntities(formList: List<ProjectPhaseQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDivisions(formList: List<DivisionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(formList: List<CategoryEntity>)


}