package com.example.alarmapp.util

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import androidx.core.app.ActivityCompat
import com.example.alarmapp.data.DatabaseHelper
import com.example.alarmapp.model.Alarm
import java.text.SimpleDateFormat
import java.util.*

class AlarmUtils private constructor() {
    companion object {
        private val TIME_FORMAT = SimpleDateFormat("h:mm", Locale.getDefault())
        private val AM_PM_FORMAT = SimpleDateFormat("a", Locale.getDefault())
        private const val REQUEST_ALARM = 1
        private val PERMISSIONS_ALARM = arrayOf(
            Manifest.permission.VIBRATE
        )

        fun checkAlarmPermissions(activity: Activity?) {
            activity?.let {
                val permission = ActivityCompat.checkSelfPermission(
                    it, Manifest.permission.VIBRATE
                )
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        it,
                        PERMISSIONS_ALARM,
                        REQUEST_ALARM
                    )
                }
            }
        }

        fun toContentValues(alarm: Alarm?): ContentValues {
            val cv = ContentValues(10)
            alarm?.let {
                cv.put(DatabaseHelper.COL_TIME, alarm.time)
                cv.put(DatabaseHelper.COL_LABEL, alarm.label)
                val days = alarm.days
                days?.let {
                    cv.put(DatabaseHelper.COL_MON, if (days[Alarm.MON]) 1 else 0)
                    cv.put(DatabaseHelper.COL_TUES, if (days[Alarm.TUES]) 1 else 0)
                    cv.put(DatabaseHelper.COL_WED, if (days[Alarm.WED]) 1 else 0)
                    cv.put(DatabaseHelper.COL_THURS, if (days[Alarm.THURS]) 1 else 0)
                    cv.put(DatabaseHelper.COL_FRI, if (days[Alarm.FRI]) 1 else 0)
                    cv.put(DatabaseHelper.COL_SAT, if (days[Alarm.SAT]) 1 else 0)
                    cv.put(DatabaseHelper.COL_SUN, if (days[Alarm.SUN]) 1 else 0)
                }
                cv.put(DatabaseHelper.COL_IS_ENABLED, alarm.isEnabled)
            }
            return cv
        }

        fun buildAlarmList(c: Cursor?): ArrayList<Alarm> {
            if (c == null) return ArrayList()
            val size = c.count
            val alarms = ArrayList<Alarm>(size)
            if (c.moveToFirst()) {
                do {
                    val id = c.getLong(c.getColumnIndex(DatabaseHelper._ID))
                    val time = c.getLong(c.getColumnIndex(DatabaseHelper.COL_TIME))
                    val label = c.getString(c.getColumnIndex(DatabaseHelper.COL_LABEL))
                    val mon = c.getInt(c.getColumnIndex(DatabaseHelper.COL_MON)) == 1
                    val tues = c.getInt(c.getColumnIndex(DatabaseHelper.COL_TUES)) == 1
                    val wed = c.getInt(c.getColumnIndex(DatabaseHelper.COL_WED)) == 1
                    val thurs = c.getInt(c.getColumnIndex(DatabaseHelper.COL_THURS)) == 1
                    val fri = c.getInt(c.getColumnIndex(DatabaseHelper.COL_FRI)) == 1
                    val sat = c.getInt(c.getColumnIndex(DatabaseHelper.COL_SAT)) == 1
                    val sun = c.getInt(c.getColumnIndex(DatabaseHelper.COL_SUN)) == 1
                    val isEnabled = c.getInt(c.getColumnIndex(DatabaseHelper.COL_IS_ENABLED)) == 1
                    val alarm = Alarm(id, time, label)
                    alarm.setDay(Alarm.MON, mon)
                    alarm.setDay(Alarm.TUES, tues)
                    alarm.setDay(Alarm.WED, wed)
                    alarm.setDay(Alarm.THURS, thurs)
                    alarm.setDay(Alarm.FRI, fri)
                    alarm.setDay(Alarm.SAT, sat)
                    alarm.setDay(Alarm.SUN, sun)
                    alarm.isEnabled = isEnabled
                    alarms.add(alarm)
                } while (c.moveToNext())
            }
            return alarms
        }

        fun getReadableTime(time: Long): String {
            return TIME_FORMAT.format(time)
        }

        fun getAmPm(time: Long): String {
            return AM_PM_FORMAT.format(time)
        }

        @JvmStatic
        fun isAlarmActive(alarm: Alarm): Boolean {
            val days = alarm.days
            var isActive = false
            var count = 0
            while (count < days?.size() ?: 0 && !isActive) {
                isActive = days?.valueAt(count) ?: false
                count++
            }
            return isActive
        }

        fun getActiveDaysAsString(alarm: Alarm): String {
            val builder = StringBuilder("Active Days: ")
            if (alarm.getDay(Alarm.MON)) builder.append("Monday, ")
            if (alarm.getDay(Alarm.TUES)) builder.append("Tuesday, ")
            if (alarm.getDay(Alarm.WED)) builder.append("Wednesday, ")
            if (alarm.getDay(Alarm.THURS)) builder.append("Thursday, ")
            if (alarm.getDay(Alarm.FRI)) builder.append("Friday, ")
            if (alarm.getDay(Alarm.SAT)) builder.append("Saturday, ")
            if (alarm.getDay(Alarm.SUN)) builder.append("Sunday.")
            if (builder.substring(builder.length - 2) == ", ") {
                builder.replace(builder.length - 2, builder.length, ".")
            }
            return builder.toString()
        }
    }

    init {
        throw AssertionError()
    }
}