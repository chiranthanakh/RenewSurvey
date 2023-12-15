package com.renew.survey.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.renew.survey.room.entities.AnswerEntity
import com.renew.survey.room.entities.CategoryEntity
import com.renew.survey.room.entities.CommonAnswersEntity
import com.renew.survey.room.entities.DivisionEntity
import com.renew.survey.room.entities.DynamicAnswersEntity
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
import com.renew.survey.room.entities.ProjectWithLanguage
import com.renew.survey.room.entities.ProjectsPhase
import com.renew.survey.room.entities.QuestionGroupWithLanguage

@Dao
interface FormsDao {

    @Query("SELECT COUNT(id) FROM AnswerEntity")
    suspend fun getTotalSurvey(): Int

    @Query("SELECT COUNT(id) FROM AnswerEntity where sync=0")
    suspend fun getTotalPendingSurvey(): Int

    @Query("SELECT COUNT(id) FROM AnswerEntity where sync=1")
    suspend fun getTotalDoneSurvey(): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllForms(formList: List<FormEntity>)

    @Query("Select fm.tbl_forms_id,mst_form_language_id,title,pp.tbl_project_phase_id,pp.version from FormEntity as fm " +
            "inner join ProjectsPhase pp on fm.id=pp.tbl_forms_id inner join FormLanguageEntity as fl on fm.tbl_forms_id=fl.module_id where fl.module='tbl_forms' and fl.mst_language_id=:language")
    suspend fun getAllFormsWithLanguage(language:Int):List<FormWithLanguage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormsQuestions(formQuestions: List<FormQuestionEntity>)

    @Query("SELECT l.title,q.* from FormQuestionEntity as q inner join FormLanguageEntity as l on q.id=l.module_id and l.module='tbl_form_questions' where l.mst_language_id=:language and q.mst_question_group_id=:group order by q.order_by")
    suspend fun getAllFormsQuestions(language: Int,group:Int):List<FormQuestionLanguage>

    @Query("SELECT title,tbl_project_phase_id,version,mqg.* FROM ProjectPhaseQuestionEntity pq " +
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

    @Query("Select * from ProjectEntity order by tbl_projects_id")
    suspend fun getAllProjects(): List<ProjectEntity>

    @Query("Select title,p.* from ProjectEntity as p  inner join FormLanguageEntity as l on p.tbl_projects_id=l.module_id where l.module='tbl_projects' and mst_language_id=1 order by tbl_projects_id")
    suspend fun getAllProjectsWithLanguage(): List<ProjectWithLanguage>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjectPhaseQuestionEntities(formList: List<ProjectPhaseQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDivisions(formList: List<DivisionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(formList: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(ans:AnswerEntity):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommonAnswer(ans:CommonAnswersEntity):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDynamicAnswer(ans:DynamicAnswersEntity):Long

    @Query("Select * from AnswerEntity where sync=0")
    suspend fun getAllUnsyncedAnswers(): List<AnswerEntity>


    @Query("Select * from CommonAnswersEntity where answer_id=:ansId limit 1")
    suspend fun getCommonAnswers(ansId:Int): CommonAnswersEntity

    @Query("Select * from DynamicAnswersEntity where answer_id=:answer_id")
    suspend fun getDynamicAns(answer_id:Int): List<DynamicAnswersEntity>

    @Query("Update AnswerEntity set sync=1 where id=:ans_id")
    suspend fun updateSync(ans_id:Int)

}