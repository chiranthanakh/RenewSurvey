package com.renew.survey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renew.survey.databinding.ItemAssignedSurveyBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.AssignedFilterSurveyEntity
import com.renew.survey.room.entities.NbsAssignedFilterSurveyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class AssignedNbsSurveyAdapter(val context:Context, private var list: List<NbsAssignedFilterSurveyEntity>, var clickListener: ClickListener) :
    RecyclerView.Adapter<AssignedNbsSurveyAdapter.ViewHolder>() {
    var villageName : String? = ""
    class ViewHolder (val binding: ItemAssignedSurveyBinding):RecyclerView.ViewHolder(binding.root)

    private val job = Job()

     val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
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
                binding.tvSurveyCode.text = "${this.farmer_unique_id}"
                binding.tvSurveyVillage.text = this.mst_village_name
                binding.btnContinue.setOnClickListener { clickListener.onProjectSelectNbs(this)}
            }
        }
    }

    interface ClickListener{
        fun onProjectSelectNbs(assignedSurveyEntity: NbsAssignedFilterSurveyEntity)
    }
}