package com.renew.survey.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import com.renew.survey.response.Language
import com.renew.survey.adapter.LanguageAdapter
import com.renew.survey.databinding.ActivityLanguageBinding
import com.renew.survey.utilitys.ApiInterface
import kotlinx.coroutines.launch
import org.json.JSONObject

class LanguageActivity : BaseActivity() {
    lateinit var binding: ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        getLanguageData()

    }
    fun getLanguageData(){
        binding.progressLayout.visibility= View.VISIBLE
        lifecycleScope.launch {
            ApiInterface.getInstance()?.apply {
                val response= preferenceManager.getToken()?.let { getLanguage(it) }
                binding.progressLayout.visibility= View.GONE
                if (response!!.isSuccessful){
                    val jsonObject= JSONObject(response.body().toString())
                    if (jsonObject.getString("success")=="1"){
                        val itemType = object : TypeToken<List<Language>>() {}.type
                        val itemList = gson.fromJson<List<Language>>(jsonObject.getJSONArray("data").toString(), itemType)
                        binding.recyclerView.adapter=LanguageAdapter(this@LanguageActivity,itemList)
                    }else{
                        Toast.makeText(this@LanguageActivity,jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}