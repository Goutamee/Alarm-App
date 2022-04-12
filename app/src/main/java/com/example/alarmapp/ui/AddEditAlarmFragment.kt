package com.example.alarmapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.alarmapp.R
import com.example.alarmapp.data.DatabaseHelper.Companion.getInstance
import com.example.alarmapp.model.Alarm
import com.example.alarmapp.service.AlarmReceiver
import com.example.alarmapp.service.LoadAlarmsService
import com.example.alarmapp.ui.AddEditAlarmActivity.Companion.MODE_EXTRA
import com.example.alarmapp.util.ViewUtils
import java.util.*

class AddEditAlarmFragment : Fragment() {
    private lateinit var mTimePicker: TimePicker
    private lateinit var mLabel: EditText
    private lateinit var mMon: CheckBox
    private lateinit var mTues: CheckBox
    private lateinit var mWed: CheckBox
    private lateinit var mThurs: CheckBox
    private lateinit var mFri: CheckBox
    private lateinit var mSat: CheckBox
    private lateinit var mSun: CheckBox
    private lateinit var llButtonBar: LinearLayout
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_add_edit_alarm, container, false)
        setHasOptionsMenu(true)
        val alarm = alarm
        mTimePicker = v.findViewById<View>(R.id.edit_alarm_time_picker) as TimePicker
        alarm?.time?.let { ViewUtils.setTimePickerTime(mTimePicker, it) }
        mLabel = v.findViewById<View>(R.id.edit_alarm_label) as EditText
        mLabel.setText(alarm?.label)
        mMon = v.findViewById<View>(R.id.edit_alarm_mon) as CheckBox
        mTues = v.findViewById<View>(R.id.edit_alarm_tues) as CheckBox
        mWed = v.findViewById<View>(R.id.edit_alarm_wed) as CheckBox
        mThurs = v.findViewById<View>(R.id.edit_alarm_thurs) as CheckBox
        mFri = v.findViewById<View>(R.id.edit_alarm_fri) as CheckBox
        mSat = v.findViewById<View>(R.id.edit_alarm_sat) as CheckBox
        mSun = v.findViewById<View>(R.id.edit_alarm_sun) as CheckBox
        llButtonBar = v.findViewById(R.id.llButtonBar)
        btnCancel = v.findViewById(R.id.btnCancel)
        btnSave = v.findViewById(R.id.btnSave)

        btnCancel.setOnClickListener { delete(true) }
        btnSave.setOnClickListener { save() }
        setDayCheckboxes(alarm)
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (mode == AddEditAlarmActivity.EDIT_ALARM) {
            inflater.inflate(R.menu.edit_alarm_menu, menu)
            llButtonBar.visibility = View.GONE
        } else {
            llButtonBar.visibility = View.VISIBLE
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> save()
            R.id.action_delete -> delete()
        }
        return super.onOptionsItemSelected(item)
    }

    private val alarm: Alarm?
        get() = requireArguments().getParcelable(AddEditAlarmActivity.ALARM_EXTRA)

    private val mode: Int
        get() = requireArguments().getInt(MODE_EXTRA)

    private fun setDayCheckboxes(alarm: Alarm?) {
        mMon.isChecked = alarm?.getDay(Alarm.MON) == true
        mTues.isChecked = alarm?.getDay(Alarm.TUES) == true
        mWed.isChecked = alarm?.getDay(Alarm.WED) == true
        mThurs.isChecked = alarm?.getDay(Alarm.THURS) == true
        mFri.isChecked = alarm?.getDay(Alarm.FRI) == true
        mSat.isChecked = alarm?.getDay(Alarm.SAT) == true
        mSun.isChecked = alarm?.getDay(Alarm.SUN) == true
    }

    private fun save() {
        val alarm = alarm
        alarm?.let {
            val time = Calendar.getInstance()
            time[Calendar.MINUTE] = ViewUtils.getTimePickerMinute(mTimePicker)
            time[Calendar.HOUR_OF_DAY] = ViewUtils.getTimePickerHour(mTimePicker)
            alarm.time = time.timeInMillis
            alarm.label = mLabel.text.toString()
            alarm.setDay(Alarm.MON, mMon.isChecked)
            alarm.setDay(Alarm.TUES, mTues.isChecked)
            alarm.setDay(Alarm.WED, mWed.isChecked)
            alarm.setDay(Alarm.THURS, mThurs.isChecked)
            alarm.setDay(Alarm.FRI, mFri.isChecked)
            alarm.setDay(Alarm.SAT, mSat.isChecked)
            alarm.setDay(Alarm.SUN, mSun.isChecked)
            val rowsUpdated = getInstance(requireContext())?.updateAlarm(alarm)
            val messageId =
                if (rowsUpdated == 1) R.string.update_complete else R.string.update_failed
            Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
            AlarmReceiver.setReminderAlarm(context, alarm)
            requireActivity().finish()
        }
    }

    private fun delete(delete: Boolean = false) {
        val alarm = alarm
        if (delete) {
            AlarmReceiver.cancelReminderAlarm(context, alarm)
            val rowsDeleted = alarm?.let { getInstance(requireContext())?.deleteAlarm(it) }
            if (rowsDeleted == 1) {
                LoadAlarmsService.launchLoadAlarmsService(context)
                requireActivity().finish()
            }
            return
        }
        val builder = AlertDialog.Builder(context, R.style.DeleteAlarmDialogTheme)
        builder.setTitle(R.string.delete_dialog_title)
        builder.setMessage(R.string.delete_dialog_content)
        builder.setPositiveButton(R.string.yes) { _, i -> //Cancel any pending notifications for this alarm
            AlarmReceiver.cancelReminderAlarm(context, alarm)
            val rowsDeleted = alarm?.let { getInstance(requireContext())?.deleteAlarm(it) }
            val messageId: Int
            if (rowsDeleted == 1) {
                messageId = R.string.delete_complete
                Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
                LoadAlarmsService.launchLoadAlarmsService(context)
                requireActivity().finish()
            } else {
                messageId = R.string.delete_failed
                Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton(R.string.no, null)
        builder.show()
    }

    companion object {
        fun newInstance(alarm: Alarm?, mode: Int): AddEditAlarmFragment {
            val args = Bundle()
            args.putParcelable(AddEditAlarmActivity.ALARM_EXTRA, alarm)
            args.putInt(MODE_EXTRA, mode)
            val fragment = AddEditAlarmFragment()
            fragment.arguments = args
            return fragment
        }
    }
}