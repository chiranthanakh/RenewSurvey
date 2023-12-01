package com.proteam.renewsurvey.utilitys

import com.proteam.example.LoginResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiInterface {
    @GET("Auth/login")
    suspend fun getAllMovies() : Response<LoginResponse>

    companion object {
        lateinit var retrofitService: ApiInterface
        fun getInstance() : ApiInterface {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://renewsms.proteam.co.in/api/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(ApiInterface::class.java)
            }
            return retrofitService!!
        }

    }
}