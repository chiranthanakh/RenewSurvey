package com.renew.survey.utilities

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.renew.survey.databinding.CustomDialogBinding

object MyCustomDialog {
    fun showDialog(context:Context,title:String,content:String,yes:String,no:String,cancelable:Boolean,listener: ClickListener){
        val dialog=Dialog(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding=CustomDialogBinding.inflate(inflater)
        dialog.setContentView(binding.root)
        dialog.window!!
            .setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        binding.title.text=title
        binding.tvMessage.text=content
        binding.tvYes.text=yes
        binding.tvNo.text=no
        dialog.show()
        dialog.setCancelable(cancelable)
        binding.tvYes.setOnClickListener {
            dialog.cancel()
            listener.onYes()
        }
        binding.tvNo.setOnClickListener {
            dialog.cancel()
            listener.onNo()
        }
    }
    interface ClickListener{
        fun onYes()
        fun onNo()
    }
}