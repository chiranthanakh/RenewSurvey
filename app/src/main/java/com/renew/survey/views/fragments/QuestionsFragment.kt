package com.renew.survey.views.fragments

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.renew.survey.R
import com.renew.survey.adapter.QuestionsAdapter
import com.renew.survey.databinding.FragmentQuestionsBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.FormQuestionLanguage
import com.renew.survey.room.entities.Options
import com.renew.survey.room.entities.QuestionGroupWithLanguage
import com.renew.survey.utilities.FileUtils
import com.renew.survey.utilities.PreferenceManager
import kotlinx.coroutines.launch

class QuestionsFragment constructor(val group: Int,val fragPos:Int, var questionGroupList: List<QuestionGroupWithLanguage>
) : Fragment() ,QuestionsAdapter.ClickListener{

    lateinit var binding:FragmentQuestionsBinding
    lateinit var prefsManager:PreferenceManager
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
        questionsAdapter= QuestionsAdapter(requireContext(),questionGroupList[fragPos].questions,this)
        binding.recyclerView.adapter=questionsAdapter
        return binding.root
    }
    fun getQuestions(){
        lifecycleScope.launch {
            if (questionGroupList[fragPos].questions.isEmpty()){
                questionGroupList[fragPos].questions=AppDatabase.getInstance(requireContext()).formDao().getAllFormsQuestions(prefsManager.getLanguage(), group,prefsManager.getForm().tbl_forms_id)
            }
            //questionList=AppDatabase.getInstance(requireContext()).formDao().getAllFormsQuestions(prefsManager.getLanguage(), group)
            questionGroupList[fragPos].questions.forEachIndexed { index, formQuestionLanguage ->
                if (formQuestionLanguage.question_type=="CHECKBOX"||formQuestionLanguage.question_type=="SINGLE_SELECT"||formQuestionLanguage.question_type=="MULTI_SELECT"||formQuestionLanguage.question_type=="RADIO"){
                    val options=AppDatabase.getInstance(requireContext()).formDao().getAllOptions(formQuestionLanguage.tbl_form_questions_id,prefsManager.getLanguage()) as ArrayList
                    if (formQuestionLanguage.question_type=="SINGLE_SELECT"){
                        options.add(0,Options(getString(R.string.select)))
                    }
                    questionGroupList[fragPos].questions[index].options=options
                }
            }
            val json= Gson().toJson(questionGroupList[fragPos])
            Log.e("Options","data=$json")
            questionsAdapter.setData(questionGroupList[fragPos].questions)
        }

    }
    var filePath = ""
    var fileType = ""
    var fileName = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            FROM_CAMERA->{
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val path = FileUtils.getRealPathFromURI(
                        requireContext(),
                        imageUri1
                    )
                    questionGroupList[fragPos].questions[position].answer=path
                    questionsAdapter.notifyItemChanged(position)
                }
            }
            PICKFILE_REQUEST_CODE->{
                if (data != null) {
                    val uri = data.data
                    filePath = FileUtils.getPathFromUri(context, uri)
                    questionGroupList[fragPos].questions[position].answer=filePath
                    questionsAdapter.notifyItemChanged(position)
                }
            }
        }

    }

    override fun onFileSelect(question: FormQuestionLanguage, pos: Int, type:String, capture:String) {
        position=pos
        //openCamera()
        if (capture=="CAPTURE"){
            openCamera()
        }else{
            openFilePick("*/*")
        }

        /*when(type){
            *//*"JPEG","JPG"->{
                openFilePick("image/jpeg")
            }
            "PNG"->{
                openFilePick("image/png")
            }
            "GIF"->{
                openFilePick("image/gif")
            }
            "PDF"->{
                openFilePick("application/pdf")
            }
            "XLS","DOC","XLSX"->{

            }*//*
        }*/
    }
    private fun openFilePick(type: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = type
        startActivityForResult(intent, PICKFILE_REQUEST_CODE)
    }
    val PICKFILE_REQUEST_CODE=43
    val FROM_CAMERA=49
    var position=0
    var imageUri1: Uri? = null
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri1 = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri1)
        startActivityForResult(intent, FROM_CAMERA)
    }


}