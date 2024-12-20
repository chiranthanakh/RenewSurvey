package com.renew.globalsurvey.views

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renew.globalsurvey.adapter.DraftSurveyAdapter
import com.renew.globalsurvey.databinding.ActivityDraftSelectBinding
import com.renew.globalsurvey.room.AppDatabase
import com.renew.globalsurvey.room.entities.DraftCommonAnswer
import kotlinx.coroutines.launch

class DraftSelectActivity : BaseActivity() ,DraftSurveyAdapter.ClickListener{
    lateinit var binding:ActivityDraftSelectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDraftSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager= LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        getData()
    }
    fun getData(){
        lifecycleScope.launch {
            val list= AppDatabase.getInstance(this@DraftSelectActivity).formDao().getAllDraftSurvey(preferenceManager.getForm().tbl_forms_id,preferenceManager.getProject().tbl_projects_id)
            binding.recyclerView.adapter= DraftSurveyAdapter(this@DraftSelectActivity,list,this@DraftSelectActivity)
        }
    }


    override fun onProjectSelect(assignedSurveyEntity: DraftCommonAnswer) {
        preferenceManager.saveDraft(assignedSurveyEntity.answer_id!!)
        Intent(this,FormsDetailsActivity::class.java).apply {
            putExtra("draft",gson.toJson(assignedSurveyEntity))
            startActivity(this)
        }
    }
}