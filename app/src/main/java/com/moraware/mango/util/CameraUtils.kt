package com.moraware.mango.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.IOException

class CameraUtils {

    companion object {
        @JvmStatic
        fun dispatchTakePictureIntent(activity: Activity, requestCode: Int): Uri? {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(activity.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile(activity)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        return null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                                activity,
                                "com.moraware.mango.provider",
                                it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        activity.startActivityForResult(takePictureIntent, requestCode)
                        return photoURI
                    }
                }
            }

            return null
        }

        var currentPhotoPath: String = ""

        @Throws(IOException::class)
        private fun createImageFile(context: Activity): File {
            // Create an image file name
            val timeStamp: String = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: throw IOException();

            return File.createTempFile(
                    "JPG_${timeStamp}_", /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
            }
        }

        private fun isExternalStorageWritable(): Boolean {
            //.d("Terms of Service: External storage available ${Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED}")
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }

    }
}