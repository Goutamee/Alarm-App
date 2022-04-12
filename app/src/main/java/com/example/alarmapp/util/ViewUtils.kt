package com.example.alarmapp.util

import android.widget.TimePicker
import android.os.Build
import android.annotation.TargetApi
import android.content.res.Resources
import java.lang.AssertionError
import java.util.*

class ViewUtils private constructor() {
    companion object {
        @JvmStatic
        fun dpToPx(dp: Float): Float {
            return dp * Resources.getSystem().displayMetrics.density
        }

        fun setTimePickerTime(picker: TimePicker?, time: Long) {
            val c = Calendar.getInstance()
            c.timeInMillis = time
            val minutes = c[Calendar.MINUTE]
            val hours = c[Calendar.HOUR_OF_DAY]
            picker?.minute = minutes
            picker?.hour = hours
        }

        @TargetApi(Build.VERSION_CODES.M)
        fun getTimePickerMinute(picker: TimePicker?): Int {
            return picker?.minute ?: 0
        }

        @TargetApi(Build.VERSION_CODES.M)
        fun getTimePickerHour(picker: TimePicker?): Int {
            return picker?.hour ?: 0
        }
    }

    init {
        throw AssertionError()
    }
}