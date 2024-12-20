package com.renew.globalsurvey.views.fragments

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.renew.globalsurvey.R
import com.renew.globalsurvey.adapter.QuestionsAdapter
import com.renew.globalsurvey.adapter.TestQuestionsAdapter
import com.renew.globalsurvey.databinding.FragmentQuestionsBinding
import com.renew.globalsurvey.helper.compressor.Compressor
import com.renew.globalsurvey.room.AppDatabase
import com.renew.globalsurvey.room.entities.FormQuestionLanguage
import com.renew.globalsurvey.room.entities.Options
import com.renew.globalsurvey.room.entities.QuestionGroupWithLanguage
import com.renew.globalsurvey.room.entities.TestQuestionLanguage
import com.renew.globalsurvey.utilities.FileUtils
import com.renew.globalsurvey.utilities.PreferenceManager
import com.renew.globalsurvey.utilities.UtilMethods
import com.renew.globalsurvey.views.DashboardActivity
import com.renew.globalsurvey.views.FormsDetailsActivity
import com.renew.globalsurvey.views.TrainingActivity
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import kotlin.math.roundToInt


class QuestionsFragment(
    val group: Int,
    val fragPos: Int,
    var questionGroupList: List<QuestionGroupWithLanguage>,
    var testquestionList: List<TestQuestionLanguage>,
    val status: Int,
    val isTraingForm: Boolean
) : Fragment() ,QuestionsAdapter.ClickListener, FormsDetailsActivity.ClickListener {

    lateinit var binding:FragmentQuestionsBinding
    lateinit var prefsManager:PreferenceManager
    lateinit var questionsAdapter: QuestionsAdapter
    lateinit var testquestionsAdapter: TestQuestionsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentQuestionsBinding.inflate(inflater,container,false)
        prefsManager=PreferenceManager(requireContext())
        if (isTraingForm) {
            getTestQuestions()
            binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
            testquestionsAdapter= TestQuestionsAdapter(requireContext(),testquestionList)
            binding.recyclerView.adapter=testquestionsAdapter
        } else {
            Log.d("QuestionQuairy", status.toString())

            if (status==4||status==5||status==6||status==8) {
                getQuestionsWithDraftAnswer()
            } else {
                getQuestions()
            }
            binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
            questionsAdapter= QuestionsAdapter(requireContext(),questionGroupList[fragPos].questions,this)
            binding.recyclerView.adapter=questionsAdapter
        }
        return binding.root
    }

    private fun getTestQuestions() {
        /*lifecycleScope.launch {
            if (testquestionList.isEmpty()) {
                testquestionList = AppDatabase.getInstance(requireContext()).formDao().getAllTestQuestions(prefsManager.getLanguage(), prefsManager.getForm().tbl_forms_id)
            }
            testquestionList.forEachIndexed { index, testQuestionLanguage ->
                if (testQuestionLanguage.question_type=="CHECKBOX"||testQuestionLanguage.question_type=="SINGLE_SELECT"||testQuestionLanguage.question_type=="MULTI_SELECT"||testQuestionLanguage.question_type=="RADIO"){
                    val options= testQuestionLanguage?.tbl_test_questions_id?.let {
                        AppDatabase.getInstance(requireContext()).formDao().getAllOptions(
                            it,prefsManager.getLanguage())
                    } as ArrayList
                    if (testQuestionLanguage.question_type=="SINGLE_SELECT"){
                        options.add(0,Options(getString(R.string.select)))
                    }
                    testquestionList[index].options=options
                }
            }
            testquestionsAdapter.setData(testquestionList)
        }*/
    }

    fun getQuestions(){
        lifecycleScope.launch {
            if (questionGroupList[fragPos].questions.isEmpty()){
                questionGroupList[fragPos].questions=AppDatabase.getInstance(requireContext()).formDao().getAllFormsQuestions(prefsManager.getLanguage(), group,prefsManager.getForm().tbl_forms_id,prefsManager.getProject().tbl_projects_id)
                //questionGroupList[fragPos].questions=AppDatabase.getInstance(requireContext()).formDao().getAllTestQuestions(prefsManager.getLanguage(), 1)
            }
            val questionList=AppDatabase.getInstance(requireContext()).formDao().getAllTestQuestions(prefsManager.getLanguage(), prefsManager.getForm().tbl_forms_id)
            Log.d("QuestionQuairy", prefsManager.getLanguage().toString()+"--"+group.toString()+"--"+prefsManager.getForm().tbl_forms_id.toString()+"--"+prefsManager.getProject().tbl_projects_id.toString())
            questionGroupList[fragPos].questions.forEachIndexed { index, formQuestionLanguage ->
                if (formQuestionLanguage.question_type=="CHECKBOX"||formQuestionLanguage.question_type=="SINGLE_SELECT"||formQuestionLanguage.question_type=="MULTI_SELECT"||formQuestionLanguage.question_type=="RADIO"){
                    val options= formQuestionLanguage?.tbl_form_questions_id?.let {
                        AppDatabase.getInstance(requireContext()).formDao().getAllOptions(
                            it,prefsManager.getLanguage())
                    } as ArrayList
                    if (formQuestionLanguage.question_type=="SINGLE_SELECT"){
                        options.add(0,Options(getString(R.string.select),false))
                    }
                    questionGroupList[fragPos].questions[index].options=options
                }
            }
            val json= Gson().toJson(questionGroupList[fragPos])
            Log.e("Options123",questionGroupList[1].questions.toString())
            questionsAdapter.setData(questionGroupList[fragPos].questions)
        }

    }
    fun getQuestionsWithDraftAnswer(){
        lifecycleScope.launch {
            //Log.e("OptionsDraft","draftAnswer=${prefsManager.getDraft()}   language=${prefsManager.getLanguage()} formId=${prefsManager.getForm().tbl_forms_id} group=${group}")
            if (questionGroupList[fragPos].questions.isEmpty()){
                questionGroupList[fragPos].questions=AppDatabase.getInstance(requireContext()).formDao().getAllFormsQuestionsWithDraftAnswer(prefsManager.getLanguage(), group,prefsManager.getForm().tbl_forms_id,prefsManager.getDraft(),prefsManager.getProject().tbl_projects_id)
            }
            Log.e("DraftAnswer","${questionGroupList[fragPos].questions}")
            //questionList=AppDatabase.getInstance(requireContext()).formDao().getAllFormsQuestions(prefsManager.getLanguage(), group)
            questionGroupList[fragPos].questions.forEachIndexed { index, formQuestionLanguage ->
                if (formQuestionLanguage.question_type=="CHECKBOX"||formQuestionLanguage.question_type=="SINGLE_SELECT"||formQuestionLanguage.question_type=="MULTI_SELECT"||formQuestionLanguage.question_type=="RADIO"){
                    val options=AppDatabase.getInstance(requireContext()).formDao().getAllOptions(
                        formQuestionLanguage.tbl_form_questions_id!!,prefsManager.getLanguage()) as ArrayList
                    if (formQuestionLanguage.question_type=="SINGLE_SELECT"){
                        options.add(0,Options(getString(R.string.select),false))
                    }
                    questionGroupList[fragPos].questions[index].options=options
                }
            }
            if (fragPos==1){
                val json= Gson().toJson(questionGroupList[fragPos])
                Log.e("Options","data=$json")
            }
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
                    val path = FileUtils.getRealPathFromURI(requireContext(), imageUri1)
                    val bmOptions = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeFile(path, bmOptions)
                    val scaledBitmap=UtilMethods.getResizedBitmap(bitmap,1500,1500)
                    val bmTimeStamp=drawTextToBitmap(scaledBitmap,18,UtilMethods.getFormattedDate(Date(),"dd-MM-yyyy HH:mm:ss"))
                    val newPath=saveImageToExternal(bmTimeStamp,requireContext())
                    lifecycleScope.launch {
                        val compressed=Compressor.compress(requireContext(),File(newPath!!.path))
                        questionGroupList[fragPos].questions[position].answer= compressed.path
                        //questionsAdapter.notifyItemChanged(position)
                        questionsAdapter.notifyDataSetChanged()

                        deleteFile(path)
                    }
                }
            }
            PICKFILE_REQUEST_CODE->{
                if (data != null) {
                    val uri = data.data
                    filePath = FileUtils.getPathFromUri(context, uri)
                    var test = filePath.split("/")
                    Log.d("filePickPath",test.get(test.size-1))
                    questionGroupList[fragPos].questions[position].answer=filePath
                //    questionsAdapter.notifyItemChanged(position)
                    questionsAdapter.notifyDataSetChanged()

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

    private fun drawTextToBitmap(bitmap: Bitmap, textSize: Int = 78, text: String): Bitmap {
        val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        // new antialised Paint - empty constructor does also work
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED

        // text size in pixels
        val scale = requireContext().resources.displayMetrics.density
        paint.textSize = (textSize * scale).roundToInt().toFloat()

        //custom fonts or a default font
        val fontFace = ResourcesCompat.getFont(requireContext(), R.font.opensans_bold)
        paint.typeface = Typeface.create(fontFace, Typeface.NORMAL)


        // draw text to the Canvas center
        val bounds = Rect()
        //draw the text
        paint.getTextBounds(text, 0, text.length, bounds)
        val height: Float = paint.measureText("yY")

        //x and y defines the position of the text, starting in the top left corner
        canvas.drawText(text, 20F, height+15F, paint)
        return mutableBitmap
    }

    @Throws(IOException::class)
    fun saveImageToExternal(bm: Bitmap, context: Context?): Uri? {
        // Create Path to save Image
        val directory = requireContext().filesDir.path
        val path = File(directory) // Creates app specific folder
        if (!path.exists()) {
            if (!path.mkdirs()) {
                Toast.makeText(context, "Unable to create directory", Toast.LENGTH_SHORT).show()
            }
        }
        val imageFile = File(path,  UtilMethods.getFormattedDate(Date(),"yyyyMMddHHmmssSSS") + ".jpg") // Imagename.png
        val out = FileOutputStream(imageFile)
        return try {
            // bm = Bitmap.createScaledBitmap(bm, 600, 400, false);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out) // Compress Image
            out.flush()
            out.close()
            Uri.fromFile(imageFile)
        } catch (e: Exception) {
            throw IOException()
        }
    }
    fun deleteFile(path: String){
        try {
            val file: File = File(path)
            file.delete()
            if (file.exists()) {
                file.canonicalFile.delete()
                if (file.exists()) {
                    requireContext().deleteFile(file.name)
                }
            }
        }catch (e:Exception){
            e.toString()
        }

    }
    fun Showsuccess(count: Int, stringExtra: String?) {
        val dialogView = Dialog(requireContext())
        dialogView.setContentView(R.layout.dialog_success)
        dialogView.setCancelable(false)
        val submit: TextView = dialogView.findViewById(R.id.btn_submit)
        val msg: TextView = dialogView.findViewById(R.id.success_tv)
        var listOfFragment= arrayListOf<String>()
        val retrievedList = prefsManager.getTrainingState("trainingState")
        retrievedList?.forEach{
            listOfFragment.add(it)
        }
        listOfFragment.add(stringExtra!!)
         prefsManager.saveTrainingState("trainingState",listOfFragment)
        msg.setText("You have successfully pass the test, \n Your obtained Marks :"+count.toString())
        submit.setOnClickListener {
            val intent = Intent(requireContext(), DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dialogView.dismiss()
        }

        dialogView.show()
    }
    fun ShowFailure(count: Int) {
        val dialogView = Dialog(requireContext())
        dialogView.setContentView(R.layout.dialog_failure)
        dialogView.setCancelable(false)
        val submit: TextView = dialogView.findViewById(R.id.btn_submit)
        val msg: TextView = dialogView.findViewById(R.id.success_tv)
        msg.setText("Test Failed, \n Your obtained Marks :"+count.toString())
        submit.setOnClickListener {
            val intent = Intent(requireContext(), TrainingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dialogView.dismiss()
        }

        dialogView.show()
    }

    override fun onSubmit(stringExtra: String?) {
        var count : Int = 0
        testquestionList.forEach{
            if (it.answer == it.answer2) {
                count++
            }
        }
        if (count < prefsManager.getPassingMarks()) {
            ShowFailure(count)
        } else {
            Showsuccess(count,stringExtra)
        }
    }
}