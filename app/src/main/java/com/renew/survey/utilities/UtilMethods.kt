package com.renew.survey.utilities

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UtilMethods {
    val dateFormatter=SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

    fun getFormattedDate(date: Date):String{
        return dateFormatter.format(date)
    }
    fun showToast(context: Context,message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }
}