package com.renew.globalsurvey.utilities

import com.google.gson.JsonElement
import com.renew.globalsurvey.room.entities.AnswerEntity
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit


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
        @Field("device_serial_number")serialNumber:String,
        @Field("app_key")appKey:String,
        @Field("user_type")userType:String,
    ) : Response<JsonElement>

    @FormUrlEncoded
    @POST("Auth/verify_user")
    suspend fun validateUser(
        @Field("mobile")mobile:String,
        @Field("app_key")appKey:String,
    ) : Response<JsonElement>

    @FormUrlEncoded
    @POST("Auth/reset_password")
    suspend fun resetPassword(
        @Field("mobile")mobile:String,
        @Field("tbl_users_id")tbl_users_id:String,
        @Field("new_password")new_password:String,
        @Field("confirm_password")confirm_password:String,
        @Header("Authorization") headers: String
        ) : Response<JsonElement>

    @FormUrlEncoded
    @POST("Auth/reset_password")
    suspend fun changePassword(
        @Field("mobile")mobile:String,
        @Field("tbl_users_id")tbl_users_id:String,
        @Field("old_password")old_password:String,
        @Field("new_password")new_password:String,
        @Field("confirm_password")confirm_password:String,
        @Header("Authorization") headers: String,
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
    @POST("ProjectMaster/send_verification_code")
    suspend fun sendOtpForDistrubution(
        @Header("Authorization") headers: String,
        @Field("tbl_users_id")tbl_users_id:String,
    ) : Response<JsonElement>
    @FormUrlEncoded
    @POST("Common/get_villages")
    suspend fun getVillage(
        @Field("mst_panchayat_id")mst_panchayat_id:String,
        @Field("app_key")appKey:String,
    ) : Response<JsonElement>

    @Multipart
    @POST("Auth/register")
    suspend fun register(
        @Part("tbl_projects_id")tbl_projects_id:RequestBody,
        @Part("project_code")project_code: RequestBody,
        @Part("aadhar_card")aadhar_card:RequestBody,
        @Part("mobile")mobile:RequestBody,
        @Part("password")password:RequestBody?,
        @Part("full_name")full_name:RequestBody,
        @Part("username")username:RequestBody,
        @Part("address")address:RequestBody,
        @Part("mst_state_id")mst_state_id:RequestBody,
        @Part("mst_village_id")mst_village_id:RequestBody,
        @Part("mst_district_id")mst_district_id:RequestBody,
        @Part("mst_tehsil_id")mst_tehsil_id:RequestBody,
        @Part("mst_panchayat_id")mst_panchayat_id:RequestBody,
        @Part("pincode")pincode:RequestBody,
        @Part("email")email:RequestBody,
        @Part("app_key")app_key:RequestBody,
        @Part("device_type")device_type:RequestBody,
        @Part("device_id")device_id:RequestBody,
        @Part("fcm_token")fcm_token:RequestBody,
        @Part("gender")gender:RequestBody,
        @Part("date_of_birth")date_of_birth:RequestBody,
        @Part("co_ordinator_id")co_ordinator_id:RequestBody,
        @Part("user_type")user_type:RequestBody,
        @Part profilePhoto: MultipartBody.Part
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
    @FormUrlEncoded
    @POST("Synchronization/sync_data_from_server")
    suspend fun syncDataFromServer(
        @Header("Authorization") headers: String,
        @Field("tbl_users_id") tbl_users_id: String,
    ) : Response<JsonElement>

    @POST("ProjectMaster/sync_survey")
    suspend fun syncSubmitForms(
        @Header("Authorization") headers: String,
        @Body data: List<AnswerEntity>,
    ) : Response<JsonElement>

    @Multipart
    @POST("ProjectMaster/sync_media")
    suspend fun syncMediaFiles(
        @Header("Authorization") headers: String,
        @Part("post_data")tbl_projects_id:RequestBody,
        //@Part("post_data") generalPaymentRequest:List<MediaSyncReqItem>,
        @Part file: Array<MultipartBody.Part?>?
    ) : Response<JsonElement>

    companion object {
        var retrofitService: ApiInterface?=null
         //  val BASE_URL="https://devrenewsms.proteam.co.in/api/v2/"
        // val BASE_URL="https://renewsms.proteam.co.in/api/v2/"
         val BASE_URL = "https://cookstove.renew.com//api/v2/"



        fun getInstance() : ApiInterface? {

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(90,TimeUnit.SECONDS)
                .readTimeout(90,TimeUnit.SECONDS)
                .build()

            if (retrofitService == null) {
               /* val gson = GsonBuilder()
                    .setLenient()
                    .create()*/
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                retrofitService = retrofit.create(ApiInterface::class.java)
            }
            return retrofitService
        }
    }
}