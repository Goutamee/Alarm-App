package com.example.alarmapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IntDef
import androidx.appcompat.app.AppCompatActivity
import com.example.alarmapp.R
import com.example.alarmapp.data.DatabaseHelper.Companion.getInstance
import com.example.alarmapp.model.Alarm
import com.example.alarmapp.service.LoadAlarmsService

class AddEditAlarmActivity : AppCompatActivity() {
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(EDIT_ALARM, ADD_ALARM, UNKNOWN)
    internal annotation class Mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_alarm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = toolbarTitle
        val alarm = alarm
        if (supportFragmentManager.findFragmentById(R.id.edit_alarm_frag_container) == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.edit_alarm_frag_container, AddEditAlarmFragment.newInstance(alarm, mode))
                .commit()
        }
    }

    private val alarm: Alarm?
        get() = when (mode) {
            EDIT_ALARM -> intent.getParcelableExtra(ALARM_EXTRA)
            ADD_ALARM -> {
                val id = getInstance(this)?.addAlarm()
                LoadAlarmsService.launchLoadAlarmsService(this)
                id?.let { Alarm(it) }
            }
            UNKNOWN -> throw IllegalStateException(
                "Mode supplied as intent extra for " +
                        AddEditAlarmActivity::class.java.simpleName + " must match value in " +
                        Mode::class.java.simpleName
            )
            else -> throw IllegalStateException(
                "Mode supplied as intent extra for " +
                        AddEditAlarmActivity::class.java.simpleName + " must match value in " +
                        Mode::class.java.simpleName
            )
        }

    @get:Mode
    private val mode: Int
        get() = intent.getIntExtra(MODE_EXTRA, UNKNOWN)
    private val toolbarTitle: String
        get() {
            val titleResId: Int = when (mode) {
                EDIT_ALARM -> R.string.edit_alarm
                ADD_ALARM -> R.string.add_alarm
                UNKNOWN -> throw IllegalStateException(
                    "Mode supplied as intent extra for " +
                            AddEditAlarmActivity::class.java.simpleName + " must match value in " +
                            Mode::class.java.simpleName
                )
                else -> throw IllegalStateException(
                    "Mode supplied as intent extra for " +
                            AddEditAlarmActivity::class.java.simpleName + " must match value in " +
                            Mode::class.java.simpleName
                )
            }
            return getString(titleResId)
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val ALARM_EXTRA = "alarm_extra"
        const val MODE_EXTRA = "mode_extra"
        const val EDIT_ALARM = 1
        const val ADD_ALARM = 2
        const val UNKNOWN = 0

        @JvmStatic
        fun buildAddEditAlarmActivityIntent(context: Context?, @Mode mode: Int): Intent {
            val i = Intent(context, AddEditAlarmActivity::class.java)
            i.putExtra(MODE_EXTRA, mode)
            return i
        }
    }
}