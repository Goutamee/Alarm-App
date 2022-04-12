package com.example.alarmapp.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.alarmapp.model.Alarm
import com.example.alarmapp.util.AlarmUtils
import java.lang.UnsupportedOperationException
import kotlin.jvm.Synchronized

class DatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, SCHEMA) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        Log.i(javaClass.simpleName, "Creating database...")

        sqLiteDatabase.execSQL(CREATE_ALARMS_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        throw UnsupportedOperationException("This shouldn't happen yet!")
    }

    fun addAlarm(): Long {
        return addAlarm(Alarm())
    }

    fun addAlarm(alarm: Alarm?): Long {
        return writableDatabase.insert(TABLE_NAME, null, AlarmUtils.toContentValues(alarm))
    }

    fun updateAlarm(alarm: Alarm): Int {
        val where = "$_ID=?"
        val whereArgs = arrayOf(java.lang.Long.toString(alarm.id))
        return writableDatabase
            .update(TABLE_NAME, AlarmUtils.toContentValues(alarm), where, whereArgs)
    }

    fun deleteAlarm(alarm: Alarm): Int {
        return deleteAlarm(alarm.id)
    }

    private fun deleteAlarm(id: Long): Int {
        val where = _ID + "=?"
        val whereArgs = arrayOf(java.lang.Long.toString(id))
        return writableDatabase.delete(TABLE_NAME, where, whereArgs)
    }

    val alarms: List<Alarm>
        get() {
            var c: Cursor? = null
            return try {
                c = readableDatabase.query(
                    TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
                AlarmUtils.buildAlarmList(c)
            } finally {
                if (c != null && !c.isClosed) c.close()
            }
        }

    companion object {
        private const val DATABASE_NAME = "alarms.db"
        private const val SCHEMA = 1
        private const val TABLE_NAME = "alarms"
        const val _ID = "_id"
        const val COL_TIME = "time"
        const val COL_LABEL = "label"
        const val COL_MON = "mon"
        const val COL_TUES = "tues"
        const val COL_WED = "wed"
        const val COL_THURS = "thurs"
        const val COL_FRI = "fri"
        const val COL_SAT = "sat"
        const val COL_SUN = "sun"
        const val COL_IS_ENABLED = "is_enabled"
        const val CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TIME + " INTEGER NOT NULL, " +
                COL_LABEL + " TEXT, " +
                COL_MON + " INTEGER NOT NULL, " +
                COL_TUES + " INTEGER NOT NULL, " +
                COL_WED + " INTEGER NOT NULL, " +
                COL_THURS + " INTEGER NOT NULL, " +
                COL_FRI + " INTEGER NOT NULL, " +
                COL_SAT + " INTEGER NOT NULL, " +
                COL_SUN + " INTEGER NOT NULL, " +
                COL_IS_ENABLED + " INTEGER NOT NULL" +
                ");"
        private var sInstance: DatabaseHelper? = null
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): DatabaseHelper? {
            if (sInstance == null) {
                sInstance = DatabaseHelper(context.applicationContext)
            }
            return sInstance
        }
    }
}