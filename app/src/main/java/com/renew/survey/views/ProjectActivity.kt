package com.renew.survey.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renew.survey.adapter.ProjectAdapter
import com.renew.survey.databinding.ActivityProjectBinding
import com.renew.survey.response.Project
import com.renew.survey.utilities.ApiInterface
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProjectActivity : BaseActivity() ,ProjectAdapter.ClickListener{
    lateinit var binding: ActivityProjectBinding
    val list= arrayListOf<Project>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        binding.recyclerView.adapter=ProjectAdapter(this,list,this)
        //getProjectData()

       //val intent = Intent(this@ProjectActivity, UserRoleActivity::class.java)
        //            startActivity(intent)

    }
    fun getProjectData(){
        binding.progressLayout.visibility= View.VISIBLE
        lifecycleScope.launch {
            ApiInterface.getInstance()?.apply {
                val response= getProjects(preferenceManager.getToken()!!,preferenceManager.getUserId()!!,preferenceManager.getLanguage()!!)
                binding.progressLayout.visibility= View.GONE
                if (response!!.isSuccessful){
                    val jsonObject= JSONObject(response.body().toString())
                    if (jsonObject.getString("success")=="1"){
                        /*val itemType = object : TypeToken<List<Language>>() {}.type
                        val itemList = gson.fromJson<List<Language>>(jsonObject.getJSONArray("data").toString(), itemType)
                        binding.recyclerView.adapter=
                            LanguageAdapter(this@ProjectActivity,itemList,this@ProjectActivity)*/
                    }else{
                        Toast.makeText(this@ProjectActivity,jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onProjectSelect(project: Project) {
        Intent(this,UserRoleActivity::class.java).apply {
            startActivity(this)
        }
    }
}