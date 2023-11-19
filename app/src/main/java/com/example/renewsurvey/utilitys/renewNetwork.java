package com.example.renewsurvey.utilitys;

import com.example.renewsurvey.Request.Loginmodel;
import com.example.renewsurvey.Responsce.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface renewNetwork {

    @POST("login")
    Call<LoginResponse> validatelogin(@Body Loginmodel loginmodel);
}
