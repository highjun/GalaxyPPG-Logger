package kaist.iclab.galaxyppglogger

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.wearable.Wearable
import kaist.iclab.galaxyppglogger.ui.MainScreen
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val TAG = javaClass.simpleName
    private val wearableCommunicationManager:WearableCommunicationManager by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStatusBarColor(window, Color.BLACK)
        setContent {
            MainScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        wearableCommunicationManager.init()
    }

    override fun onPause() {
        super.onPause()
        wearableCommunicationManager.clear()
    }


    fun setStatusBarColor(window: Window, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
                view.setBackgroundColor(color)

                // Adjust padding to avoid overlap
                view.setPadding(0, statusBarInsets.top, 0, 0)
                insets
            }
        } else {
            // For Android 14 and below
            window.statusBarColor = color
        }
    }
}


