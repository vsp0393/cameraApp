package com.vsple.cameraapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.vsple.cameraapp.modes.DataModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private var image: ImageView? = null
    private var tvsetDate: TextView? = null
    private var tvCopyText: TextView? = null
    private var button: Button? = null
    private var button2: Button? = null
    private var btDatePicker: Button? = null
    private var fileProfileImage: File? = null
    var currentPhotoPath: String? = null
    private val calendar = Calendar.getInstance()

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        image = findViewById<ImageView>(R.id.imageView)
        tvsetDate = findViewById<TextView>(R.id.tvsetDate)
        tvCopyText = findViewById<TextView>(R.id.tvCopyText)
        button = findViewById<Button>(R.id.btCamera)
        button2 = findViewById<Button>(R.id.btGallery)
        btDatePicker = findViewById<Button>(R.id.btDatePicker)

        tvCopyText!!.setText(getString(R.string.simple_string) + "" + "meraz")

        button2?.setOnClickListener(View.OnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        })

        button?.setOnClickListener(View.OnClickListener {
            openMoBileCamera()
        })
        btDatePicker?.setOnClickListener(View.OnClickListener {
            showDatePicker()
        })


        // api call
        val call: Call<DataModel> = ApiCall.apiService.getDATA()
        call.enqueue(object : Callback<DataModel> {
            override fun onResponse(call: Call<DataModel>, response: Response<DataModel>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d("TAG", "onResponse: " + data)
                    // Process the data here
                } else {
                    // Handle the error case here
                }
            }

            override fun onFailure(call: Call<DataModel>, t: Throwable) {
                // Handle the network request failure here
            }
        })

    }


    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        //        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == RESULT_OK) {
                val f: File = File(currentPhotoPath)
                image?.setImageURI(Uri.fromFile(f))
                Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(f))
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri = Uri.fromFile(f)
                Log.d("TAG", "contentUri" + contentUri)
                mediaScanIntent.data = contentUri
                fileProfileImage = File(f.toString())
                this.sendBroadcast(mediaScanIntent)
            }
        }

    private val galleryLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri? = result.data?.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? =
                contentResolver.query(selectedImage!!, filePathColumn, null, null, null)

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val filePath = cursor.getString(columnIndex)
                    val bitmap: Bitmap = BitmapFactory.decodeFile(filePath)

                    // Rest of your code here...
                }
                cursor.close()
            }

            // Get the selected image URI from the gallery
            /* var selectedImageUri = result.data!!.data
             val extension = getExtensionFromUri(selectedImageUri!!)
             selectedImageUri = Uri.parse(selectedImageUri.toString() + "." + extension);
             Log.d("TAG", "selectedImageUri" + selectedImageUri)
             image?.setImageURI(selectedImageUri)*/
        }
    }

    private fun openMoBileCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            fileProfileImage = null
            try {
                fileProfileImage = createImageFile()
            } catch (ex: IOException) {
                // Handle the exception
            }
            if (fileProfileImage != null) {
                val photoURI = fileProfileImage?.let {
                    FileProvider.getUriForFile(
                        this,
                        "com.vsple.cameraapp.fileprovider",
                        it
                    )
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureLauncher.launch(takePictureIntent) // Launch the camera
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val result2 = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        )
        val result3 = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
    }


    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 300
    }

    private fun showDatePicker() {
        // Get the current date
        val currentDate = Calendar.getInstance()

        // Subtract 18 years from the current date
        currentDate.add(Calendar.YEAR, -18)

        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { datePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Create a new Calendar instance to hold the selected date
                val selectedDate = Calendar.getInstance()
                // Set the selected date using the values received from the DatePicker dialog
                selectedDate.set(year, monthOfYear, dayOfMonth)
                // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                // Format the selected date into a string
                val formattedDate = dateFormat.format(selectedDate.time)
                // Update the TextView to display the selected date with the "Selected Date: " prefix
                tvsetDate?.text = "Selected Date: $formattedDate"
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )

        // Set a maximum date to today
        datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis

        // Show the DatePicker dialog
        datePickerDialog.show()
    }
}

