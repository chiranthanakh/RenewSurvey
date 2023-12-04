package com.renew.survey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renew.survey.response.Language
import com.renew.survey.databinding.ItemLanguageBinding

class LanguageAdapter(val context:Context, private var list: List<Language>) :
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
            }
        }
    }
}