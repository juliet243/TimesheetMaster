package com.example.assignmentpart2

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class TimerService : Service()
{
    override fun onBind(p0: Intent?): IBinder? = null

    private val timer = Timer()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int
    {
        val time = intent.getDoubleExtra(TimeExtra, 0.0)
        timer.scheduleAtFixedRate(TimeTask(time), 0, 100)
        return START_NOT_STICKY
        //return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy()
    {
        timer.cancel()
        super.onDestroy()
    }

    private inner class TimeTask(private var time: Double) : TimerTask()
    {
        override fun run() {
            val intent = Intent(TimerUpdated)
            time++
            intent.putExtra(TimeExtra, time)
            sendBroadcast(intent)
        }
    }

    companion object
    {
        const val TimerUpdated = "timerUpdate"
        const val TimeExtra = "timeExtra"
    }
}
//--------------------------------------ooo000EndOfFile000ooo---------------------------------------