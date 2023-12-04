package com.renew.survey.utilitys

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
    @GET("Common/get_languages")
    suspend fun getLanguage(@Header("Authorization")headers: String) : Response<JsonElement>


    companion object {
        var retrofitService: ApiInterface?=null
        fun getInstance() : ApiInterface? {

            if (retrofitService == null) {
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