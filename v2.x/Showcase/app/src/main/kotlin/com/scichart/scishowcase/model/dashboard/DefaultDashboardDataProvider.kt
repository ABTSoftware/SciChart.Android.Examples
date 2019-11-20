package com.scichart.scishowcase.model.dashboard

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import com.scichart.scishowcase.model.DataProviderBase
import java.io.RandomAccessFile
import java.util.*
import java.util.concurrent.TimeUnit


class DefaultDashboardDataProvider(val context: Context) : DataProviderBase<DashboardData>(1000L, TimeUnit.MICROSECONDS) {
    override fun onNext(): DashboardData {
        val time = Calendar.getInstance().timeInMillis
        val cpuUsage = getCpuUsage()
        val memoryUsage = getMemoryUsage()

        return DashboardData(time, cpuUsage, memoryUsage)
    }

    fun getCpuUsage(): Float {
        val reader = RandomAccessFile("/proc/stat", "r")
        var load = reader.readLine()

        var toks = load.split(" ")

        val idle1 = toks[5].toLong()
        val cpu1 = toks[2].toLong() + toks[3].toLong() + toks[4].toLong() + toks[6].toLong() + toks[7].toLong() + toks[8].toLong()

        try {
            Thread.sleep(360)
        } catch (e: Exception) {
        }

        reader.seek(0)
        load = reader.readLine()
        reader.close()

        toks = load.split(" ")

        val idle2 = toks[5].toLong()
        val cpu2 = toks[2].toLong() + toks[3].toLong() + toks[4].toLong() + toks[6].toLong() + toks[7].toLong() + toks[8].toLong()

        return (cpu2 - cpu1).toFloat() / (cpu2 + idle2 - (cpu1 + idle1))
    }

    fun getMemoryUsage(): Long {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)

        val availableMegs = mi.availMem / 0x100000L

        //Percentage can be calculated for API 16+
        val usedMemory = mi.totalMem - mi.availMem
        val percentUsed = (usedMemory / mi.totalMem).toDouble()

        return usedMemory / 0x100000L
    }
}