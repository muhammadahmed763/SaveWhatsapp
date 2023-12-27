package com.example.savewhatsapp.ui.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.savewhatsapp.ui.MainActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.savewhatsapp.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE
    private val scrollInterval: Long = 100
    private val REQUEST_CODE = 99
    lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForAppUpdate()
        requestPermissionForStorage()
    }
    private fun requestPermissionForStorage() {
        if(Build.VERSION.SDK_INT < 33) {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                // Code to execute if SDK version is greater than Q
                // For example, you might use new APIs that are only available on Android 11 and above

                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is already granted, perform the desired operation
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(5000)
                        //var s =InsititialAdJavaClass()
                        //s.insititailAdShow(this@SplashScreen)
                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                        finish()
                    }
                } else {
                    // Permission is not granted, request it
                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }

            } else {
                // Code to execute if SDK version is Q or lower
                // For example, you might use older APIs that are still supported on Android 10 and below
                requestStorageOnLowerVersionsPermission()
            }
        }else{
            // ask here for android 13 permissions
            requestStoragePermissionAndroid13()
        }
    }
    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener {info->
            val isUpdateAvailaible=info.updateAvailability()== UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed=when(updateType){
                AppUpdateType.IMMEDIATE->info.isImmediateUpdateAllowed
                else ->false
            }
            if (isUpdateAvailaible&&isUpdateAllowed){
                appUpdateManager.startUpdateFlowForResult(
                    info,updateType,this,123
                )
            }
        }.addOnFailureListener {

        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        recreate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode != RESULT_OK) {
                // The user has cancelled or failed to complete the update
                // Prompt the update again or close the app
                checkForAppUpdate()
                // Optionally, you can close the app if you do not want to allow usage without the update
                // finish()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        123
                    )
                }
            }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            finish()
        } else {
            // Permission is denied, handle accordingly
            Toast.makeText(this, "Permission must require to use this app", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun requestStorageOnLowerVersionsPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions already granted
            CoroutineScope(Dispatchers.IO).launch {
                delay(2000)

                //var s =InsititialAdJavaClass()
                //s.insititailAdShow(this@SplashScreen)
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestStoragePermissionAndroid13() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions already granted
            CoroutineScope(Dispatchers.IO).launch {
                delay(9000)

                //var s =InsititialAdJavaClass()
                //s.insititailAdShow(this@SplashScreen)
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                ),
                REQUEST_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            } else {
                // Permissions not granted
                Toast.makeText(this, "Permission must require to use this app", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}