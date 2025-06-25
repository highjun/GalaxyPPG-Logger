package kaist.iclab.galaxyppglogger

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import kaist.iclab.galaxyppglogger.ui.MainScreen

class MainActivity : ComponentActivity() {

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hasForeground = foregroundPermissions.all {
            checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }

        val hasBackground =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    checkSelfPermission(backgroundSensorPermission) == PackageManager.PERMISSION_GRANTED

        if (hasForeground && hasBackground) {
            launchApp()
        } else {
            foregroundPermissionLauncher.launch(foregroundPermissions.toTypedArray())
        }
    }

    private val foregroundPermissions = buildList {
        add(Manifest.permission.BODY_SENSORS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val backgroundSensorPermission = Manifest.permission.BODY_SENSORS_BACKGROUND

    // Step 1: Request foreground permissions
    private val foregroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.all { it.value }) {
            Log.d(TAG, "✅ Foreground permissions granted")
            // Step 2: Request background sensor permission if needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                backgroundPermissionLauncher.launch(backgroundSensorPermission)
            } else {
                launchApp()
            }
        } else {
            showToastAndExit("Required permissions were denied. Exiting app.")
        }
    }

    // Step 2: Request BODY_SENSORS_BACKGROUND separately
    private val backgroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.d(TAG, "✅ Background sensor permission granted")
            launchApp()
        } else {
            showToastAndExit("Background sensor permission was denied. Exiting app.")
        }
    }

    private fun launchApp() {
        setContent {
            MainScreen()
        }
    }

    private fun showToastAndExit(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
        Handler(Looper.getMainLooper()).post{
            finishAffinity()
        }
    }



}