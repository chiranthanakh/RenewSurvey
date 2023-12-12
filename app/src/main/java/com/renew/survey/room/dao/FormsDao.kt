package com.renew.survey.room.dao

import android.adservices.adid.AdId
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
import com.renew.survey.room.entities.FormQuestionLanguage
import com.renew.survey.room.entities.FormQuestionOptionsEntity
import com.renew.survey.room.entities.FormWithLanguage
import com.renew.survey.room.entities.Options
import com.renew.survey.room.entities.ProjectEntity
import com.renew.survey.room.entities.ProjectPhaseQuestionEntity
import com.renew.survey.room.entities.ProjectsPhase
import com.renew.survey.room.entities.QuestionGroupWithLanguage

@Dao
interface FormsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllForms(formList: List<FormEntity>)

    @Query("Select fm.tbl_forms_id,mst_form_language_id,title,pp.tbl_project_phase_id,pp.version from FormEntity as fm " +
            "inner join ProjectsPhase pp on fm.id=pp.tbl_forms_id inner join FormLanguageEntity as fl on fm.tbl_forms_id=fl.module_id where fl.module='tbl_forms' and fl.mst_language_id=:language")
    suspend fun getAllFormsWithLanguage(language:Int):List<FormWithLanguage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormsQuestions(formQuestions: List<FormQuestionEntity>)

    @Query("SELECT l.title,q.* from FormQuestionEntity as q inner join FormLanguageEntity as l on q.id=l.module_id and l.module='tbl_form_questions' where l.mst_language_id=:language and q.mst_question_group_id=:group order by q.order_by")
    suspend fun getAllFormsQuestions(language: Int,group:Int):List<FormQuestionLanguage>

    @Query("SELECT title,mqg.* FROM ProjectPhaseQuestionEntity pq " +
            "LEFT JOIN FormQuestionGroupEntity mqg ON mqg.mst_question_group_id = pq.mst_question_group_id " +
            "LEFT JOIN FormLanguageEntity mfl ON mfl.module_id = mqg.mst_question_group_id AND mfl.module = 'mst_question_group' " +
            "WHERE mfl.mst_language_id = :language AND pq.tbl_projects_id =:project AND " +
            "pq.tbl_forms_id = :formId GROUP BY mqg.mst_question_group_id order by mqg.order_by")
    suspend fun getAllFormsQuestionGroup(language: Int,project:Int,formId: Int):List<QuestionGroupWithLanguage>

    @Query("SELECT l.title from FormQuestionOptionsEntity as q inner join FormLanguageEntity as l on q.tbl_form_questions_option_id=l.module_id where l.module='tbl_form_questions_option' and tbl_form_questions_id=:question and l.mst_language_id=:language")
    suspend fun getAllOptions(question: Int,language: Int):List<Options>

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