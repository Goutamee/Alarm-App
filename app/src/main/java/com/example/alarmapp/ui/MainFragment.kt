package com.example.alarmapp.ui

import com.example.alarmapp.ui.AddEditAlarmActivity.Companion.buildAddEditAlarmActivityIntent
import com.example.alarmapp.service.LoadAlarmsReceiver.OnAlarmsLoadedListener
import com.example.alarmapp.service.LoadAlarmsReceiver
import com.example.alarmapp.adapter.AlarmsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.alarmapp.R
import com.example.alarmapp.view.EmptyRecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.alarmapp.util.AlarmUtils
import android.content.IntentFilter
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.alarmapp.service.LoadAlarmsService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.alarmapp.model.Alarm
import com.example.alarmapp.view.DividerItemDecoration
import java.util.ArrayList

class MainFragment : Fragment(), OnAlarmsLoadedListener {
    private lateinit var mReceiver: LoadAlarmsReceiver
    private lateinit var mAdapter: AlarmsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mReceiver = LoadAlarmsReceiver(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        val rv: EmptyRecyclerView = v.findViewById(R.id.recycler)
        mAdapter = AlarmsAdapter()
        rv.setEmptyView(v.findViewById(R.id.empty_view))
        rv.adapter = mAdapter
        rv.addItemDecoration(DividerItemDecoration(context))
        rv.layoutManager = LinearLayoutManager(context)
        rv.itemAnimator = DefaultItemAnimator()
        v.findViewById<Button>(R.id.btnExit).setOnClickListener { view: View? ->
            requireActivity().finish()
        }
        v.findViewById<Button>(R.id.btnAdd).setOnClickListener { view: View? ->
            AlarmUtils.checkAlarmPermissions(activity)
            val i = buildAddEditAlarmActivityIntent(context, AddEditAlarmActivity.ADD_ALARM)
            startActivity(i)
        }
        return v
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(LoadAlarmsService.ACTION_COMPLETE)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mReceiver, filter)
        LoadAlarmsService.launchLoadAlarmsService(context)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mReceiver)
    }

    override fun onAlarmsLoaded(alarms: ArrayList<Alarm>) {
        mAdapter.setAlarms(alarms)
    }
}