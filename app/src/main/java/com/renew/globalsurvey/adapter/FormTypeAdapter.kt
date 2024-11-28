package com.renew.globalsurvey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renew.globalsurvey.databinding.ItemFormLayoutBinding
import com.renew.globalsurvey.room.entities.FormWithLanguage

class FormTypeAdapter(
    val context:Context, private var list: List<FormWithLanguage>,
    var clickListener: ClickListener) :
    RecyclerView.Adapter<FormTypeAdapter.ViewHolder>() {

    class ViewHolder (val binding: ItemFormLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFormLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.text.text=this.title
                binding.llSurvey.setOnClickListener { clickListener.onFormSelected(this) }
            }
        }
    }
    interface ClickListener{
        fun onFormSelected(form: FormWithLanguage)
    }
}