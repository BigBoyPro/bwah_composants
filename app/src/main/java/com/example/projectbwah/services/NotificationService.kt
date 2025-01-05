package com.example.projectbwah.services

import android.content.Context
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.projectbwah.MainActivity
import com.example.projectbwah.R

const val NOTIFICATION_CHANNEL_ID = "my_channel_id"
const val NOTIFICATION_CHANNEL_NAME = "My Channel"
const val NOTIFICATION_ID = 1
const val REQUEST_CODE = 200



class NotificationService (private val context: Context) {
    private  val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private  val myIntent = Intent(context, MainActivity::class.java)

    private  val pendingIntent = PendingIntent.getActivity(
        context,
        REQUEST_CODE,
        myIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    fun showNotification(activityName: String, scheduleType: String) {
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$scheduleType Reminder:") // Use activity name in title
            .setContentText("It's time for your $activityName!") // Use activity name in content
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)

    }
}