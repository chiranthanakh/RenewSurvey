package com.renew.survey.utilities

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import java.io.File
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
    fun getMimeType(file: File): String? {
        var type: String? = null
        val url = file.toString()
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(extension.lowercase(Locale.getDefault()))
        }
        if (type == null) {
            type = "image/*" // fallback type. You might set it to */*
        }
        return type
    }
    fun getResizedBitmap(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var image = image
        return if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            image
        } else {
            image
        }
    }
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnected
    }

}