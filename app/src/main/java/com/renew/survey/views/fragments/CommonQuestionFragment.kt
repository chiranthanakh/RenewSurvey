package com.renew.survey.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.lifecycleScope
import com.renew.survey.adapter.DistrictSpinnerAdapter
import com.renew.survey.adapter.PanchayatSpinnerAdapter
import com.renew.survey.adapter.StateSpinnerAdapter
import com.renew.survey.adapter.TehsilSpinnerAdapter
import com.renew.survey.adapter.VillageSpinnerAdapter
import com.renew.survey.databinding.FragmentCommonQuestionBinding
import com.renew.survey.response.DistrictModel
import com.renew.survey.response.PanchayathModel
import com.renew.survey.response.StateModel
import com.renew.survey.response.TehsilModel
import com.renew.survey.response.VillageModel
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.DistrictEntity
import com.renew.survey.room.entities.PanchayathEntity
import com.renew.survey.room.entities.StatesEntity
import com.renew.survey.room.entities.TehsilEntity
import com.renew.survey.room.entities.VillageEntity
import kotlinx.coroutines.launch


class CommonQuestionFragment : Fragment() {
    lateinit var binding:FragmentCommonQuestionBinding
    var stateList= arrayListOf<StateModel>();
    var districtList= arrayListOf<DistrictModel>()
    var tehsilList= arrayListOf<TehsilModel>()
    var panchayathList= arrayListOf<PanchayathModel>()
    var villageList= arrayListOf<VillageModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentCommonQuestionBinding.inflate(inflater,container,false)
        getStateData()
        spinnerSelectors()

        return binding.root
    }
    fun getStateData(){
        lifecycleScope.launch {
            stateList=AppDatabase.getInstance(requireContext()).placesDao().getAllStates().transformState() as ArrayList<StateModel>
            stateList.add(0,StateModel("","Select State"))
            binding.spState.adapter=StateSpinnerAdapter(requireContext(),stateList)
        }
    }
    fun getDistrict(state: Int){
        lifecycleScope.launch {
            districtList=AppDatabase.getInstance(requireContext()).placesDao().getAllDistricts(state).transformDistrict() as ArrayList<DistrictModel>
            districtList.add(0, DistrictModel("Select District",""))
            binding.spDistrict.adapter=DistrictSpinnerAdapter(requireContext(),districtList)
        }
    }
    fun getTehsil(state: Int){
        lifecycleScope.launch {
            tehsilList=AppDatabase.getInstance(requireContext()).placesDao().getAllTehsils(state).transformTehsil() as ArrayList<TehsilModel>
            tehsilList.add(0,TehsilModel("","Select Tehsil"))
            binding.spTehsil.adapter=TehsilSpinnerAdapter(requireContext(),tehsilList)
        }
    }
    fun getPanchayath(state: Int){
        lifecycleScope.launch {
            panchayathList=AppDatabase.getInstance(requireContext()).placesDao().getAllPanchayath(state).transformPanchayath() as ArrayList<PanchayathModel>
            panchayathList.add(0, PanchayathModel("","Select Panchayath"))
            binding.spPanchayat.adapter=PanchayatSpinnerAdapter(requireContext(),panchayathList)
        }
    }
    fun getVillage(state: Int){
        lifecycleScope.launch {
            villageList=AppDatabase.getInstance(requireContext()).placesDao().getAllVillages(state).transformVillage() as ArrayList<VillageModel>
            villageList.add(0,VillageModel("","Select State"))
            binding.spVillage.adapter=VillageSpinnerAdapter(requireContext(),villageList)
        }
    }


    fun spinnerSelectors(){
        binding.spState.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getDistrict(stateList[p2].mst_state_id.toInt())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.spDistrict.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getTehsil(districtList[p2].mst_district_id.toInt())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.spTehsil.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getPanchayath(tehsilList[p2].mst_tehsil_id.toInt())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.spPanchayat.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2>0){
                    getVillage(panchayathList[p2].mst_panchayat_id.toInt())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

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