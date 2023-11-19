/*
 * Created by Krishnamurthy T
 * Copyright (c) 2019 .  V V Technologies All rights reserved.
 * Last modified 27/11/18 3:07 PM
 *
 */

package com.example.renewsurvey.utilitys;




/**
 * Created by Chiranthan on 08/07/2022.
 */

public interface OnResponseListener<T> {

    void onResponse(T response, WebServices.ApiType URL, boolean isSucces, int code);

}