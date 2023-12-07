package com.renew.survey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renew.survey.response.Language
import com.renew.survey.databinding.ItemLanguageBinding
import com.renew.survey.databinding.ItemProjectSelectBinding
import com.renew.survey.response.Project
import java.util.Locale

class ProjectAdapter(val context:Context, private var list: List<Project>, var clickListener: ClickListener) :
    RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    class ViewHolder (val binding: ItemProjectSelectBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProjectSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.llProject1.setOnClickListener { clickListener.onProjectSelect(Project(""))}
        /*with(holder){
            with(list[position]){

               *//* binding.text.text=this.title
                binding.tvShort.text= this.symbol!!.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()) else it.toString() }
                binding.llLanguage.setOnClickListener { clickListener.onProjectSelect(this) }*//*
                binding.llLanguage.setOnClickListener { clickListener.onProjectSelect(this)}
            }
        }*/
    }
    interface ClickListener{
        fun onProjectSelect(language: Project)
    }
}