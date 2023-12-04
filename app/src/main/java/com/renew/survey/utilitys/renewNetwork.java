package com.renew.survey.utilitys;

import com.renew.survey.request.Loginmodel;
import com.renew.survey.response.LoginResponseJava;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface renewNetwork {

    @POST("login")
    Call<LoginResponseJava> validatelogin(@Body Loginmodel loginmodel);
}
