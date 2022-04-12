package com.example.alarmapp.service

import com.example.alarmapp.data.DatabaseHelper.Companion.getInstance
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import java.util.concurrent.Executors

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Executors.newSingleThreadExecutor().execute {
                val alarms = getInstance(context)?.alarms
                AlarmReceiver.setReminderAlarms(context, alarms)
            }
        }
    }
}