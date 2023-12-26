package com.renew.survey.utilities

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.google.common.reflect.TypeToken
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

    fun savePassingMarks(marks: Int){
        val editor = sharedPreferences.edit()
        editor.putInt("passingMarks",marks)
        editor.apply()
    }

    fun getPassingMarks(): Int{
        return  sharedPreferences.getInt("passingMarks",0)
    }

    fun saveTrainingState(key: String, value: List<String>) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(value)
        editor.putString(key, json)
        editor.apply()
    }

    fun getTrainingState(key: String): List<String>? {
        val json = sharedPreferences.getString(key, null)
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveUrl(key: String, value: ArrayList<String>) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(value)
        editor.putString(key, json)
        editor.apply()
    }

    fun getUrl(key: String): ArrayList<String>? {
        val json = sharedPreferences.getString(key, null)
        val gson = Gson()
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return gson.fromJson(json, type)
    }

    // Save a list of URIs to SharedPreferences
    fun saveUriList(uriList: ArrayList<Uri>) {
        val uriSet = HashSet(uriList.map { it.toString() })
        sharedPreferences.edit().putStringSet("uri_list", uriSet).apply()
    }

    fun loadUriList(): ArrayList<Uri> {
        val uriSet = sharedPreferences.getStringSet("uri_list", HashSet()) ?: HashSet()
        return uriSet.map { Uri.parse(it) } as ArrayList<Uri>
    }

     /*fun saveUriList(uriList: ArrayList<Uri>) {
        val editor = sharedPreferences.edit()
        val uriSet = HashSet(uriList)
        editor.putStringSet("uri_list", uriSet)
        editor.apply()
    }

     fun loadUriList(): ArrayList<String> {
        val loadedUriSet = sharedPreferences.getStringSet("uri_list", HashSet<String>()) ?: HashSet()
        return ArrayList(loadedUriSet)
    }*/


    fun saveDraft(language: Int){
        val editor = sharedPreferences.edit()
        editor.putInt("draft",language)
        editor.apply()
    }

    fun getDraft(): Int{
        return  sharedPreferences.getInt("draft",0)
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

    fun saveTraining(training: String, formid: String){
        val editor = sharedPreferences.edit()
        editor.putString("training",training)
        editor.apply()
    }

    fun getTraining(): String?{
        return sharedPreferences.getString("training","")
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