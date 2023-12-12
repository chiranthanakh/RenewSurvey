package com.renew.survey.views

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.renew.survey.R
import com.renew.survey.adapter.QuestionGroupAdapter
import com.renew.survey.databinding.ActivityFormsDetailsBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.QuestionGroupWithLanguage
import com.renew.survey.views.fragments.CommonQuestionFragment
import com.renew.survey.views.fragments.QuestionsFragment
import kotlinx.coroutines.launch
import java.lang.String
import java.util.Locale


class FormsDetailsActivity : BaseActivity() ,QuestionGroupAdapter.ClickListener{
    lateinit var binding: ActivityFormsDetailsBinding
    private var questionGroupList= listOf<QuestionGroupWithLanguage>()
    private var listOfFragment= arrayListOf<Fragment>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFormsDetailsBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)
        getAllQuestionGroup()
        binding.recyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        loadQuestionFragment(1)
        turnOnLocation()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationRequest = LocationRequest.create()
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest!!.setInterval(20 * 1000)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location!=null){
                        preferenceManager.saveLocation("${location.latitude},${location.longitude}")
                    }
                }
            }
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location!=null){
                    preferenceManager.saveLocation("${location.latitude},${location.longitude}")
                }
            }


    }
    fun getAllQuestionGroup(){
        lifecycleScope.launch {
            questionGroupList=AppDatabase.getInstance(this@FormsDetailsActivity).formDao().getAllFormsQuestionGroup(preferenceManager.getLanguage(),preferenceManager.getProject().id!!,preferenceManager.getForm().tbl_forms_id)
            Log.e("roomData","data=$questionGroupList")
            binding.recyclerView.adapter=QuestionGroupAdapter(this@FormsDetailsActivity,questionGroupList,this@FormsDetailsActivity)
            /*for (d in questionGroupList){
                val fragment=QuestionsFragment()
                supportFragmentManager.beginTransaction().add(R.id.container,fragment)
                listOfFragment.add(fragment)
            }*/
        }
    }

    override fun onQuestionGroupSelected(questionGroup: QuestionGroupWithLanguage,pos:Int) {
        loadQuestionFragment(questionGroup.mst_question_group_id)
    }

    private fun loadCommonFragment(){
        supportFragmentManager.beginTransaction().replace(R.id.container, CommonQuestionFragment()).commit();
    }
    private fun loadQuestionFragment(group:Int){
        supportFragmentManager.beginTransaction().replace(R.id.container, QuestionsFragment(group)).commit();
    }
    fun turnOnLocation(){
        try {
            val googleApiClient=GoogleApiClient.Builder(this).addApi(LocationServices.API).build()
            val req=LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
            req.setAlwaysShow(false)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}