package com.renew.survey.utilities

import com.google.gson.JsonElement
import okhttp3.Interceptor.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiInterface {
    @FormUrlEncoded
    @POST("Auth/login")
    suspend fun login(
        @Field("mobile")mobile:String,
        @Field("password")password:String,
        @Field("app_key")appKey:String,
    ) : Response<JsonElement>

    @FormUrlEncoded
    @POST("Auth/validate_project")
    suspend fun validateProject(
        @Field("mobile")mobile:String,
        @Field("project_code")projectCode:String,
        @Field("aadhar_card")aadharCard:String,
        @Field("app_key")appKey:String,
        @Field("user_type")userType:String,
    ) : Response<JsonElement>

    @FormUrlEncoded
    @POST("Common/get_states")
    suspend fun getStates(
        @Field("app_key")appKey:String,
    ) : Response<JsonElement>
    @FormUrlEncoded
    @POST("Common/get_districts")
    suspend fun getDistricts(
        @Field("mst_state_id")mst_state_id:String,
        @Field("app_key")appKey:String,
    ) : Response<JsonElement>
    @FormUrlEncoded
    @POST("Common/get_tehsils")
    suspend fun getTehsil(
        @Field("mst_district_id")mst_district_id:String,
        @Field("app_key")appKey:String,
    ) : Response<JsonElement>
    @FormUrlEncoded
    @POST("Common/get_panchayats")
    suspend fun getPanchayat(
        @Field("mst_tehsil_id")mst_tehsil_id:String,
        @Field("app_key")appKey:String,
    ) : Response<JsonElement>
    @FormUrlEncoded
    @POST("Common/get_villages")
    suspend fun getVillage(
        @Field("mst_panchayat_id")mst_panchayat_id:String,
        @Field("app_key")appKey:String,
    ) : Response<JsonElement>


    @FormUrlEncoded
    @POST("Auth/register")
    suspend fun register(
        @Field("tbl_projects_id")tbl_projects_id:String,
        @Field("project_code")project_code:String,
        @Field("aadhar_card")aadhar_card:String,
        @Field("mobile")mobile:String,
        @Field("password")password:String,
        @Field("full_name")full_name:String,
        @Field("username")username:String,
        @Field("address")address:String,
        @Field("mst_state_id")mst_state_id:String,
        @Field("mst_village_id")mst_village_id:String,
        @Field("mst_district_id")mst_district_id:String,
        @Field("mst_tehsil_id")mst_tehsil_id:String,
        @Field("mst_panchayat_id")mst_panchayat_id:String,
        @Field("pincode")pincode:String,
        @Field("email")email:String,
        @Field("app_key")app_key:String,
        @Field("device_type")device_type:String,
        @Field("device_id")device_id:String,
        @Field("fcm_token")fcm_token:String,
        @Field("gender")gender:String,
        @Field("date_of_birth")date_of_birth:String,
        @Field("co_ordinator_id")co_ordinator_id:String,
        @Field("user_type")user_type:String,
    ) : Response<JsonElement>

    @GET("Common/get_languages")
    suspend fun getLanguage(@Header("Authorization")headers: String) : Response<JsonElement>
    @FormUrlEncoded
    @POST("ProjectMaster/get_projects")
    suspend fun getProjects(
        @Header("Authorization") headers: String,
        @Field("tbl_users_id") tbl_users_id: String,
        @Field("tbl_users_id") mst_language_id: String,
    ) : Response<JsonElement>


    companion object {
        var retrofitService: ApiInterface?=null
        fun getInstance() : ApiInterface? {
            if (retrofitService == null) {
               /* val gson = GsonBuilder()
                    .setLenient()
                    .create()*/
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://renewsms.proteam.co.in/api/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(ApiInterface::class.java)
            }
            return retrofitService
        }

    }
}