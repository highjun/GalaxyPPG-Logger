package kaist.iclab.galaxyppglogger

import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

fun isPermissionAllowed(androidContext: Context, permissions: List<String>): Boolean {
    return permissions.all { permission ->
        val permission_status = ContextCompat.checkSelfPermission(androidContext, permission)
        when (permission_status) {
            PackageManager.PERMISSION_GRANTED -> true
            PackageManager.PERMISSION_DENIED -> false
            else -> {
                Log.d("Util", "Unknown permission_status: $permission_status")
                false
            }
        }
    }
}

fun showToastFromBackground(context: Context, message: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
