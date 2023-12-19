package com.renew.survey.utilities

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.renew.survey.response.UserData
import com.renew.survey.room.entities.FormWithLanguage
import com.renew.survey.room.entities.ProjectWithLanguage

class PreferenceManager constructor(context: Context){
    private val USER_ID="user_id"
    val gson=Gson()

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
    fun saveFCMToken(string: String){
        val editor = sharedPreferences.edit()
        editor.putString("FCMToken",string)
        editor.apply()
    }

    fun getFCMToken(): String?{
        return  sharedPreferences.getString("FCMToken","")
    }

    fun saveLanguage(language: Int){
        val editor = sharedPreferences.edit()
        editor.putInt("language",language)
        editor.apply()
    }

    fun getLanguage(): Int{
        return  sharedPreferences.getInt("language",0)
    }

    fun saveProject(projectEntity: ProjectWithLanguage){
        val editor = sharedPreferences.edit()
        editor.putString("project",gson.toJson(projectEntity))
        editor.apply()
    }

    fun getProject(): ProjectWithLanguage{
        return gson.fromJson(sharedPreferences.getString("project",""),ProjectWithLanguage::class.java)
    }


    fun saveForm(projectEntity: FormWithLanguage){
        val editor = sharedPreferences.edit()
        editor.putString("form",gson.toJson(projectEntity))
        editor.apply()
    }

    fun getForm(): FormWithLanguage{
        return gson.fromJson(sharedPreferences.getString("form",""),FormWithLanguage::class.java)
    }

    fun saveSync(date: String,sync: Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean(date,sync)
        editor.apply()
    }

    fun getSync(date: String,): Boolean{
        return sharedPreferences.getBoolean(date,false)
    }

    fun saveLocation(location: String){
        val editor = sharedPreferences.edit()
        editor.putString("location",location)
        editor.apply()
    }

    fun getLocation(): String?{
        return sharedPreferences.getString("location","")
    }




    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


    fun saveUserData(user: UserData){
        val editor = sharedPreferences.edit()
        editor.putString("userData",gson.toJson(user))
        editor.apply()
    }

    fun getUserdata(): UserData{
        return gson.fromJson(sharedPreferences.getString("userData",""),UserData::class.java)
    }


}