package com.example.projectbwah.utils

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.projectbwah.data.ScheduleType
import com.example.projectbwah.workers.DailyActivityNotificationWorker
import com.example.projectbwah.workers.MonthlyActivityNotificationWorker
import com.example.projectbwah.workers.OnceActivityNotificationWorker
import com.example.projectbwah.workers.WeeklyActivityNotificationWorker
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.concurrent.TimeUnit


fun scheduleNotification(
    activityId: Int?,
    activityName: String,
    scheduleType: ScheduleType,
    scheduleDate: LocalDate?,
    scheduleTime: LocalTime?,
    scheduleDayOfWeekOrMonth: Int?,
    context: Context
) {

    val delay = calculateInitialDelay(scheduleType, scheduleDate, scheduleTime, scheduleDayOfWeekOrMonth)

    val workRequest = when (scheduleType) {
        ScheduleType.DAILY -> {
            val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyActivityNotificationWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData( // Call setInputData here
                    workDataOf(
                        "activityName" to activityName,
                        "scheduleType" to scheduleType.name,
                        "scheduleDate" to scheduleDate?.toString(),
                        "scheduleTime" to scheduleTime?.toString(),
                        "scheduleDayOfWeekOrMonth" to scheduleDayOfWeekOrMonth
                    )
                )
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork("activityNotification_$activityName", ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, dailyWorkRequest)
            dailyWorkRequest
        }
        ScheduleType.WEEKLY -> {
            val weeklyWorkRequest = PeriodicWorkRequestBuilder<WeeklyActivityNotificationWorker>(7, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData( // Call setInputData here
                    workDataOf(
                        "activityName" to activityName,
                        "scheduleType" to scheduleType.name,
                        "scheduleDate" to scheduleDate?.toString(),
                        "scheduleTime" to scheduleTime?.toString(),
                        "scheduleDayOfWeekOrMonth" to scheduleDayOfWeekOrMonth
                    )
                )
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork("activityNotification_$activityName", ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, weeklyWorkRequest)
            weeklyWorkRequest
        }
        ScheduleType.MONTHLY -> {
            val monthlyWorkRequest = PeriodicWorkRequestBuilder<MonthlyActivityNotificationWorker>(30, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData( // Call setInputData here
                    workDataOf(
                        "activityName" to activityName,
                        "scheduleType" to scheduleType.name,
                        "scheduleDate" to scheduleDate?.toString(),
                        "scheduleTime" to scheduleTime?.toString(),
                        "scheduleDayOfWeekOrMonth" to scheduleDayOfWeekOrMonth
                    )
                )
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork("activityNotification_$activityName", ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, monthlyWorkRequest)
            monthlyWorkRequest
        }
        ScheduleType.ONCE -> {
            // Target date and time
            val targetCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, scheduleDate?.year ?: Calendar.getInstance().get(Calendar.YEAR))
                set(
                    Calendar.MONTH, ((scheduleDate?.monthValue?.minus(1))  ?: Calendar.getInstance().get(
                        Calendar.MONTH) ))
                set(
                    Calendar.DAY_OF_MONTH, scheduleDate?.dayOfMonth ?: Calendar.getInstance().get(
                        Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, scheduleTime?.hour ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, scheduleTime?.minute ?: Calendar.getInstance().get(Calendar.MINUTE))
                set(Calendar.SECOND, scheduleTime?.second ?: Calendar.getInstance().get(Calendar.SECOND))
            }
            // Current time
            val currentCalendar = Calendar.getInstance()
            // Calculate the delay
            val delayInMillis = targetCalendar.timeInMillis - currentCalendar.timeInMillis

            // Ensure delay is not negative
            if (delayInMillis > 0) {
                val oneTimeWorkRequest = OneTimeWorkRequestBuilder<OnceActivityNotificationWorker>()
                    .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                    .setInputData( // Call setInputData here
                        workDataOf(
                            "activityName" to activityName,
                            "scheduleType" to scheduleType.name,
                            "scheduleDate" to scheduleDate?.toString(),
                            "scheduleTime" to scheduleTime?.toString(),
                            "scheduleDayOfWeekOrMonth" to scheduleDayOfWeekOrMonth
                        )
                    )
                    .build()
                WorkManager.getInstance(context).enqueueUniqueWork("activityNotification_$activityName", ExistingWorkPolicy.REPLACE, oneTimeWorkRequest)
                oneTimeWorkRequest
            } else {
                // Handle case where target date is in the past
                Log.d("ActivityViewModel", "Target date is in the past.")
                null // or throw an exception, or handle it differently
            }
        }
    }



}



fun calculateInitialDelay(
    scheduleType: ScheduleType,
    scheduleDate: LocalDate?,
    scheduleTime: LocalTime?,
    scheduleDayOfWeekOrMonth: Int?
): Long {
    val currentCalendar = Calendar.getInstance()
    val targetCalendar = Calendar.getInstance()

    when (scheduleType) {
        ScheduleType.ONCE -> {
            targetCalendar.apply {
                set(Calendar.YEAR, scheduleDate?.year ?: Calendar.getInstance().get(Calendar.YEAR) )
                set(
                    Calendar.MONTH, ((scheduleDate?.monthValue?.minus(1)) ?: Calendar.getInstance().get(
                        Calendar.MONTH) )) // Month is 0-indexed
                set(
                    Calendar.DAY_OF_MONTH, scheduleDate?.dayOfMonth ?: Calendar.getInstance().get(
                        Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, scheduleTime?.hour ?: 0)
                set(Calendar.MINUTE, scheduleTime?.minute ?: 0)
                set(Calendar.SECOND, scheduleTime?.second ?: 0)
            }
        }
        ScheduleType.DAILY -> {
            targetCalendar.apply {
                set(Calendar.HOUR_OF_DAY, scheduleTime?.hour ?: 0)
                set(Calendar.MINUTE, scheduleTime?.minute ?: 0)
                set(Calendar.SECOND, scheduleTime?.second ?: 0)
            }
            // If target time is in the past, schedule for the next day
            if (targetCalendar.before(currentCalendar)) {
                targetCalendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        ScheduleType.WEEKLY -> {
            targetCalendar.apply {
                set(Calendar.DAY_OF_WEEK, scheduleDayOfWeekOrMonth ?: 0) // Assuming scheduleDayOfWeekOrMonth is 1-indexed (Sunday=1, Monday=2, etc.)
                set(Calendar.HOUR_OF_DAY, scheduleTime?.hour ?: 0)
                set(Calendar.MINUTE, scheduleTime?.minute ?: 0)
                set(Calendar.SECOND, scheduleTime?.second ?: 0)
            }
            // If target day/time is in the past, schedule for the next week
            if (targetCalendar.before(currentCalendar)) {
                targetCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            }
        }
        ScheduleType.MONTHLY -> {
            targetCalendar.apply {
                set(Calendar.DAY_OF_MONTH, scheduleDayOfWeekOrMonth ?: 0)
                set(Calendar.HOUR_OF_DAY, scheduleTime?.hour ?: 0)
                set(Calendar.MINUTE, scheduleTime?.minute ?: 0)
                set(Calendar.SECOND, scheduleTime?.second ?: 0)
            }
            // If target day/time is in the past, schedule for the next month
            if (targetCalendar.before(currentCalendar)) {
                targetCalendar.add(Calendar.MONTH, 1)
            }
        }
    }

    return targetCalendar.timeInMillis - currentCalendar.timeInMillis
}

