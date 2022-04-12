package com.example.alarmapp.ui

import android.content.Context
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.alarmapp.R
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.alarmapp.ui.AlarmLandingPageFragment

class AlarmLandingPageFragment : Fragment(), SensorEventListener {
    private var lastTime: Long = 0
    private var speed = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var x = 0f
    private var y = 0f
    private var z = 0f
    private var count = 0
    lateinit var sensorManager: SensorManager
    private var accelerormeterSensor: Sensor? = null
    private var pm: PowerManager? = null
    private lateinit var vibrator: Vibrator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_alarm_landing_page, container, false)
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val launchMainActivityBtn = v.findViewById<View>(R.id.load_main_activity_btn) as Button
        val dismiss = v.findViewById<View>(R.id.dismiss_btn) as Button
        launchMainActivityBtn.setOnClickListener {
            startActivity(Intent(context, MainActivity::class.java))
            vibrator.cancel()
            requireActivity().finish()
        }
        dismiss.setOnClickListener {
            vibrator.cancel()
            requireActivity().finish()
        }
        pm = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
        startvibe()
        return v
    }

    override fun onStart() {
        super.onStart()
        if (accelerormeterSensor != null) sensorManager.registerListener(
            this, accelerormeterSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            val gabOfTime = currentTime - lastTime
            if (gabOfTime > 100) {
                lastTime = currentTime
                x = event.values[SensorManager.DATA_X]
                y = event.values[SensorManager.DATA_Y]
                z = event.values[SensorManager.DATA_Z]
                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000
                if (speed > SHAKE_THRESHOLD) {
                    lastTime = currentTime
                    count++ //for shake test
                    if (count == 10) {
                        vibrator.cancel()
                        requireActivity().finish()
                    }
                }
                lastX = event.values[DATA_X]
                lastY = event.values[DATA_Y]
                lastZ = event.values[DATA_Z]
            }
        }
    }

    fun startvibe() {
        vibrator.vibrate(longArrayOf(100, 1000, 100, 500, 100, 1000), 0)
    }

    companion object {
        const val SHAKE_THRESHOLD = 100
        var DATA_X = SensorManager.DATA_X
        var DATA_Y = SensorManager.DATA_Y
        var DATA_Z = SensorManager.DATA_Z
    }
}