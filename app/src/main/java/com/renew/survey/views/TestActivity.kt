package com.renew.survey.views

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renew.survey.R
import com.renew.survey.adapter.TestQuestionsAdapter
import com.renew.survey.databinding.ActivityTestBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.Options
import com.renew.survey.room.entities.TestOptionLanguage
import com.renew.survey.room.entities.TestQuestionLanguage
import com.renew.survey.utilities.UtilMethods
import kotlinx.coroutines.launch

class TestActivity : BaseActivity() {
    lateinit var binding:ActivityTestBinding
    var testquestionList: List<TestQuestionLanguage> = arrayListOf()
    lateinit var testquestionsAdapter: TestQuestionsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        testquestionsAdapter= TestQuestionsAdapter(this,testquestionList)
        binding.recyclerView.adapter=testquestionsAdapter
        getTestQuestions()
        binding.btnContinue.setOnClickListener {
            var count=0

            for (d in testquestionList){
                when(d.question_type){
                    "SINGLE_SELECT"->{
                        if (d.answers.isNotEmpty()){
                            if (d.answers[0].is_answer=="Y"){
                                count++
                            }
                        }
                    }
                    "NUMBER"->{
                        if (d.answer==d.answer2){
                            count++
                        }
                    }
                    "RADIO"->{
                        if (d.answers.isNotEmpty()){
                            if (d.answers[0].is_answer=="Y"){
                                count++
                            }
                        }
                    }
                    "CHECKBOX"->{
                        var wrongAns=0
                        var rightAns=0
                        for (x in d.answers){
                            if (x.is_answer=="N"){
                                wrongAns++
                            }else if (x.is_answer=="Y"){
                                rightAns++
                            }
                        }
                        if (wrongAns==0 && rightAns>0){
                            count++
                        }
                    }
                }
            }
            if (count < preferenceManager.getPassingMarks()) {
                ShowFailure(count)
            } else {
                Showsuccess(count,"${preferenceManager.getForm().tbl_forms_id}-${preferenceManager.getProject().id}-${preferenceManager.getUserId()}")
            }
            /*val res=gson.toJson(testquestionList)
            Log.e("testresult",res)*/
        }
    }
    private fun getTestQuestions() {
        lifecycleScope.launch {
            if (testquestionList.isEmpty()) {
                testquestionList = AppDatabase.getInstance(this@TestActivity).formDao().getAllTestQuestions(preferenceManager.getLanguage(), preferenceManager.getForm().tbl_forms_id)
            }
            testquestionList.forEachIndexed { index, testQuestionLanguage ->
                if (testQuestionLanguage.question_type=="CHECKBOX"||testQuestionLanguage.question_type=="SINGLE_SELECT"||testQuestionLanguage.question_type=="MULTI_SELECT"||testQuestionLanguage.question_type=="RADIO"){
                    val options= testQuestionLanguage.tbl_test_questions_id.let {
                        AppDatabase.getInstance(this@TestActivity).formDao().getAllTestOptions(it,preferenceManager.getLanguage())
                    } as ArrayList
                    if (testQuestionLanguage.question_type=="SINGLE_SELECT"){
                        options.add(0, TestOptionLanguage("N",0,0,0,0,getString(R.string.select)))
                    }
                    testquestionList[index].options=options
                }
            }
            Log.e("TAG", "getTestQuestions: ${gson.toJson(testquestionList)}" )
            testquestionsAdapter.setData(testquestionList)
        }
    }

    fun Showsuccess(count: Int, stringExtra: String?) {
        val dialogView = Dialog(this)
        dialogView.setContentView(R.layout.dialog_success)
        dialogView.setCancelable(false)
        val submit: TextView = dialogView.findViewById(R.id.btn_submit)
        val msg: TextView = dialogView.findViewById(R.id.success_tv)
        var listOfFragment= arrayListOf<String>()
        val retrievedList = preferenceManager.getTrainingState("trainingState")
        retrievedList?.forEach{
            listOfFragment.add(it)
        }
        listOfFragment.add(stringExtra!!)
        preferenceManager.saveTrainingState("trainingState",listOfFragment)
        msg.setText("You have successfully pass the test, \n Your obtained Marks :"+count.toString())
        submit.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
            dialogView.dismiss()
        }

        dialogView.show()
    }
    fun ShowFailure(count: Int) {
        val dialogView = Dialog(this)
        dialogView.setContentView(R.layout.dialog_failure)
        dialogView.setCancelable(false)
        val submit: TextView = dialogView.findViewById(R.id.btn_submit)
        val msg: TextView = dialogView.findViewById(R.id.success_tv)
        msg.setText("Test Failed, \n Your obtained Marks :"+count.toString())
        submit.setOnClickListener {
            val intent = Intent(this, TrainingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dialogView.dismiss()
        }

        dialogView.show()
    }
}