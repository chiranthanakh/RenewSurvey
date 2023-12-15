package com.renew.survey.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renew.survey.adapter.ProjectAdapter
import com.renew.survey.databinding.ActivityProjectBinding
import com.renew.survey.response.Project
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.ProjectEntity
import com.renew.survey.room.entities.ProjectWithLanguage
import kotlinx.coroutines.launch

class ProjectActivity : BaseActivity() ,ProjectAdapter.ClickListener{
    lateinit var binding: ActivityProjectBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)

        getProjectData()

       //val intent = Intent(this@ProjectActivity, UserRoleActivity::class.java)
        //            startActivity(intent)

    }
    fun getProjectData(){
        lifecycleScope.launch {
            val projects=AppDatabase.getInstance(this@ProjectActivity).formDao().getAllProjectsWithLanguage()
            binding.recyclerView.adapter=ProjectAdapter(this@ProjectActivity,projects,this@ProjectActivity)
            /*ApiInterface.getInstance()?.apply {
                val response= getProjects(preferenceManager.getToken()!!,preferenceManager.getUserId()!!,preferenceManager.getLanguage()!!)
                binding.progressLayout.visibility= View.GONE
                if (response!!.isSuccessful){
                    val jsonObject= JSONObject(response.body().toString())
                    if (jsonObject.getString("success")=="1"){
                        *//*val itemType = object : TypeToken<List<Language>>() {}.type
                        val itemList = gson.fromJson<List<Language>>(jsonObject.getJSONArray("data").toString(), itemType)
                        binding.recyclerView.adapter=
                            LanguageAdapter(this@ProjectActivity,itemList,this@ProjectActivity)*//*
                    }else{
                        Toast.makeText(this@ProjectActivity,jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                }
            }*/
        }
    }

    override fun onProjectSelect(project: ProjectWithLanguage) {
        preferenceManager.saveProject(project)
        Intent(this,UserRoleActivity::class.java).apply {
            startActivity(this)
        }
    }
}