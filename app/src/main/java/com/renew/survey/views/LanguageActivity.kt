package com.renew.survey.views

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.renew.survey.R
import com.renew.survey.response.Language
import com.renew.survey.adapter.LanguageAdapter
import com.renew.survey.databinding.ActivityLanguageBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.LanguageEntity
import kotlinx.coroutines.launch

class LanguageActivity : BaseActivity() ,LanguageAdapter.ClickListener{
    lateinit var binding: ActivityLanguageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        getLanguageData()

    }
    fun getLanguageData(){
        lifecycleScope.launch {
            val languages=AppDatabase.getInstance(this@LanguageActivity).languageDao().getAll()
            binding.recyclerView.adapter=LanguageAdapter(this@LanguageActivity,languages,this@LanguageActivity)

           /* ApiInterface.getInstance()?.apply {
                val response= preferenceManager.getToken()?.let { getLanguage(it) }
                binding.progressLayout.visibility= View.GONE
                if (response!!.isSuccessful){
                    val jsonObject= JSONObject(response.body().toString())
                    if (jsonObject.getString("success")=="1"){
                        val itemType = object : TypeToken<List<Language>>() {}.type
                        val itemList = gson.fromJson<List<Language>>(jsonObject.getJSONArray("data").toString(), itemType)
                        binding.recyclerView.adapter=LanguageAdapter(this@LanguageActivity,itemList,this@LanguageActivity)
                    }else{
                        Toast.makeText(this@LanguageActivity,jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                }
            }*/
        }
    }

    override fun onLanguageSelected(language: LanguageEntity) {
        preferenceManager.saveLanguage(language.mst_language_id)
        proceed()
    }
    fun proceed(){
        Intent(this,ProjectActivity::class.java).apply {
            startActivity(this)
        }
    }

}