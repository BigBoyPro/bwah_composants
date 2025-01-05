package com.example.projectbwah.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.projectbwah.services.NotificationService

class MonthlyActivityNotificationWorker (context: Context, params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result {
        val activityName = inputData.getString("activityName") ?: "Unknown Activity" // Get activity name from inputData
        val scheduleType = inputData.getString("scheduleType") ?: "Unknown Type" // Get activity name from inputData

        val notificationService = NotificationService(applicationContext)
        notificationService.showNotification(activityName, scheduleType) // Pass activity name to showNotification
        return Result.success()
    }
}