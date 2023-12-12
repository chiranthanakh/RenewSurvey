package com.renew.survey.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renew.survey.adapter.QuestionsAdapter
import com.renew.survey.databinding.FragmentQuestionsBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.FormQuestionLanguage
import com.renew.survey.room.entities.Options
import com.renew.survey.room.entities.QuestionGroupWithLanguage
import com.renew.survey.utilities.PreferenceManager
import kotlinx.coroutines.launch

class QuestionsFragment constructor(groupId: Int) : Fragment() ,QuestionsAdapter.ClickListener{

    lateinit var binding:FragmentQuestionsBinding
    var questionList= listOf<FormQuestionLanguage>()
    lateinit var prefsManager:PreferenceManager
    var group=groupId
    lateinit var questionsAdapter: QuestionsAdapter


    /*companion object{
        fun instance(group: Int): QuestionsFragment {
            val data = Bundle()
            data.putInt("group", group)
            return QuestionsFragment().apply{
                arguments = data
            }
        }
    }*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentQuestionsBinding.inflate(inflater,container,false)
        prefsManager=PreferenceManager(requireContext())
        getQuestions()
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        questionsAdapter= QuestionsAdapter(requireContext(),questionList,this)
        binding.recyclerView.adapter=questionsAdapter
        return binding.root
    }
    fun getQuestions(){
        lifecycleScope.launch {
            questionList=AppDatabase.getInstance(requireContext()).formDao().getAllFormsQuestions(prefsManager.getLanguage(), group)
            questionList.forEachIndexed { index, formQuestionLanguage ->
                if (formQuestionLanguage.question_type=="SINGLE_SELECT"||formQuestionLanguage.question_type=="MULTI_SELECT"||formQuestionLanguage.question_type=="RADIO"){
                    val options=AppDatabase.getInstance(requireContext()).formDao().getAllOptions(formQuestionLanguage.tbl_form_questions_id,prefsManager.getLanguage()) as ArrayList
                    Log.e("Options","data=$options")
                    if (formQuestionLanguage.question_type=="SINGLE_SELECT"){
                        options.add(0,Options("Select"))
                    }
                    questionList[index].options=options
                }
            }
            questionsAdapter.setData(questionList)
        }

    }

    override fun onQuestionGroupSelected(questionGroup: QuestionGroupWithLanguage, pos: Int) {

    }


}