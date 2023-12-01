package com.proteam.renewsurvey.utilitys

import android.content.Context


class SharedPrefManager constructor(private val mCtx: Context) {
    val sharedPreferences  =mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)


    val isLoggedIn: Boolean
        get() {

            return sharedPreferences.getInt("id", -1) != -1
        }
    fun saveToken(token: String){
        val editor = sharedPreferences.edit()
        editor.putString("token",token)
        editor.commit()
    }

    fun getToken(): String?{
        val editor = sharedPreferences.edit()
        return  sharedPreferences.getString("token","")
    }


    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private val SHARED_PREF_NAME = "my_shared_preff"
        private var mInstance: SharedPrefManager? = null
        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }
    }

}