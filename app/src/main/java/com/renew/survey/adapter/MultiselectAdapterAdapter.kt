package com.renew.survey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renew.survey.databinding.ItemMultiSelectBinding
import com.renew.survey.databinding.ItemProjectSelectBinding
import com.renew.survey.request.MultiSelectItem
import com.renew.survey.response.Project
import com.renew.survey.room.entities.Options
import com.renew.survey.room.entities.ProjectEntity

class MultiselectAdapterAdapter(val context:Context, private var list: List<Options>) : RecyclerView.Adapter<MultiselectAdapterAdapter.ViewHolder>() {

    class ViewHolder (val binding: ItemMultiSelectBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMultiSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.checkbox.text=this.title
                if (this.selected){
                    binding.checkbox.isChecked=true
                }
                binding.checkbox.setOnClickListener {
                    list[position].selected = binding.checkbox.isChecked
                }
            }
        }
    }

}