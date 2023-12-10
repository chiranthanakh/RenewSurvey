package com.renew.survey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renew.survey.databinding.ItemProjectSelectBinding
import com.renew.survey.response.Project
import com.renew.survey.room.entities.ProjectEntity

class ProjectAdapter(val context:Context, private var list: List<ProjectEntity>, var clickListener: ClickListener) :
    RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    class ViewHolder (val binding: ItemProjectSelectBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProjectSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.tvProject.text=this.project_code
                binding.llProject1.setOnClickListener { clickListener.onProjectSelect(this)}
            }
        }
    }
    interface ClickListener{
        fun onProjectSelect(project: ProjectEntity)
    }
}