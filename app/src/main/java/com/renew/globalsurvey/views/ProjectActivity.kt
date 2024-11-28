package com.renew.globalsurvey.views

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renew.globalsurvey.adapter.ProjectAdapter
import com.renew.globalsurvey.databinding.ActivityProjectBinding
import com.renew.globalsurvey.room.AppDatabase
import com.renew.globalsurvey.room.entities.ProjectWithLanguage
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
            val projects=AppDatabase.getInstance(this@ProjectActivity).formDao().getAllProjectsWithLanguage(preferenceManager.getLanguage())
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