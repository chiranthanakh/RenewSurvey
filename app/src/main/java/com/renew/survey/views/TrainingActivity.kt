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
import android.view.View
import android.widget.MediaController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.renew.survey.R
import com.renew.survey.databinding.ActivityLoginBinding
import com.renew.survey.databinding.ActivitySplashBinding
import com.renew.survey.databinding.ActivityTrainingBinding
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

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*if(isVideoFileAvailable(getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath + "/training_video.mp4")) {
            binding.downloadButton.visibility = View.GONE
        }

        binding.downloadButton.setOnClickListener {
            if (checkPermission()) {
                downloadVideo("https://freetestdata.com/wp-content/uploads/2022/02/Free_Test_Data_15MB_MP4.mp4")
            } else {
                requestPermission()
            }
        }*/

        binding.startTest.setOnClickListener {
            Intent(this,FormsDetailsActivity::class.java).apply {
                putExtra("training",true)
                putExtra("trainingInfo",intent.getStringExtra("trainingInfo"))
                startActivity(this)
            }
        }

        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        registerReceiver(
            downloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )

        // Create a MediaController instance
        mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        mediaController.setMediaPlayer(binding.videoView)
        binding.videoView.setMediaController(mediaController)
    }


    private fun downloadVideo(videoUrl: String) {
        val request = DownloadManager.Request(Uri.parse(videoUrl))
        val fileName = "training_video.mp4"
        request.setDestinationInExternalFilesDir(
            applicationContext,
            Environment.DIRECTORY_MOVIES,
            fileName
        )
        request.setTitle("Video Download")
        request.setDescription("Downloading video file")
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)
        handler.postDelayed(progressChecker, 1000)

    }


    private fun playDownloadedVideo() {
        val videoUri = Uri.parse(getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath + "/training_video.mp4")
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.start()
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
           // binding.progressBar.visibility = android.view.View.GONE
          //  binding.playButton.visibility = android.view.View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadReceiver)
    }


    private val progressChecker = object : Runnable {
        override fun run() {
            //checkDownloadProgress()
            handler.postDelayed(this, 1000)
        }
    }

    /*@SuppressLint("Range")
    private fun checkDownloadProgress() {
        val downloadManager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val totalBytes =
                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val downloadedBytes =
                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

            if (totalBytes != -1L) {
                val progress = (downloadedBytes * 100 / totalBytes).toInt()
                binding.progressBar.progress = progress

                if (progress == 100) {
                    binding.progressBar.visibility = View.GONE
                    binding.playButton.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.playButton.visibility = View.GONE
                }
            }
        }

        cursor.close()
    }*/

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