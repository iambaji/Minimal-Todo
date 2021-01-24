package com.example.avjindersinghsekhon.minimaltodo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.avjindersinghsekhon.minimaltodo.utility.ToDoItem
import com.example.avjindersinghsekhon.minimaltodo.utility.TodoNotificationService
import java.util.*
import kotlin.collections.ArrayList

class AlarmManager(private val mToDoItemsArrayList: ArrayList<ToDoItem>, private val context: Context) {


     fun setAlarms() {
        if (mToDoItemsArrayList != null) {
            for (item in mToDoItemsArrayList!!) {
                if (item.hasReminder() && item.toDoDate != null) {
                    if (item.toDoDate.before(Date())) {
                        item.toDoDate = null
                        continue
                    }
                    val i = Intent(context, TodoNotificationService::class.java)
                    i.putExtra(TodoNotificationService.TODOUUID, item.identifier)
                    i.putExtra(TodoNotificationService.TODOTEXT, item.toDoText)
                    createAlarm(i, item.identifier.hashCode(), item.toDoDate.time)
                }
            }
        }
    }


     fun getAlarmManager(): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

     fun doesPendingIntentExist(i: Intent, requestCode: Int): Boolean {
        val pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_NO_CREATE)
        return pi != null
    }

     fun createAlarm(i: Intent, requestCode: Int, timeInMillis: Long) {
        val am = getAlarmManager()
        val pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT)
        am[AlarmManager.RTC_WAKEUP, timeInMillis] = pi
//        Log.d("OskarSchindler", "createAlarm "+requestCode+" time: "+timeInMillis+" PI "+pi.toString());
    }

     fun deleteAlarm(i: Intent, requestCode: Int) {
        if (doesPendingIntentExist(i, requestCode)) {
            val pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_NO_CREATE)
            pi.cancel()
            getAlarmManager().cancel(pi)
            Log.d("OskarSchindler", "PI Cancelled " + doesPendingIntentExist(i, requestCode))
        }
    }
}