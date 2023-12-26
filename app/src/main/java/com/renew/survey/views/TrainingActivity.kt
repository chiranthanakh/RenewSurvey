package com.renew.survey.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.renew.survey.R
import com.renew.survey.databinding.ActivityLoginBinding
import com.renew.survey.databinding.ActivitySplashBinding
import com.renew.survey.databinding.ActivityTrainingBinding
import com.renew.survey.room.AppDatabase
import com.renew.survey.room.entities.TutorialsDetailsEntry
import com.renew.survey.utilities.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class TrainingActivity : AppCompatActivity() {
    lateinit var binding: ActivityTrainingBinding
    private var downloadId: Long = 0
    private val handler = Handler()
    private lateinit var mediaController: MediaController
    lateinit var preferenceManager: PreferenceManager
    var PassingMarks:Int? = 0
    var tutorials: TutorialsDetailsEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager= PreferenceManager(this);

        val pdfuri = preferenceManager.loadUriList()
        lifecycleScope.launch {
            val testDetails = AppDatabase.getInstance(this@TrainingActivity).formDao().getTest(preferenceManager.getLanguage(), preferenceManager.getForm().tbl_forms_id)
            val testquestionList = AppDatabase.getInstance(this@TrainingActivity).formDao().getAllTestQuestions(preferenceManager.getLanguage(), preferenceManager.getForm().tbl_forms_id)
             tutorials = AppDatabase.getInstance(this@TrainingActivity).formDao().getTutorial( preferenceManager.getForm().tbl_forms_id)

            Log.d("testtutorialsdetail",tutorials.toString())

            if (testDetails?.title != null && testDetails?.passing_marks != null ) {
                preferenceManager.savePassingMarks(testDetails.passing_marks.toInt())
                binding.testName.setText("Test name :  "+testDetails.title)
                binding.testQuestion.setText("Total Questions :  "+testquestionList.size.toString())
                binding.testPassingMarks.setText("Passing Marks :  " +testDetails.passing_marks.toString())
            }
        }
        binding.startTest.setOnClickListener {
            Intent(this,TestActivity::class.java).apply{
                putExtra("training",true)
                putExtra("trainingInfo",intent.getStringExtra("trainingInfo"))
                putExtra("passingMarks",PassingMarks)
                startActivity(this)
            }
            /*Intent(this,FormsDetailsActivity::class.java).apply {
                putExtra("training",true)
                putExtra("trainingInfo",intent.getStringExtra("trainingInfo"))
                putExtra("passingMarks",PassingMarks)
                startActivity(this)
            }*/
        }
        binding.pdfView.setOnClickListener {
            pdfuri.forEach {
                tutorials?.let { it1 ->
                    if(it.toString().endsWith(it1.tutorial_file,true) ) {
                        openPdf(it)
                    }
                }
            }
        }
        mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        mediaController.setMediaPlayer(binding.videoView)
        binding.videoView.setMediaController(mediaController)
    }

    private fun openPdf(pdfUriString: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(pdfUriString, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        grantUriPermission(
            "com.example.receiverapp",
            pdfUriString,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        try {
            startActivity(intent)
        } catch (e: Exception) {

        }
    }

    private fun playDownloadedVideo() {
        val videoUri = Uri.parse(getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath + "/training_video.mp4")
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.start()
    }

    private val progressChecker = object : Runnable {
        override fun run() {
            //checkDownloadProgress()
            handler.postDelayed(this, 1000)
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val readPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val writePermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            101
        )
    }

    fun isVideoFileAvailable(filePath: String): Boolean {
        val videoFile = File(filePath)
        return videoFile.exists() && videoFile.isFile()
    }
}