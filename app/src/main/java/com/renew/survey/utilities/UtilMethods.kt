package com.renew.survey.utilities

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UtilMethods {
    private val dateFormatter=SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    private val dateFormatterServer=SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    fun getFormattedDate(date: Date,format:String):String{
        val dateFormatter=SimpleDateFormat(format, Locale.ENGLISH)
        return dateFormatter.format(date)
    }

    fun getFormattedDate(date: Date):String{
        return dateFormatter.format(date)
    }
    fun showToast(context: Context,message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }
    fun getDisplayDateFormat(dateStr:String):String{
        val date= dateFormatterServer.parse(dateStr)
        return dateFormatter.format(date)
    }
}