package com.weather.demo.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.permissionx.guolindev.PermissionX
import com.weather.demo.BuildConfig
import com.weather.demo.R
import com.weather.demo.dialog.DialogUtils
import com.weather.demo.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

open class BaseActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val job = Job()

    lateinit var takePicture: ActivityResultLauncher<Uri>
    var finalPickerCallBack: ((Uri, String) -> Unit?)? = null
    var pickerCallBack: ((String) -> Unit?)? = null

    lateinit var pickMediaGallery: ActivityResultLauncher<PickVisualMediaRequest>
    val which = ""
    private var capturedUri: Uri? = null
    private var capturedPath: String? = null
    private var imagePickerCallBack: ((Uri, String) -> Unit)? = null
    private var settingCallBack: (() -> Unit)? = null

    fun makeToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
    }

    override fun attachBaseContext(newBase: Context?) {
        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }

    fun hideKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    protected fun hasInternet(): Boolean {
        return Utils.hasInternet(this)
    }

    private fun galleryLauncher(function: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                this, readImagePermission
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                function.invoke()
            }
            else -> {
                val appName = getString(R.string.app_name)
                callPermissionSetting(
                    "Allow " + appName
                            + "access to your device's photos and media. Tap settings > Permissions and turn media on."
                ) {
                    openNotificationSetting {
                        galleryLauncher(function)
                    }
                }
            }
        }
    }

    private fun Context.openNotificationSetting(callBack: (() -> Unit)) {
        settingCallBack = callBack
        val intent = (Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        })
        settingCallBackLaunch.launch(intent)
    }

    private val settingCallBackLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            settingCallBack?.invoke()
            val resultCode = result.resultCode
        }


    private fun callPermissionSetting(message: String, function: () -> Unit) {
        DialogUtils.showCustomDialogWithButton(
            this,
            message = message,
            positiveBtnName = getString(R.string.settings),
            negativeBtnName = getString(R.string.not_now),
            positiveListener = {
                function.invoke()
            })
    }

    fun toMultipartBody(param: String, file: File?): MultipartBody.Part? {
        var attachment: MultipartBody.Part? = null
        val requestFile: RequestBody
        if (file != null) {
            requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val filename = file.name
            attachment = MultipartBody.Part.createFormData(param, filename, requestFile)
        }
        return attachment
    }

    fun pickImage(crop: Boolean = true, callBack: (Uri, String) -> Unit) {
        pickAndCapturedImage(
            galleyOnly = true, cameraOnly = false, crop = crop, callBack = callBack
        )
    }

    fun capturedImage(crop: Boolean = true, callBack: (Uri, String) -> Unit) {
        pickAndCapturedImage(
            galleyOnly = false, cameraOnly = true, crop = crop, callBack = callBack
        )
    }

    fun selectionPicker(crop: Boolean = false, callBack: (Uri, String) -> Unit) {
        pickAndCapturedImage(
            galleyOnly = false, cameraOnly = false, crop = crop, callBack = callBack
        )
    }


    protected fun getPermissions(
        array: List<String>,
        permissionMsg: String = getString(R.string.app_needs_permission_to_continue),
        permissionGranted: () -> Unit
    ) {
        PermissionX.init(this).permissions(array).request { allGranted, _, deniedList ->
            if (allGranted) {
                permissionGranted()
            } else {
                DialogUtils.showCustomDialogWithButton(
                    this,
                    message = permissionMsg,
                    positiveBtnName = getString(R.string.settings),
                    negativeBtnName = getString(R.string.cancel),
                    positiveListener = {
                        openNotificationSetting {
                            getPermissions(array, permissionMsg, permissionGranted)
                        }
                    }
                )
            }
        }
    }

    fun pickAndCapturedImage(
        galleyOnly: Boolean = false,
        cameraOnly: Boolean = false,
        crop: Boolean = true,
        callBack: ((Uri, String) -> Unit)?
    ) {
        imagePickerCallBack = callBack


        val picker = ImagePicker.with(this)

        if (galleyOnly) {
            picker.galleryOnly()
        } else if (cameraOnly) {
            picker.cameraOnly()
        }

        if (crop) {
            picker.crop()
        }
        picker.createIntent {
            imagePickerLuncher.launch(it)
        }
    }

    private val imagePickerLuncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    imagePickerCallBack?.invoke(fileUri, fileUri.path.toString())
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                }
            }
        }

    fun checkPermissionAndGetPhoto(function: (Uri, String) -> Unit = { s, p -> }) {
        finalPickerCallBack = function
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPermissions(
                arrayListOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                ), getString(R.string.permission_msg_gallery)
            ) {
                pickMediaGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        } else {
            getPermissions(
                arrayListOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), getString(R.string.permission_msg_gallery)
            ) {
                (this).pickImage(false) { uri, s ->
                    finalPickerCallBack?.invoke(uri, s)
                }
            }
        }
    }

    fun checkPermissionAndCapturePhoto(function: (Uri, String) -> Unit = { _, p -> }) {
        finalPickerCallBack = function
        getPermissions(
            arrayListOf(
                Manifest.permission.CAMERA
            ), getString(R.string.permission_msg_camera)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                try {
                    val photoFile = createImageFile()
                    capturedUri = FileProvider.getUriForFile(
                        this,
                        "${BuildConfig.APPLICATION_ID}.fileprovider",
                        photoFile
                    )
                    takePicture.launch(capturedUri)

                } catch (ex: Exception) {
                    Log.e("Rk", ex.message.toString())
                }
            } else {
                (this).capturedImage(false) { uri, s ->
                    finalPickerCallBack?.invoke(uri, s)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        capturedPath = image.absolutePath

        return image
    }

    override fun onStart() {
        super.onStart()
        pickMediaGallery =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    finalPickerCallBack?.invoke(uri, uri.path!!)
                } else {
                    Log.e("PhotoPicker", "No media selected")
                }
            }

        takePicture =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
                if (success) {
                    capturedUri?.let {
                        finalPickerCallBack?.invoke(it, capturedPath.toString())
                    }
                }
            }
    }


    private val readImagePermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE

    private val readVideoPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_VIDEO
        else Manifest.permission.READ_EXTERNAL_STORAGE

    private fun checkPermissionStorage(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this, readImagePermission
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
}