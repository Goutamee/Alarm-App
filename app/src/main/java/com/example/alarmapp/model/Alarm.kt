package com.example.alarmapp.model

import android.os.Parcelable
import android.os.Parcel
import android.util.SparseBooleanArray
import kotlin.jvm.JvmOverloads
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class Alarm : Parcelable {
    private constructor(`in`: Parcel) {
        id = `in`.readLong()
        time = `in`.readLong()
        label = `in`.readString()
        days = `in`.readSparseBooleanArray()
        isEnabled = `in`.readByte().toInt() != 0
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeLong(id)
        parcel.writeLong(time)
        parcel.writeString(label)
        parcel.writeSparseBooleanArray(days)
        parcel.writeByte((if (isEnabled) 1 else 0).toByte())
    }

    @Retention(RetentionPolicy.SOURCE)
    internal annotation class Days

    val id: Long
    var time: Long
    var label: String?
    var days: SparseBooleanArray?
    var isEnabled = false

    @JvmOverloads
    constructor(id: Long = NO_ID) : this(id, System.currentTimeMillis()) {
    }

    constructor(id: Long, time: Long, @Days vararg days: Int) : this(id, time, null, *days) {}
    constructor(id: Long, time: Long, label: String?, @Days vararg days: Int) {
        this.id = id
        this.time = time
        this.label = label
        this.days = buildDaysArray(*days)
    }

    fun setDay(@Days day: Int, isAlarmed: Boolean) {
        days?.append(day, isAlarmed)
    }

    fun getDay(@Days day: Int): Boolean {
        return days?.let { it[day] } ?: false
    }

    fun notificationId(): Int {
        val id = id
        return (id xor (id ushr 32)).toInt()
    }

    override fun toString(): String {
        return "Alarm{" +
                "id=" + id +
                ", time=" + time +
                ", name='" + label + '\'' +
                ", allDays=" + days +
                ", isEnabled=" + isEnabled +
                '}'
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + (id xor (id ushr 32)).toInt()
        result = 31 * result + (time xor (time ushr 32)).toInt()
        result = 31 * result + label.hashCode()
        days?.let {
            for (i in 0 until it.size()) {
                result = 31 * result + if (it.valueAt(i)) 1 else 0
            }
        }
        return result
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Alarm> = object : Parcelable.Creator<Alarm> {
            override fun createFromParcel(`in`: Parcel): Alarm {
                return Alarm(`in`)
            }

            override fun newArray(size: Int): Array<Alarm?> {
                return arrayOfNulls(size)
            }
        }
        const val MON = 1
        const val TUES = 2
        const val WED = 3
        const val THURS = 4
        const val FRI = 5
        const val SAT = 6
        const val SUN = 7
        private const val NO_ID: Long = -1
        private fun buildDaysArray(@Days vararg days: Int): SparseBooleanArray {
            val array = buildBaseDaysArray()
            for (@Days day in days) {
                array.append(day, true)
            }
            return array
        }

        private fun buildBaseDaysArray(): SparseBooleanArray {
            val numDays = 7
            val array = SparseBooleanArray(numDays)
            array.put(MON, false)
            array.put(TUES, false)
            array.put(WED, false)
            array.put(THURS, false)
            array.put(FRI, false)
            array.put(SAT, false)
            array.put(SUN, false)
            return array
        }
    }
}