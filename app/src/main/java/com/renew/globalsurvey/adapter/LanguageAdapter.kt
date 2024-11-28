package com.renew.globalsurvey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renew.globalsurvey.databinding.ItemLanguageBinding
import com.renew.globalsurvey.room.entities.LanguageEntity
import java.util.Locale

class LanguageAdapter(
    val context:Context, private var list: List<LanguageEntity>,
    var clickListener: ClickListener) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    class ViewHolder (val binding: ItemLanguageBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.text.text=this.title
                binding.tvShort.text= this.symbol!!.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()) else it.toString() }
                binding.llLanguage.setOnClickListener { clickListener.onLanguageSelected(this) }
            }
        }
    }
    interface ClickListener{
        fun onLanguageSelected(language: LanguageEntity)
    }
}