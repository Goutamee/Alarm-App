package com.example.alarmapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.alarmapp.R
import com.example.alarmapp.ui.AlarmLandingPageActivity

class AlarmLandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_landing_page)
    }

    companion object {
        @JvmStatic
        fun launchIntent(context: Context?): Intent {
            val i = Intent(context, AlarmLandingPageActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            return i
        }
    }
}