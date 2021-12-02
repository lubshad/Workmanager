package com.example.background.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import java.io.File


private const val TAG = "CleanupWorker"

class CleanupWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {


        makeStatusNotification("Cleaning Temporory Files", applicationContext)

        return try {

            val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)
            if (outputDirectory.exists()) {
                val files = outputDirectory.listFiles()
                if (!files.isNullOrEmpty()) {
                    for (file in files) {
                        val fileName = file.name
                        if (fileName.isNotEmpty() && fileName.endsWith(".png")) {
                            val deleted = file.delete()
                            if (deleted) {
                                Log.e(TAG, "Deleted $fileName")
                            }

                        }
                    }
                }
            }
            Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, throwable.message)
            Result.failure()
        }
    }
}