package com.example.alarmapp.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.alarmapp.model.Alarm
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.alarmapp.R
import androidx.core.content.ContextCompat
import com.example.alarmapp.util.AlarmUtils
import com.example.alarmapp.ui.AddEditAlarmActivity
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.Spanned
import android.view.View
import android.widget.TextView

class AlarmsAdapter : RecyclerView.Adapter<AlarmsAdapter.ViewHolder>() {
    private var mAlarms: List<Alarm>? = null
    private lateinit var mDays: Array<String>
    private var mAccentColor = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val c = parent.context
        val v = LayoutInflater.from(c).inflate(R.layout.alarm_row, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val c = holder.itemView.context
        if (mAccentColor == -1) {
            mAccentColor = ContextCompat.getColor(c, R.color.accent)
        }
        mDays = c.resources.getStringArray(R.array.days_abbreviated)
        val alarm = mAlarms?.get(position)
        alarm?.let {
            holder.tvTime.text = AlarmUtils.getReadableTime(alarm.time)
            holder.tvAmPm.text = AlarmUtils.getAmPm(alarm.time)
            if (alarm.label.isNullOrBlank()) {
                "Alarm ${position + 1}".also { holder.tvLabel.text = it }
            } else {
                holder.tvLabel.text = alarm.label
            }
            holder.tvDays.text = buildSelectedDays(alarm)
            holder.itemView.setOnClickListener { view ->
                val context = view.context
                val launchEditAlarmIntent = AddEditAlarmActivity.buildAddEditAlarmActivityIntent(
                    context, AddEditAlarmActivity.EDIT_ALARM
                )
                launchEditAlarmIntent.putExtra(AddEditAlarmActivity.ALARM_EXTRA, alarm)
                context.startActivity(launchEditAlarmIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return mAlarms?.size ?: 0
    }

    private fun buildSelectedDays(alarm: Alarm): Spannable {
        val numDays = 7
        val days = alarm.days
        val builder = SpannableStringBuilder()
        var span: ForegroundColorSpan
        var startIndex: Int
        var endIndex: Int
        for (i in 0 until numDays) {
            startIndex = builder.length
            val dayText = mDays[i]
            builder.append(dayText)
            builder.append(" ")
            endIndex = startIndex + dayText.length
            val isSelected = days?.valueAt(i)
            if (isSelected == true) {
                span = ForegroundColorSpan(mAccentColor)
                builder.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return builder
    }

    fun setAlarms(alarms: List<Alarm>?) {
        mAlarms = alarms
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvAmPm: TextView = itemView.findViewById(R.id.tvAmPm)
        val tvLabel: TextView = itemView.findViewById(R.id.tvLabel)
        val tvDays: TextView = itemView.findViewById(R.id.tvDays)

    }
}