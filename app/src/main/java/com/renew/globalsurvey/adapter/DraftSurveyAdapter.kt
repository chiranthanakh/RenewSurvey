package com.renew.globalsurvey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renew.globalsurvey.databinding.ItemAssignedSurveyBinding
import com.renew.globalsurvey.room.entities.DraftCommonAnswer

class DraftSurveyAdapter(val context:Context, private var list: List<DraftCommonAnswer>, var clickListener: ClickListener) :
    RecyclerView.Adapter<DraftSurveyAdapter.ViewHolder>() {

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
        fun onProjectSelect(assignedSurveyEntity: DraftCommonAnswer)
    }
}