package com.renew.survey.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renew.survey.R
import com.renew.survey.adapter.AssignedSurveyAdapter
import com.renew.survey.databinding.ActivitySurveySelectBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.AssignedSurveyEntity
import com.renew.survey.utilities.PreferenceManager
import kotlinx.coroutines.launch

class SurveySelectActivity : BaseActivity() ,AssignedSurveyAdapter.ClickListener{
    lateinit var binding:ActivitySurveySelectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySurveySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager= PreferenceManager(this);

        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        getData()

    }
    fun getData(){
        lifecycleScope.launch {
            val list=AppDatabase.getInstance(this@SurveySelectActivity).formDao().getAllAssignedSurvey(preferenceManager.getForm().tbl_forms_id)
            binding.recyclerView.adapter=AssignedSurveyAdapter(this@SurveySelectActivity,list,this@SurveySelectActivity)
        }
    }

    override fun onProjectSelect(assignedSurveyEntity: AssignedSurveyEntity) {
        Intent(this,FormsDetailsActivity::class.java).apply {
            putExtra("assigned",gson.toJson(assignedSurveyEntity))
            startActivity(this)
        }
    }
}