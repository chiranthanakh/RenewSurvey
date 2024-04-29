package com.renew.survey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renew.survey.databinding.ItemAssignedSurveyBinding
import com.renew.survey.databinding.ItemProjectSelectBinding
import com.renew.survey.response.Project
import com.renew.survey.room.entities.AssignedFilterSurveyEntity
import com.renew.survey.room.entities.AssignedSurveyEntity
import com.renew.survey.room.entities.ProjectEntity
import com.renew.survey.room.entities.ProjectWithLanguage

class AssignedSurveyAdapter(val context:Context, private var list: List<AssignedFilterSurveyEntity>, var clickListener: ClickListener) :
    RecyclerView.Adapter<AssignedSurveyAdapter.ViewHolder>() {

    class ViewHolder (val binding: ItemAssignedSurveyBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAssignedSurveyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.tvName.text="${this.banficary_name}"
                binding.btnContinue.setOnClickListener { clickListener.onProjectSelect(this)}
            }
        }
    }
    interface ClickListener{
        fun onProjectSelect(assignedSurveyEntity: AssignedFilterSurveyEntity)
    }
}