package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


private const val TAG = "SaveImageToFileWorker"

class SaveImageToFileWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {

    private val fileName = "Blurred Image"
    private val dateFormatter = SimpleDateFormat(
        "yyyy.MM.dd 'at' HH:mm:ss z",
        Locale.getDefault()
    )


    override fun doWork(): Result {

        makeStatusNotification("Saving Image", applicationContext)
        val resolver = applicationContext.contentResolver


        return try {

            val imageUri = inputData.getString(KEY_IMAGE_URI)
            Log.e(TAG, imageUri.toString())
            if (imageUri.isNullOrEmpty()) {
                Log.e(TAG, "invalid input uri")
                throw IllegalArgumentException("invalid input uri")
            }

            val inputStream = resolver.openInputStream(Uri.parse(imageUri))
            val bitmap = BitmapFactory.decodeStream(inputStream)


            val outputImageUrl = MediaStore.Images.Media.insertImage(
                resolver,
                bitmap,
                fileName,
                dateFormatter.format(Date())
            )

            if (!outputImageUrl.isNullOrEmpty()) {
                val output = workDataOf(KEY_IMAGE_URI to outputImageUrl)
                Result.success(output)
            } else {
                Log.e(TAG, "Error writing to media store")
                Result.failure()
            }

        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}