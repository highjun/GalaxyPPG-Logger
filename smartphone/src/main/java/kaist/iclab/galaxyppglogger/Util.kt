package kaist.iclab.galaxyppglogger

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@SuppressLint("DefaultLocale")
fun Long.formatLapsedTime(): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
}

fun Long.convert2Datetime(): String {
    val date = Date(this)
    val calendar = Calendar.getInstance()
    calendar.setTime(date)

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    dateFormat.setTimeZone(TimeZone.getDefault())

    return dateFormat.format(calendar.getTime())
}

fun Int.toPpgStatus(): String{
    return when (this) {
        -999 -> "A higher priority sensor is operating. E.g. BIA\n"
        0 -> "Normal value"
        500 -> "STATUS is not supported"
        -1 -> "READY"
        else -> "UNKNOWN"
    }
}

fun Int.toHrStatus(): String{
    return when (this) {
        -999 -> "A higher priority sensor is operating. E.g. BIA\n"
        -99 -> "flush() is called but no data"
        -10 -> "PPG signal is too week"
        -8 -> "PPG signal is weak"
        -3 -> "Wearable is detached"
        -2 -> "Wearabled movement is detected"
        0 -> "Initial heart reate measuring state or a higher priority sensor is operating. E.g. BIA"
        1 -> "Heart rate is being measured"
        2 -> "READY"
        else -> "UNKNOWN"
    }
}

fun showToastFromBackground(context: Context, message: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}