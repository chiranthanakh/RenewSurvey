package com.renew.survey.views

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.renew.survey.R
import com.renew.survey.adapter.AssignedSurveyAdapter
import com.renew.survey.adapter.DistrictSpinnerAdapter
import com.renew.survey.adapter.PanchayatSpinnerAdapter
import com.renew.survey.adapter.StateSpinnerAdapter
import com.renew.survey.adapter.TehsilSpinnerAdapter
import com.renew.survey.adapter.VillageSpinnerAdapter
import com.renew.survey.databinding.ActivitySurveySelectBinding
import com.renew.survey.response.DistrictModel
import com.renew.survey.response.PanchayathModel
import com.renew.survey.response.StateModel
import com.renew.survey.response.TehsilModel
import com.renew.survey.response.VillageModel
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.AssignedFilterSurveyEntity
import com.renew.survey.room.entities.DistrictEntity
import com.renew.survey.room.entities.PanchayathEntity
import com.renew.survey.room.entities.StatesEntity
import com.renew.survey.room.entities.TehsilEntity
import com.renew.survey.room.entities.VillageEntity
import com.renew.survey.utilities.PreferenceManager
import kotlinx.coroutines.launch


class SurveySelectActivity : BaseActivity() ,AssignedSurveyAdapter.ClickListener {
    lateinit var binding: ActivitySurveySelectBinding
    var villageList = arrayListOf<VillageModel>()
    var list = arrayListOf<AssignedFilterSurveyEntity>()
    var filteredlist = arrayListOf<AssignedFilterSurveyEntity>()

    var stateList= arrayListOf<StateModel>();
    var frequancy= arrayListOf<String>();
    var districtList= arrayListOf<DistrictModel>()
    var tehsilList= arrayListOf<TehsilModel>()
    var panchayathList= arrayListOf<PanchayathModel>()
    var villageid : Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.ivFilter.setOnClickListener {
            ShowFilter()
        }
        getData()
    }

    override fun onResume() {
        super.onResume()
        getData()

        binding.edtFilter.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                filterlocation(s)
                return false
            }
        })
    }

    private fun filterlocation(s: String) {
        filteredlist.clear()
        if (villageid == null) {
            list.forEach {
                if (!it.mst_village_name.isNullOrEmpty() && !it.mst_district_name.isNullOrEmpty() && !it.mst_state_name.isNullOrEmpty() && !it.mst_tehsil_name.isNullOrEmpty() && !it.mst_panchayat_name.isNullOrEmpty() ) {
                    if (it.mst_village_name?.lowercase()!!
                            .contains(s.lowercase()) || it.mst_district_name?.lowercase()!!
                            .contains(s.lowercase()) || it.mst_state_name?.lowercase()!!
                            .contains(s.lowercase()) ||
                        it.mst_tehsil_name?.lowercase()!!
                            .contains(s.lowercase()) || it.mst_panchayat_name?.lowercase()!!
                            .contains(s.lowercase()) || it.aadhar_card.lowercase()
                            .contains(s.lowercase()) || it.banficary_name.lowercase()
                            .contains(s.lowercase())
                    ) {
                        filteredlist.add(it)
                    }
                }
            }
            binding.recyclerView.adapter =
                AssignedSurveyAdapter(this@SurveySelectActivity, filteredlist, this@SurveySelectActivity)

        } else {
            list.forEach {
                if (!it.mst_village_name.isNullOrEmpty() && !it.mst_district_name.isNullOrEmpty() && !it.mst_state_name.isNullOrEmpty() && !it.mst_tehsil_name.isNullOrEmpty() && !it.mst_panchayat_name.isNullOrEmpty() ) {
                    if ((it.mst_village_name?.lowercase()!!
                            .contains(s.lowercase()) || it.mst_district_name?.lowercase()!!
                            .contains(s.lowercase()) || it.mst_state_name?.lowercase()!!
                            .contains(s.lowercase()) ||
                                it.mst_tehsil_name?.lowercase()!!
                                    .contains(s.lowercase()) || it.mst_panchayat_name?.lowercase()!!
                            .contains(s.lowercase()) || it.aadhar_card.lowercase()
                            .contains(s.lowercase()) ||
                                it.banficary_name.lowercase()
                                    .contains(s.lowercase())) && it.mst_village_id.toInt() == villageid
                    ) {
                        filteredlist.add(it)
                    }
                }
            }
            binding.recyclerView.adapter =
                AssignedSurveyAdapter(this@SurveySelectActivity, filteredlist, this@SurveySelectActivity)

        }
    }


    fun getData() {
        list.clear()
        lifecycleScope.launch {
             list = AppDatabase.getInstance(this@SurveySelectActivity).formDao()
                 .getAllFilteredAssignedSurvey(preferenceManager.getForm().tbl_forms_id, preferenceManager.getProject().tbl_projects_id) as ArrayList<AssignedFilterSurveyEntity>

            list.forEach {
//                Log.d("formDetails234", it.mst_village_id.toString())
                it.mst_village_name = AppDatabase.getInstance(this@SurveySelectActivity).placesDao().getVillage(it.mst_village_id)?.lowercase()
                it.mst_tehsil_name = AppDatabase.getInstance(this@SurveySelectActivity).placesDao()
                    .getTehsils(it.mst_tehsil_id)?.lowercase()
                it.mst_panchayat_name = it.mst_panchayat_id?.let { it1 ->
                    AppDatabase.getInstance(this@SurveySelectActivity).placesDao().getPanchayath(
                        it1
                    )?.lowercase()
                }
                it.mst_district_name =
                    AppDatabase.getInstance(this@SurveySelectActivity).placesDao()
                        .getDistricts(it.mst_district_id)?.lowercase()
                it.mst_state_name = AppDatabase.getInstance(this@SurveySelectActivity).placesDao()
                    .getStates(it.mst_state_id)?.lowercase()
            }
            Log.d("filteredList", list.toString())
            binding.recyclerView.adapter =
                AssignedSurveyAdapter(this@SurveySelectActivity, list, this@SurveySelectActivity)
        }
    }

    override fun onProjectSelect(assignedSurveyEntity: AssignedFilterSurveyEntity) {
        Intent(this, FormsDetailsActivity::class.java).apply {
            putExtra("assigned", gson.toJson(assignedSurveyEntity))
            startActivity(this)
        }
    }

    fun ShowFilter() {
        val dialogView = Dialog(this@SurveySelectActivity)
        dialogView.setContentView(R.layout.dialog_filter)
        dialogView.setCancelable(false)
        val cancel : ImageView = dialogView.findViewById(R.id.id_cancel)
         val state : Spinner = dialogView.findViewById(R.id.sp_state)
        val District : Spinner = dialogView.findViewById(R.id.sp_district)
       val Tehsil : Spinner = dialogView.findViewById(R.id.sp_tehsil)
        val Panchayath : Spinner = dialogView.findViewById(R.id.sp_panchayat)
       val Village : Spinner = dialogView.findViewById(R.id.sp_village)
        val submit : Button = dialogView.findViewById(R.id.button)
        val reset : Button = dialogView.findViewById(R.id.button2)

        cancel.setOnClickListener {
            dialogView.cancel()
            dialogView.dismiss()
        }

        submit.setOnClickListener {
            filterlocation("")
            dialogView.cancel()
            dialogView.dismiss()
        }

        reset.setOnClickListener {
            state.setSelection(0)
            District.setSelection(0)
            Tehsil.setSelection(0)
            Panchayath.setSelection(0)
            Village.setSelection(0)
            villageid = null
        }

        state.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    lifecycleScope.launch {
                        districtList=AppDatabase.getInstance(this@SurveySelectActivity).placesDao().getAllDistricts(stateList[p2].mst_state_id.toInt()).transformDistrict() as ArrayList<DistrictModel>
                        districtList.add(0, DistrictModel("Select District",""))
                        District.adapter= DistrictSpinnerAdapter(this@SurveySelectActivity,districtList)
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        District?.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    lifecycleScope.launch {
                        tehsilList=AppDatabase.getInstance(this@SurveySelectActivity).placesDao().getAllTehsils(districtList[p2].mst_district_id.toInt()).transformTehsil() as ArrayList<TehsilModel>
                        tehsilList.add(0, TehsilModel("","Select Tehsil"))
                        Tehsil.adapter= TehsilSpinnerAdapter(this@SurveySelectActivity,tehsilList)

                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        Tehsil.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    lifecycleScope.launch {
                        panchayathList=AppDatabase.getInstance(this@SurveySelectActivity).placesDao().getAllPanchayath(tehsilList[p2].mst_tehsil_id.toInt()).transformPanchayath() as ArrayList<PanchayathModel>
                        panchayathList.add(0, PanchayathModel("","Select Panchayath"))
                        Panchayath.adapter= PanchayatSpinnerAdapter(this@SurveySelectActivity,panchayathList)

                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        Panchayath.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    lifecycleScope.launch {
                        villageList=AppDatabase.getInstance(this@SurveySelectActivity).placesDao().getAllVillages(panchayathList[p2].mst_panchayat_id.toInt()).transformVillage() as ArrayList<VillageModel>
                        villageList.add(0, VillageModel("","Select Village"))
                        Village?.adapter= VillageSpinnerAdapter(this@SurveySelectActivity,villageList)
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        Village.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    villageid = villageList[p2].mst_villages_id.toInt()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        lifecycleScope.launch {
            stateList=AppDatabase.getInstance(this@SurveySelectActivity).placesDao().getAllStates().transformState() as ArrayList<StateModel>
            stateList.add(0, StateModel("","Select State"))
            state.adapter= StateSpinnerAdapter(this@SurveySelectActivity,stateList)
        }

        dialogView.show()
    }

    fun List<StatesEntity>.transformState(): List<StateModel> {
        return this.map {
            it.transform1()
        }
    }
    fun StatesEntity.transform1(): StateModel {
        this.apply {
            return StateModel(
                mst_state_id.toString(), state_name
            )
        }
    }
    fun List<DistrictEntity>.transformDistrict(): List<DistrictModel> {
        return this.map {
            it.transform1()
        }
    }
    fun DistrictEntity.transform1(): DistrictModel {
        this.apply {
            return DistrictModel(
                district_name,mst_district_id.toString()
            )
        }
    }

    fun List<TehsilEntity>.transformTehsil(): List<TehsilModel> {
        return this.map {
            it.transform1()
        }
    }
    fun TehsilEntity.transform1(): TehsilModel {
        this.apply {
            return TehsilModel(
                mst_tehsil_id.toString(),tehsil_name
            )
        }
    }

    fun List<PanchayathEntity>.transformPanchayath(): List<PanchayathModel> {
        return this.map {
            it.transform()
        }
    }
    fun PanchayathEntity.transform(): PanchayathModel {
        this.apply {
            return PanchayathModel(
                mst_panchayat_id.toString(),panchayat_name
            )
        }
    }

    fun List<VillageEntity>.transformVillage(): List<VillageModel> {
        return this.map {
            it.transform()
        }
    }
    fun VillageEntity.transform(): VillageModel {
        this.apply {
            return VillageModel(
                mst_village_id.toString(),village_name
            )
        }
    }
}