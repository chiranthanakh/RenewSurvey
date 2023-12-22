package com.renew.survey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renew.survey.R
import com.renew.survey.databinding.ItemLanguageBinding
import com.renew.survey.databinding.ItemQuestionGroupBinding
import com.renew.survey.room.entities.LanguageEntity
import com.renew.survey.room.entities.QuestionGroupWithLanguage
import java.util.Locale

class QuestionGroupAdapter(
    val context:Context, private var list: List<QuestionGroupWithLanguage>,
    var clickListener: ClickListener,var previousSelected:Int) :
    RecyclerView.Adapter<QuestionGroupAdapter.ViewHolder>() {

    class ViewHolder (val binding: ItemQuestionGroupBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuestionGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                if (this.selected!=null){
                    if (selected!!){
                        binding.llItem.setBackgroundColor(context.getColor(R.color.demo_state_third))
                    }else{
                        binding.llItem.setBackgroundColor(context.getColor(R.color.green2))
                    }
                }else{
                    if (position==0){
                        binding.llItem.setBackgroundColor(context.getColor(R.color.demo_state_third))
                        list[0].selected=true
                    }else{
                        binding.llItem.setBackgroundColor(context.getColor(R.color.green2))
                        list[position].selected=false
                    }

                }
                binding.tvQuestionGroupName.text=this.title
                binding.llItem.setOnClickListener {
                    notifyItemChanged(previousSelected)
                    list[previousSelected].selected=false
                    list[position].selected=true
                    notifyItemChanged(position)
                    clickListener.onQuestionGroupSelected(this,position)
                    previousSelected=position
                }
            }
        }
    }
    interface ClickListener{
        fun onQuestionGroupSelected(questionGroup: QuestionGroupWithLanguage,pos:Int)
    }
}