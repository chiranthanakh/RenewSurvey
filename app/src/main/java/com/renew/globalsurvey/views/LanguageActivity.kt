package com.renew.globalsurvey.views

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renew.globalsurvey.adapter.LanguageAdapter
import com.renew.globalsurvey.databinding.ActivityLanguageBinding
import com.renew.globalsurvey.room.AppDatabase
import com.renew.globalsurvey.room.entities.LanguageEntity
import kotlinx.coroutines.launch
import java.util.Locale


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
        setLocale(this,language.symbol)
        proceed()
    }
    fun proceed(){
        Intent(this,ProjectActivity::class.java).apply {
            startActivity(this)
        }
    }
    fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())
    }

}