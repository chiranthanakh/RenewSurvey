package com.proteam.renewsurvey.utilitys;

import com.proteam.renewsurvey.Request.Loginmodel;
import com.proteam.renewsurvey.Responsce.LoginResponseJava;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface renewNetwork {

    @POST("login")
    Call<LoginResponseJava> validatelogin(@Body Loginmodel loginmodel);
}
