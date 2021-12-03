package com.example.background.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalArgumentException


private const val TAG = "BlurWorker"

class BlurWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val appContext = applicationContext
        val imageResourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring image", appContext)

        sleep()

        return try {


            if (imageResourceUri.isNullOrEmpty()) {
                Log.e(TAG, "invalid input uri")
                throw IllegalArgumentException("invalid input uri")
            }

            val inputStream =
                appContext.contentResolver.openInputStream((Uri.parse(imageResourceUri)))

            val picture = BitmapFactory.decodeStream(inputStream)


            val output = blurBitmap(picture, appContext)

            val outputUri = writeBitmapToFile(appContext, output)

            makeStatusNotification("output is $outputUri", appContext)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri)

            Result.success(outputData)
        } catch (throwable: Throwable) {
            Log.e(TAG, throwable.message)
            Result.failure()
        }
    }
}