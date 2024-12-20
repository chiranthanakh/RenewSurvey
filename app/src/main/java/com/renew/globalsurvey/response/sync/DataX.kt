package com.renew.globalsurvey.response.sync

data class DataX(
    val allowed_file_type: String,
    val category_name: String,
    val create_date: String,
    val created_by: String,
    val district_name: String,
    val division_code: String,
    val division_name: String,
    val extension: String,
    val form_name_in_english: Any,
    val form_name_in_hindi: Any,
    val form_name_in_marathi: Any,
    val format: String,
    val is_active: String,
    val is_delete: String,
    val is_mandatory: String,
    val is_released: String,
    val is_special_char_allowed: String,
    val is_validation_required: String,
    val is_visible_column: String,
    val last_update: String? = "",
    val max_file_size: String,
    val max_length: String,
    val min_length: String,
    val module: String,
    val module_id: String,
    val mst_categories_id: String,
    val mst_country_id: String,
    val mst_district_id: String,
    val mst_divisions_id: String,
    val mst_file_types_id: String,
    val mst_form_language_id: String,
    val mst_language_id: String,
    val mst_languages_id: String,
    val mst_panchayat_id: String,
    val mst_question_group_id: String,
    val mst_state_id: String,
    val mst_tehsil_id: String,
    var mst_village_id: String,
    val order_by: String,
    val panchayat_name: String,
    val phase: String,
    val project_co_ordinator: String,
    val project_code: String,
    val project_manager: String,
    val question_type: String,
    val release_version: Any,
    val state_code: String,
    val state_name: String,
    val symbol: String,
    val tbl_form_questions_id: String,
    val tbl_form_questions_option_id: String,
    val tbl_forms_id: String,
    val tbl_project_phase_id: String,
    val tbl_project_phase_question_id: String,
    val tbl_projects_id: String,
    val tehsil_name: String,
    val title: String,
    val version: String,
    val village_name: String,
    val tbl_tutorials: Int,
    val tutorial_file: String,
    val tutorial_code: String,
    val tbl_tests_id : Int,
    val passing_marks : String,
    val test_code : String,
    val tbl_test_questions_id : String,
    val answer: String,
    val is_answer: String,
    val tbl_test_questions_option_id: String,
    val tbl_users_assigned_projects_id: String,
    val tbl_users_id: String,
    )