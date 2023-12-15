package com.renew.survey.views

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.renew.survey.R
import com.renew.survey.databinding.ActivitySplashBinding
import com.renew.survey.utilities.UtilMethods
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class SplashActivity:BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    private var permissionListener: PermissionListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        //val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //splashScreen.setKeepOnScreenCondition { true }
        setContentView(binding.root)
        checkPermissions()
    }

    fun isDataSyncedForTheDay():Boolean{
        val date= Date()
        return preferenceManager.getSync(UtilMethods.getFormattedDate(date))
    }

    fun proceed(){
        lifecycleScope.launch {
            delay(2000)
            if (preferenceManager.getToken()!=""){
                if (isDataSyncedForTheDay()){
                    Intent(this@SplashActivity, LanguageActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(this)
                        finish()
                    }
                }else{
                    Intent(this@SplashActivity, SyncDataActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(this)
                        finish()
                    }
                }

            }else{
                Intent(this@SplashActivity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(this)
                    finish()
                }
            }
        }
    }
    private fun checkPermissions() {
        permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                proceed()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    this@SplashActivity,
                    "Permission Denied\n$deniedPermissions", Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedMessage(getString(R.string.if_you_reject_this_permission))
                .setPermissions(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA
                )
                .check()
        } else {
            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedMessage(getString(R.string.if_you_reject_this_permission))
                .setPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA
                )
                .check()
        }
    }
}