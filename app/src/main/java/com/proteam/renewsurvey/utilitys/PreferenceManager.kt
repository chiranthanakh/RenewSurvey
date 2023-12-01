package com.proteam.renewsurvey.utilitys

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager constructor(context: Context){
    private val USER_ID="user_id"

    val sharedPreferences:SharedPreferences  = context.getSharedPreferences("RenewSurvey", Context.MODE_PRIVATE)

    fun saveUserId(id: String){
        val editor = sharedPreferences.edit()
        editor.putString(USER_ID,id)
        editor.apply()
    }

    fun getUserId(): String?{
        return  sharedPreferences.getString(USER_ID,"")
    }
    fun saveToken(string: String){
        val editor = sharedPreferences.edit()
        editor.putString("token",string)
        editor.apply()
    }

    fun getToken(): String?{
        return  sharedPreferences.getString("token","")
    }


    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}