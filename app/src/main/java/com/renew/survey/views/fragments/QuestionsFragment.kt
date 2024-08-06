package com.renew.survey.views.fragments

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
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mapbox.mapboxsdk.geometry.LatLng
import com.renew.survey.R
import com.renew.survey.adapter.QuestionsAdapter
import com.renew.survey.adapter.TestQuestionsAdapter
import com.renew.survey.databinding.FragmentQuestionsBinding
import com.renew.survey.helper.compressor.Compressor
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.DynamicAnswersEntity
import com.renew.survey.room.entities.FormQuestionLanguage
import com.renew.survey.room.entities.Options
import com.renew.survey.room.entities.QuestionGroupWithLanguage
import com.renew.survey.room.entities.TestQuestionLanguage
import com.renew.survey.utilities.FileUtils
import com.renew.survey.utilities.LatLagWrapper
import com.renew.survey.utilities.MyDiffUtilCallback
import com.renew.survey.utilities.PreferenceManager
import com.renew.survey.utilities.UtilMethods
import com.renew.survey.views.DashboardActivity
import com.renew.survey.views.FormsDetailsActivity
import com.renew.survey.views.MapManagerActivity
import com.renew.survey.views.TrainingActivity
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import kotlin.math.roundToInt


class QuestionsFragment(
    val group: Int,
    val fragPos: Int,
    var questionGroupList: MutableList<QuestionGroupWithLanguage> = mutableListOf(),
    var testquestionList: List<TestQuestionLanguage>,
    val status: Int,
    val isTraingForm: Boolean
) : Fragment(), QuestionsAdapter.ClickListener, FormsDetailsActivity.ClickListener {

    lateinit var binding: FragmentQuestionsBinding
    lateinit var prefsManager: PreferenceManager
    lateinit var questionsAdapter: QuestionsAdapter
    lateinit var testquestionsAdapter: TestQuestionsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        prefsManager = PreferenceManager(requireContext())
        if (isTraingForm) {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            testquestionsAdapter = TestQuestionsAdapter(requireContext(), testquestionList)
            binding.recyclerView.adapter = testquestionsAdapter
        } else {
            Log.d("QuestionQuairy", status.toString())
            if (status == 4 || status == 5 || status == 6 || status == 8 || status == 9) {
                getQuestionsWithDraftAnswer()
            } else {
                getQuestions()
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            questionsAdapter =
                QuestionsAdapter(requireContext(), questionGroupList[fragPos].questions, this)
            binding.recyclerView.adapter = questionsAdapter
        }
        return binding.root
    }

    fun getQuestions() {
        lifecycleScope.launch {
            if (questionGroupList[fragPos].questions.isEmpty()) {
                questionGroupList[fragPos].questions =
                    AppDatabase.getInstance(requireContext()).formDao().getAllFormsQuestions(
                        prefsManager.getLanguage(),
                        group,
                        prefsManager.getForm().tbl_forms_id,
                        prefsManager.getProject().tbl_projects_id,
                        prefsManager.getProject().mst_divisions_id,
                        prefsManager.getProject().mst_categories_id
                    )
            }
            Log.d("checkfullquestions", questionGroupList[fragPos].questions.toString())
            AppDatabase.getInstance(requireContext()).formDao().getAllTestQuestions(
                prefsManager.getLanguage(),
                prefsManager.getForm().tbl_forms_id
            )
            // Log.d("QuestionQuairy", prefsManager.getLanguage().toString()+"--"+group.toString()+"--"+prefsManager.getForm().tbl_forms_id.toString()+"--"+prefsManager.getProject().tbl_projects_id.toString())
            questionGroupList[fragPos].questions.forEachIndexed { index, formQuestionLanguage ->
                if (formQuestionLanguage.question_type == "CHECKBOX" || formQuestionLanguage.question_type == "SINGLE_SELECT" || formQuestionLanguage.question_type == "MULTI_SELECT" || formQuestionLanguage.question_type == "RADIO") {
                    val options = formQuestionLanguage.tbl_form_questions_id.let {
                        AppDatabase.getInstance(requireContext()).formDao()
                            .getAllOptions(it, prefsManager.getLanguage())
                    } as ArrayList
                    if (formQuestionLanguage.question_type == "SINGLE_SELECT") {
                        options.add(0, Options(getString(R.string.select), false))
                    }
                    questionGroupList[fragPos].questions[index].options = options
                }
            }
            Gson().toJson(questionGroupList[fragPos])
            Log.e("Options123", questionGroupList[1].questions.toString())
            questionsAdapter.setData(questionGroupList[fragPos].questions)
        }
    }

    private fun assignData(
        loopDependentQuestion: MutableList<FormQuestionLanguage>,
        j: Int,
        type : String
    ): FormQuestionLanguage {
        val allowedFileType = loopDependentQuestion[j].allowed_file_type
        val format = loopDependentQuestion[j].format
        val isMandatory = loopDependentQuestion[j].is_mandatory
        val isSpecialCharAllowed = loopDependentQuestion[j].is_special_char_allowed
        val isValidationRequired = loopDependentQuestion[j].is_validation_required
        val maxFileSize = loopDependentQuestion[j].max_file_size
        val maxLength = loopDependentQuestion[j].max_length
        val minLength = loopDependentQuestion[j].min_length
        val mstQuestionGroupId = loopDependentQuestion[j].mst_question_group_id
        val orderBy = loopDependentQuestion[j].order_by
        val questionType = loopDependentQuestion[j].question_type
        val tblFormQuestionsId = loopDependentQuestion[j].tbl_form_questions_id
        val title = loopDependentQuestion[j].title
        val answer = loopDependentQuestion[j].answer
        val hasDependencyQuestion = if (type == "loop"){
             loopDependentQuestion[j].has_dependancy_question+"--"+type }else{
             loopDependentQuestion[j].has_dependancy_question }
        val parentQuestionId = loopDependentQuestion[j].parent_question_id
        val newFormQuestionLanguage = FormQuestionLanguage(
            id = null, // Assuming id is nullable and you want to set it to null
            allowed_file_type = allowedFileType,
            format = format,
            is_mandatory = isMandatory,
            is_special_char_allowed = isSpecialCharAllowed,
            is_validation_required = isValidationRequired,
            max_file_size = maxFileSize,
            max_length = maxLength,
            min_length = minLength,
            mst_question_group_id = mstQuestionGroupId,
            order_by = orderBy,
            question_type = questionType,
            tbl_form_questions_id = tblFormQuestionsId,
            title = title,
            answer = answer,
            has_dependancy_question = hasDependencyQuestion,
            parent_question_id = parentQuestionId,
            options = listOf() // Initialize options list as empty for now
        )
        return newFormQuestionLanguage
    }

    fun getQuestionsWithDraftAnswer() {
        lifecycleScope.launch {
            val tempList = ArrayList<FormQuestionLanguage>()
            var indexloop : Int = 0
            //Log.e("OptionsDraft","draftAnswer=${prefsManager.getDraft()}   language=${prefsManager.getLanguage()} formId=${prefsManager.getForm().tbl_forms_id} group=${group}")
            if (questionGroupList[fragPos].questions.isEmpty()) {
                questionGroupList[fragPos].questions =
                    AppDatabase.getInstance(requireContext()).formDao()
                        .getAllFormsQuestionsWithDraftAnswer(
                            prefsManager.getLanguage(),
                            group,
                            prefsManager.getForm().tbl_forms_id,
                            prefsManager.getDraft(),
                            prefsManager.getProject().tbl_projects_id
                        )
            }
            questionGroupList[fragPos].questions.forEachIndexed { index, formQuestionLanguage ->
                if (formQuestionLanguage.question_type == "CHECKBOX" || formQuestionLanguage.question_type == "SINGLE_SELECT" ||
                    formQuestionLanguage.question_type == "MULTI_SELECT" || formQuestionLanguage.question_type == "RADIO"
                ) {
                    Log.d("checkOptionIdes", formQuestionLanguage.toString())
                    val options = AppDatabase.getInstance(requireContext()).formDao().getAllOptions(
                        formQuestionLanguage.tbl_form_questions_id, prefsManager.getLanguage()
                    ) as ArrayList
                    if (formQuestionLanguage.question_type == "SINGLE_SELECT") {
                        options.add(0, Options(getString(R.string.select), false))
                    }
                    questionGroupList[fragPos].questions[index].options = options
                } else if (formQuestionLanguage.question_type == "LOOP") {
                    indexloop = index
                    var loopDependentQuestion =
                        AppDatabase.getInstance(requireContext()).formDao().getLoopFormsQuestions(
                            prefsManager.getLanguage(),
                            group,
                            prefsManager.getForm().tbl_forms_id,
                            prefsManager.getProject().tbl_projects_id,
                            prefsManager.getProject().mst_divisions_id,
                            prefsManager.getProject().mst_categories_id,
                            formQuestionLanguage.tbl_form_questions_id
                        )

                    if (loopDependentQuestion.isNotEmpty() && !formQuestionLanguage.answer.isNullOrEmpty() && formQuestionLanguage.answer!!.isDigitsOnly()) {
                            for (i in 1..formQuestionLanguage.answer!!.toInt()) {
                                for (j in 0..loopDependentQuestion.size - 1) {
                                Log.d("checkOptionIdes", formQuestionLanguage.toString())

                                val dynamicAns = AppDatabase.getInstance(requireContext()).formDao()
                                    .getDynamicLoopAns(
                                        prefsManager.getDraft(),
                                        (loopDependentQuestion.get(j).tbl_form_questions_id.toString() + i.toString()).toInt()
                                    )
                                val data = assignData(loopDependentQuestion, j, "loop")

                                if (loopDependentQuestion.get(j).question_type == "CHECKBOX" ||
                                    loopDependentQuestion.get(j).question_type == "SINGLE_SELECT" ||
                                    loopDependentQuestion.get(j).question_type == "MULTI_SELECT" ||
                                    loopDependentQuestion.get(j).question_type == "RADIO"
                                ) {
                                    Log.d("checkOptionIdes", formQuestionLanguage.toString())
                                    val options =
                                        AppDatabase.getInstance(requireContext()).formDao()
                                            .getAllOptions(
                                                loopDependentQuestion.get(j).tbl_form_questions_id,
                                                prefsManager.getLanguage()
                                            ) as ArrayList
                                    data.options = options
                                }
                                if (dynamicAns != null) {
                                    if (!dynamicAns.answer?.isNullOrEmpty()!!) {
                                        data.answer = dynamicAns.answer
                                        // questionGroupList[fragPos].questions.add(data as FormQuestionLanguage)
                                    }
                                    tempList.add(data)
                                }
                            }
                        }
                    }
                }
            }
            if (tempList.size != 0) {
                questionGroupList[fragPos].questions.addAll(indexloop + 1, tempList)
            }
            if (fragPos == 1) {
                val json = Gson().toJson(questionGroupList[fragPos])
                Log.e("Options", "data=$json")
            }
            questionsAdapter.setData(questionGroupList[fragPos].questions)
        }
    }

    var filePath = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FROM_CAMERA -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val path = FileUtils.getRealPathFromURI(requireContext(), imageUri1)
                    val bmOptions = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeFile(path, bmOptions)
                    val scaledBitmap = UtilMethods.getResizedBitmap(bitmap, 1500, 1500)
                    val bmTimeStamp = drawTextToBitmap(
                        scaledBitmap,
                        18,
                        UtilMethods.getFormattedDate(Date(), "dd-MM-yyyy HH:mm:ss")
                    )
                    val newPath = saveImageToExternal(bmTimeStamp, requireContext())
                    lifecycleScope.launch {
                        val compressed = Compressor.compress(requireContext(), File(newPath!!.path))
                        questionGroupList[fragPos].questions[position].answer = compressed.path
                       // questionsAdapter.notifyItemChanged(position)
                        questionsAdapter.notifyDataSetChanged()
                        deleteFile(path)
                    }
                }
            }

            PICKFILE_REQUEST_CODE -> {
                if (data != null) {
                    val uri = data.data
                    filePath = FileUtils.getPathFromUri(context, uri)
                    var test = filePath.split("/")
                    Log.d("filePickPath", test.get(test.size - 1))
                    questionGroupList[fragPos].questions[position].answer = filePath
                   // questionsAdapter.notifyItemChanged(position)
                    questionsAdapter.notifyDataSetChanged()

                }
            }

            FROM_MAP -> {
                var points: MutableList<LatLng> = mutableListOf()
                if (data != null) {
                    val wrappedPoints = data.getParcelableArrayListExtra<LatLagWrapper>("key")
                    val formattedList = wrappedPoints?.let {
                        it.map {
                            "\"${String.format("%.4f", it.latitude)},${
                                String.format(
                                    "%.4f",
                                    it.longitude
                                )
                            }\""
                        }
                    }
                    Log.d("MapPointsCo", formattedList.toString())
                    questionGroupList[fragPos].questions[position].answer =
                        formattedList?.toString()
                    questionsAdapter.notifyItemChanged(position)
                }
            }
        }
    }

    override fun onFileSelect(
        question: FormQuestionLanguage,
        pos: Int,
        type: String,
        capture: String
    ) {
        position = pos
        if (capture == "CAPTURE") {
            openCamera()
        } else if (capture == "MAP") {
            val intent = Intent(requireActivity(), MapManagerActivity::class.java)
            startActivityForResult(intent, FROM_MAP)
        } else {
            openFilePick("*/*")
        }
    }

    override fun onLoopSelect(pos: Int,question: FormQuestionLanguage, answer: String) {
        lifecycleScope.launch {
            Log.e("LoopDuplicate", "data=$questionGroupList[fragPos].questions")
            questionGroupList[fragPos].questions.removeAll {
                it.parent_question_id == question.tbl_form_questions_id
            }
            val tempList = ArrayList<FormQuestionLanguage>()
            questionsAdapter.notifyDataSetChanged()
            var loopDependentQuestion =
                AppDatabase.getInstance(requireContext()).formDao().getLoopFormsQuestions(
                    prefsManager.getLanguage(),
                    group,
                    prefsManager.getForm().tbl_forms_id,
                    prefsManager.getProject().tbl_projects_id,
                    prefsManager.getProject().mst_divisions_id,
                    prefsManager.getProject().mst_categories_id,
                    question.tbl_form_questions_id
                )

            loopDependentQuestion.forEach {
                AppDatabase.getInstance(requireContext()).formDao()
                    .deleteLoopAns(it.tbl_form_questions_id.toString(), prefsManager.getDraft())
            }
            if (answer.isNotEmpty()) {

                for (i in 1..answer.toInt()) {

                    for (j in 0 until loopDependentQuestion.size) {
                        val baseData = assignData(loopDependentQuestion, j,"loop")
                        val questionid = baseData.tbl_form_questions_id
                        val data = baseData.copy()
                        data.tbl_form_questions_id = "${baseData.tbl_form_questions_id}$i".toInt()
                        if (data.question_type == "CHECKBOX" || data.question_type == "SINGLE_SELECT" ||
                            data.question_type == "MULTI_SELECT" || data.question_type == "RADIO"
                        ) {
                            val options = AppDatabase.getInstance(requireContext()).formDao()
                                .getAllOptions(questionid, prefsManager.getLanguage())
                            data.options = options

                            if (data.question_type == "SINGLE_SELECT") {
                                options.add(0, Options(getString(R.string.select), false))
                            }
                        }
                        tempList.add(data)
                    }
                }
                questionGroupList[fragPos].questions.addAll(pos+1,tempList)
                questionsAdapter.notifyItemRangeInserted(pos+1, tempList.size)
               questionsAdapter.notifyDataSetChanged()
               /*binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                questionsAdapter =
                    QuestionsAdapter(requireContext(), questionGroupList[fragPos].questions, questionsAdapter.clickListener)
                binding.recyclerView.adapter = questionsAdapter*/
                loopDependentQuestion.forEach {
                    for (i in 1..answer.toInt()) {
                        val dynamicAnswersEntity = DynamicAnswersEntity(
                            null,
                            it.mst_question_group_id,
                            "",/*it.tbl_form_questions_id*/
                            (it.tbl_form_questions_id.toString() + i.toString()),
                            prefsManager.getDraft(),
                            it.tbl_form_questions_id.toString()
                        )
                        AppDatabase.getInstance(requireContext()).formDao()
                            .insertDynamicAnswer(dynamicAnswersEntity)
                    }
                }
            }
        }
    }

    override fun onDependentSelect(question: FormQuestionLanguage, pos: Int, answer: String) {
        lifecycleScope.launch {
            //if ans already present delete it
            questionGroupList[fragPos].questions.removeAll {
                it.parent_question_id == question.tbl_form_questions_id
            }
            questionsAdapter.notifyDataSetChanged()
            //get all dependency question
            var deleteDependentQuestion = AppDatabase.getInstance(requireContext()).formDao()
                .getDependentFormsQuestionsToDelete(
                    prefsManager.getLanguage(),
                    group,
                    prefsManager.getForm().tbl_forms_id,
                    prefsManager.getProject().tbl_projects_id,
                    prefsManager.getProject().mst_divisions_id,
                    prefsManager.getProject().mst_categories_id,
                    question.tbl_form_questions_id
                )

            //delete if parent option changes
            deleteDependentQuestion.forEach {
                AppDatabase.getInstance(requireContext()).formDao()
                    .deleteDependencyAns(it.tbl_form_questions_id, prefsManager.getDraft())
            }

            //to get all dependent question
            var dependentQuestion =
                AppDatabase.getInstance(requireContext()).formDao().getDependentFormsQuestions(
                    prefsManager.getLanguage(),
                    group,
                    prefsManager.getForm().tbl_forms_id,
                    prefsManager.getProject().tbl_projects_id,
                    prefsManager.getProject().mst_divisions_id,
                    prefsManager.getProject().mst_categories_id,
                    question.tbl_form_questions_id,
                    answer
                )
            Log.d("laingageId",question.tbl_form_questions_id.toString()+"--"+
                    prefsManager.getForm().tbl_forms_id+"--"+prefsManager.getLanguage()+"--"+answer)

            dependentQuestion.forEach {
                if (it.question_type == "CHECKBOX" || it.question_type == "SINGLE_SELECT" ||
                    it.question_type == "MULTI_SELECT" || it.question_type == "RADIO"
                ) {
                    val options = AppDatabase.getInstance(requireContext()).formDao()
                        .getAllOptions(it.tbl_form_questions_id, prefsManager.getLanguage())
                    it.options = options

                    if (it.question_type == "SINGLE_SELECT") {
                        (it.options as MutableList<Options>).add(
                            0,
                            Options(getString(R.string.select), false)
                        )
                    }
                }
                questionGroupList[fragPos].questions.add(it)

                val dynamicAnswersEntity = DynamicAnswersEntity(
                    null,
                    it.mst_question_group_id,
                    "",
                    it.tbl_form_questions_id.toString(),
                    prefsManager.getDraft(),
                    ""
                )
                var checkid = AppDatabase.getInstance(requireContext()).formDao()
                    .getDependencyDynamicAns(it.tbl_form_questions_id, prefsManager.getDraft())
                if (checkid == null) {
                    AppDatabase.getInstance(requireContext()).formDao()
                        .insertDynamicAnswer(dynamicAnswersEntity)
                } else {
                    AppDatabase.getInstance(requireContext()).formDao().updateDynamicAnswer(
                        it.answer!!,
                        it.mst_question_group_id,
                        it.tbl_form_questions_id,
                        prefsManager.getDraft()
                    )
                }
            }

            //questionGroupList[fragPos].questions.add(pos+1,dependentQuestion)
            questionsAdapter.notifyDataSetChanged()
            Log.d("LogMap", dependentQuestion.toString())

        }
    }

    private fun openFilePick(type: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = type
        startActivityForResult(intent, PICKFILE_REQUEST_CODE)
    }

    val PICKFILE_REQUEST_CODE = 43
    val FROM_CAMERA = 49
    val FROM_MAP = 54
    var position = 0
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
        canvas.drawText(text, 20F, height + 15F, paint)
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
        val imageFile = File(
            path,
            UtilMethods.getFormattedDate(Date(), "yyyyMMddHHmmssSSS") + ".jpg"
        ) // Imagename.png
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

    fun deleteFile(path: String) {
        try {
            val file: File = File(path)
            file.delete()
            if (file.exists()) {
                file.canonicalFile.delete()
                if (file.exists()) {
                    requireContext().deleteFile(file.name)
                }
            }
        } catch (e: Exception) {
            e.toString()
        }

    }

    fun Showsuccess(count: Int, stringExtra: String?) {
        val dialogView = Dialog(requireContext())
        dialogView.setContentView(R.layout.dialog_success)
        dialogView.setCancelable(false)
        val submit: TextView = dialogView.findViewById(R.id.btn_submit)
        val msg: TextView = dialogView.findViewById(R.id.success_tv)
        var listOfFragment = arrayListOf<String>()
        val retrievedList = prefsManager.getTrainingState("trainingState")
        retrievedList?.forEach {
            listOfFragment.add(it)
        }
        listOfFragment.add(stringExtra!!)
        prefsManager.saveTrainingState("trainingState", listOfFragment)
        msg.setText("You have successfully pass the test, \n Your obtained Marks :" + count.toString())
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
        msg.setText("Test Failed, \n Your obtained Marks :" + count.toString())
        submit.setOnClickListener {
            val intent = Intent(requireContext(), TrainingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dialogView.dismiss()
        }
        dialogView.show()
    }

    override fun onSubmit(stringExtra: String?) {
        var count: Int = 0
        testquestionList.forEach {
            if (it.answer == it.answer2) {
                count++
            }
        }
        if (count < prefsManager.getPassingMarks()) {
            ShowFailure(count)
        } else {
            Showsuccess(count, stringExtra)
        }
    }
}