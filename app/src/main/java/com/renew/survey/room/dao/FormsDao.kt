package com.renew.survey.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.renew.survey.room.entities.DraftCommonAnswer
import com.renew.survey.room.entities.AnswerEntity
import com.renew.survey.room.entities.AssignedFilterSurveyEntity
import com.renew.survey.room.entities.AssignedFormEntry
import com.renew.survey.room.entities.AssignedSurveyEntity
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
import com.renew.survey.room.entities.TestDetailsEntry
import com.renew.survey.room.entities.TestEntry
import com.renew.survey.room.entities.TestOptionLanguage
import com.renew.survey.room.entities.TestOptionsEntity
import com.renew.survey.room.entities.TestQuestionLanguage
import com.renew.survey.room.entities.TestQuestionsEntry
import com.renew.survey.room.entities.TutorialEntity
import com.renew.survey.room.entities.TutorialsDetailsEntry

@Dao
interface FormsDao {

    @Query("SELECT COUNT(id) FROM AnswerEntity where tbl_forms_id=:formId and draft=0 and tbl_projects_id=:project")
    suspend fun getTotalSurvey(formId: Int,project: Int): Int

    @Query("SELECT COUNT(id) FROM AnswerEntity where sync=0 and tbl_forms_id=:formId and draft=0 and tbl_projects_id=:project")
    suspend fun getTotalPendingSurvey(formId: Int,project: Int): Int

    @Query("SELECT COUNT(id) FROM AnswerEntity where sync=1 and tbl_forms_id=:formId and draft=0 and tbl_projects_id=:project")
    suspend fun getTotalDoneSurvey(formId: Int,project: Int): Int

    @Query("SELECT COUNT(id) FROM AnswerEntity where draft=1 and tbl_forms_id=:formId and tbl_projects_id=:project")
    suspend fun getDraftSurvey(formId: Int,project: Int): Int
    @Query("SELECT COUNT(id) FROM AnswerEntity where draft=1")
    suspend fun getAllDraftSurveyCount(): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllForms(formList: List<FormEntity>)

    @Query("Select fm.tbl_forms_id,mst_form_language_id,title,pp.tbl_project_phase_id,pp.version from FormEntity as fm " +
            "inner join ProjectsPhase pp on fm.id=pp.tbl_forms_id and pp.tbl_projects_id=:projectId inner join FormLanguageEntity as fl on fm.tbl_forms_id=fl.module_id where fl.module='tbl_forms' and fl.mst_language_id=:language order by fm.tbl_forms_id")
    suspend fun getAllFormsWithLanguage(language:Int,projectId:Int):List<FormWithLanguage>

    @Query("Select  ap.tbl_forms_id, mfl.title, tpp.tbl_project_phase_id,tpp.version FROM AssignedFormEntry as ap " +
            " inner JOIN ProjectEntity tp ON tp.tbl_projects_id = ap.tbl_projects_id inner JOIN ProjectsPhase tpp ON tpp.tbl_projects_id = ap.tbl_projects_id AND tpp.tbl_forms_id = ap.tbl_forms_id AND tpp.is_released = 'Y'" +
            " inner JOIN FormEntity tf ON tf.tbl_forms_id = ap.tbl_forms_id inner JOIN FormLanguageEntity mfl ON mfl.module = 'tbl_forms' AND mfl.module_id = tf.tbl_forms_id /*AND mfl.is_active = 1 AND mfl.is_delete = 0*/ AND mfl.mst_language_id = :language" +
            " inner JOIN FormLanguageEntity mfl1 ON mfl1.module = 'tbl_projects' AND mfl1.module_id = tp.tbl_projects_id /*AND mfl1.is_active = 1 AND mfl1.is_delete = 0*/ AND mfl1.mst_language_id =:language WHERE ap.tbl_users_id = :userid AND ap.tbl_projects_id =:projectId")
    suspend fun getAssignedFormsWithLanguage(language:Int,projectId:Int,userid:Int):List<FormWithLanguage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormsQuestions(formQuestions: List<FormQuestionEntity>)

    @Query("SELECT l.title,q.* from \n" +
            "ProjectPhaseQuestionEntity as p inner join\n" +
            "FormQuestionEntity as q  on q.tbl_form_questions_id = p.tbl_form_questions_id and p.tbl_projects_id=:project \n" +
            "inner join FormLanguageEntity as l on l.module_id = q.id and l.module='tbl_form_questions' \n" +
            "where l.mst_language_id=:language and q.mst_question_group_id=:group and p.tbl_forms_id=:formId order by q.order_by")
    suspend fun getAllFormsQuestions(language: Int,group:Int,formId:Int,project: Int):List<FormQuestionLanguage>

    @Query("SELECT mfl.title, tq.* from" +
            " TestQuestionsEntry as tq LEFT JOIN FormLanguageEntity mfl ON mfl.module = 'tbl_test_questions' AND mfl.module_id = tbl_test_questions_id " +
            "WHERE tbl_tests_id = :formId AND is_active AND mst_language_id =:language")
    suspend fun getAllTestQuestions(language: Int,formId:Int):List<TestQuestionLanguage>

    @Query("SELECT mfl.title,tt.* from" +
            " TestEntry as tt LEFT JOIN FormLanguageEntity mfl ON mfl.module = 'tbl_tests' AND mfl.module_id = tt.tbl_tests_id " +
            "WHERE tt.tbl_forms_id = :formId  AND mst_language_id =:language")
    suspend fun getTest(language: Int,formId:Int):TestDetailsEntry

    @Query("SELECT tt.* from" +
            " TutorialEntity as tt /*LEFT JOIN FormLanguageEntity mfl ON mfl.module = 'tbl_tests' AND mfl.module_id = tt.tbl_tutorials_id */" +
            "WHERE tt.tbl_forms_id = :formId")
    suspend fun getTutorial(formId:Int): TutorialsDetailsEntry

    @Query("SELECT l.title,d.answer,q.id,q.allowed_file_type,q.format,q.is_mandatory,q.is_special_char_allowed,q.is_validation_required,q.max_file_size,q.max_length,q.min_length,q.mst_question_group_id,q.order_by,q.question_type,q.tbl_form_questions_id,q.tbl_forms_id from \n" +
            "ProjectPhaseQuestionEntity as p inner join\n" +
            "FormQuestionEntity as q  on q.id = p.tbl_form_questions_id and p.tbl_projects_id=:project\n" +
            "inner join FormLanguageEntity as l on l.module_id = q.id and l.module='tbl_form_questions' \n" +
            "inner join DynamicAnswersEntity as d on d.tbl_form_questions_id=q.tbl_form_questions_id "+
            "where l.mst_language_id=:language and q.mst_question_group_id=:group and p.tbl_forms_id=:formId and d.answer_id=:answerId order by q.order_by")
    suspend fun getAllFormsQuestionsWithDraftAnswer(language: Int,group:Int,formId:Int,answerId:Int,project: Int):List<FormQuestionLanguage>


    @Query("SELECT title,tbl_project_phase_id,version,mqg.* FROM ProjectPhaseQuestionEntity pq " +
            "LEFT JOIN FormQuestionGroupEntity mqg ON mqg.mst_question_group_id = pq.mst_question_group_id " +
            "LEFT JOIN FormLanguageEntity mfl ON mfl.module_id = mqg.mst_question_group_id AND mfl.module = 'mst_question_group' " +
            "WHERE mfl.mst_language_id = :language AND pq.tbl_projects_id =:project AND " +
            "pq.tbl_forms_id = :formId GROUP BY mqg.mst_question_group_id order by mqg.order_by")
    suspend fun getAllFormsQuestionGroup(language: Int,project:Int,formId: Int):List<QuestionGroupWithLanguage>

    @Query("SELECT l.title from FormQuestionOptionsEntity as q inner join FormLanguageEntity as l on q.tbl_form_questions_option_id=l.module_id where l.module='tbl_form_questions_option' and tbl_form_questions_id=:question and l.mst_language_id=:language")
    suspend fun getAllOptions(question: Int,language: Int):List<Options>

    @Query("SELECT l.title, q.* from TestOptionsEntity as q inner join FormLanguageEntity as l on q.tbl_test_questions_option_id=l.module_id where l.module='tbl_test_questions_option' and tbl_test_questions_id=:question and l.mst_language_id=:language")
    suspend fun getAllTestOptions(question: Int,language: Int):List<TestOptionLanguage>

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
    suspend fun insertAllAssignedSurvey(formList: List<AssignedSurveyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjects(formList: List<ProjectEntity>)

    @Query("Select * from ProjectEntity order by tbl_projects_id")
    suspend fun getAllProjects(): List<ProjectEntity>

    @Query("Select title,p.* from ProjectEntity as p  inner join FormLanguageEntity as l on p.tbl_projects_id=l.module_id where l.module='tbl_projects' and mst_language_id=:language order by tbl_projects_id")
    suspend fun getAllProjectsWithLanguage(language:Int): List<ProjectWithLanguage>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjectPhaseQuestionEntities(formList: List<ProjectPhaseQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertassignedforms(formList: List<AssignedFormEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDivisions(formList: List<DivisionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(formList: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTutorials(formList: List<TutorialEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTests(formList: List<TestEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTestQuestions(formList: List<TestQuestionsEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTestOptions(formList: List<TestOptionsEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(ans:AnswerEntity):Long

    @Update
    suspend fun updateAnswer(ans:AnswerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommonAnswer(ans:CommonAnswersEntity):Long

    @Update
    suspend fun updateCommonAnswer(ans:CommonAnswersEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDynamicAnswer(ans:DynamicAnswersEntity):Long

    @Query("Select * from AnswerEntity where sync=0 and draft=0")
    suspend fun getAllUnsyncedAnswers(): List<AnswerEntity>

    @Query("Select * from AnswerEntity where media_sync=0 and draft=0")
    suspend fun getAllUnsyncedMediaAnswers(): List<AnswerEntity>

    @Query("Select * from CommonAnswersEntity where answer_id=:ansId limit 1")
    suspend fun getCommonAnswers(ansId:Int): CommonAnswersEntity

    @Query("Select * from TutorialEntity where tbl_forms_id =:formId limit 1")
    suspend fun getTrainings(formId:Int): TutorialEntity

    @Query("Select * from DynamicAnswersEntity where answer_id=:answer_id")
    suspend fun getDynamicAns(answer_id:Int): List<DynamicAnswersEntity>

    @Query("Update AnswerEntity set sync=1 where id=:ans_id")
    suspend fun updateSync(ans_id:Int)

    @Query("Update AnswerEntity set media_sync=1 where id=:ans_id")
    suspend fun updateMediaSync(ans_id:Int)

    @Query("Select * from AssignedSurveyEntity where status=0 and next_form_id=:formId")
    suspend fun getAllAssignedSurvey(formId: Int): List<AssignedSurveyEntity>

    @Query("Select * from AssignedSurveyEntity where status=0 and next_form_id=:formId and tbl_projects_id=:project")
    suspend fun getAllFilteredAssignedSurvey(formId: Int, project: Int): List<AssignedFilterSurveyEntity>

    @Query("Select tbl_forms_id,ca.*  from AnswerEntity as a inner join CommonAnswersEntity ca on a.id=ca.answer_id where a.draft=1 and a.tbl_forms_id=:formId and tbl_projects_id=:project")
    suspend fun getAllDraftSurvey(formId: Int, project: Int): List<DraftCommonAnswer>

    @Query("UPDATE DynamicAnswersEntity set answer=:answer where mst_question_group_id=:group and tbl_form_questions_id=:question and answer_id=:answer_id")
    suspend fun updateDynamicAnswer(answer:String,group: Int,question: Int,answer_id: Int)

    @Query("UPDATE AssignedSurveyEntity set status=1 where id=:id")
    suspend fun updateAssignedStatus(id: Int)

    @Query("Delete from AnswerEntity where draft=:id")
    suspend fun deleteDrafts(id: Int)

    @Query("Delete from AssignedSurveyEntity where id=:id")
    suspend fun deleteAssigned(id: Int)

    @Query("Select * from ProjectsPhase where tbl_forms_id=:formId and tbl_projects_id=:project")
    suspend fun getProjectPhase(formId: Int,project: Int):ProjectsPhase

}