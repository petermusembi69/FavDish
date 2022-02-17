package com.example.android.favdish.model.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotifyWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {
    override fun doWork(): Result {
        return  Result.success()
    }
}