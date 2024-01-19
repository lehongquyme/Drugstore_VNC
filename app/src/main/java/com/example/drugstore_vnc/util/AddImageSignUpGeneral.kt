package com.example.drugstore_vnc.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.drugstore_vnc.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("StaticFieldLeak")
object AddImageSignUpGeneral {

    private const val REQUEST_IMAGE_CAPTURE = 1
    private const val REQUEST_IMAGE_GALLERY = 2
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    var imageUri: Uri? = null

    fun openImageDialog(context: Context,activity: Activity, fragment: Fragment?) {
        this.activity = activity
        this.fragment = fragment

        val options = arrayOf(context.getString(R.string.capture),context.getString(R.string.gallery))
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.selectImg)

        builder.setItems(options) { _, which ->
            when (which) {
                0 -> takePicture()
                1 -> chooseFromGallery()
            }
        }
        builder.show()
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageUri = createImageUri()
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        if (fragment != null) {
            fragment?.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            activity?.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DISPLAY_NAME, "temp_image")
        }
        return activity?.contentResolver?.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    private fun chooseFromGallery() {
        val pickPhotoIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (fragment != null) {
            fragment?.startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_GALLERY)
        } else {
            activity?.startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_GALLERY)
        }
    }

    fun handleImageSelectionResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        imageView: ImageView?
    ) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    imageUri?.let { uri ->
                        val imageBitmap = getBitmapFromUri(uri)
                        val rotatedBitmap = getRotatedBitmap(imageBitmap, uri)
                        imageView?.setImageBitmap(rotatedBitmap)
                        val layoutParams = imageView?.layoutParams
                        layoutParams?.height = 1000
                        layoutParams?.width = 800
                        imageView?.layoutParams = layoutParams
                    }
                }

                REQUEST_IMAGE_GALLERY -> {
                    val selectedImage = data?.data
                    imageView?.setImageURI(selectedImage)
                    val layoutParams = imageView?.layoutParams
                    layoutParams?.height = 1000
                    layoutParams?.width = 800
                    imageView?.layoutParams = layoutParams
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = activity?.contentResolver?.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getRotatedBitmap(bitmap: Bitmap?, uri: Uri): Bitmap? {
        if (bitmap == null) return null

        val exif = try {
            ExifInterface(activity?.contentResolver?.openInputStream(uri)!!)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        val rotation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val rotationInDegrees = when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }

        return rotateImage(bitmap, rotationInDegrees)
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun getRequestBodyFromFile(fileUri: Uri?): RequestBody? {
        try {
            if (fileUri == null) {
                Log.e("File Handling", "FileUri is null")
                return null
            }

            val inputStream: InputStream? = activity?.contentResolver?.openInputStream(fileUri)
            val mediaType = activity?.contentResolver?.getType(fileUri)?.toMediaTypeOrNull()

            return if (inputStream != null && mediaType != null) {
                inputStream.use {
                    it.readBytes().toRequestBody(mediaType)
                }
            } else {
                null
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.e("File Handling", "File not found", e)
            return null
        }
    }

    fun isUrlReachable(urlString: String): Boolean {
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            connection.disconnect()

            responseCode == HttpURLConnection.HTTP_OK

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
