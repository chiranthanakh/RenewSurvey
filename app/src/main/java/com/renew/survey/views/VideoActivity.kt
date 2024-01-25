package com.renew.survey.views

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.MediaController
import com.renew.survey.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {
    lateinit var binding:ActivityVideoBinding
    private lateinit var mediaController: MediaController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVideoBinding.inflate(layoutInflater)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.root)
        val data=intent.getStringExtra("data")
        val uri= Uri.parse(data)
        mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        mediaController.setMediaPlayer(binding.videoView)
        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(uri)
        binding.videoView.start()
    }
}