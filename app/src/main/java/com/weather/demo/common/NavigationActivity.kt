package com.weather.demo.common

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.weather.demo.Controller
import com.weather.demo.R
import com.weather.demo.activity.DashBoardActivity
import com.weather.demo.activity.SignInActivity
import com.weather.demo.activity.SignUpActivity
import com.weather.demo.adapter.DropDownAdapter
import com.weather.demo.utils.launchActivity
import java.io.File

open class NavigationActivity : BaseActivity() {

    override fun onStart() {
        super.onStart()
        Controller.instance.context = this
    }

    fun openLoginActivity() {
        launchActivity<SignInActivity> { }
        finishAffinity()
    }

    fun openDashBoardActivity() {
        launchActivity<DashBoardActivity> { }
        finishAffinity()
    }

    fun openSignUpActivity() {
        launchActivity<SignUpActivity> { }
    }

    var dialog: AppCompatDialog? = null

    fun showProgress() {
        try {
            dialog = AppCompatDialog(this, R.style.dialogTheme)
            dialog?.setCancelable(false)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setContentView(R.layout.progress_loading)
            dialog?.show()
        } catch (_: Exception) {
        }
    }

    internal fun hideProgress() {
        try {
            dialog?.dismiss()
            dialog = null
        } catch (_: Exception) {
        }
    }

    fun getFileFromUri(context: Activity, uri: Uri?): File? {
        uri ?: return null
        uri.path ?: return null

        var newUriString = uri.toString()
        newUriString = newUriString.replace(
            "content://com.android.providers.downloads.documents/",
            "content://com.android.providers.media.documents/"
        )
        newUriString = newUriString.replace(
            "/msf%3A", "/image%3A"
        )
        val newUri = Uri.parse(newUriString)

        var realPath = String()
        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (newUri.path?.contains("/document/image:") == true) {
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(newUri).split(":")[1])
        } else {
            databaseUri = newUri
            selection = null
            selectionArgs = null
        }
        try {
            val column = "_data"
            val projection = arrayOf(column)
            val cursor = context.contentResolver.query(
                databaseUri, projection, selection, selectionArgs, null
            )
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    realPath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Log.i("GetFileUri Exception:", e.message ?: "")
        }
        val path = realPath.ifEmpty {
            when {
                newUri.path?.contains("/document/raw:") == true -> newUri.path?.replace(
                    "/document/raw:", ""
                )
                newUri.path?.contains("/document/primary:") == true -> newUri.path?.replace(
                    "/document/primary:", "/storage/emulated/0/"
                )
                else -> return null
            }
        }
        return if (path.isNullOrEmpty()) null else File(path)
    }

    fun showPopup(
        v: View, cityList: MutableList<String>, action: (String) -> Unit
    ) {
        val popupView: View = View.inflate(this, R.layout.dialog_drop_dworn, null)
        val popupWindow = PopupWindow(
            popupView, v.width, ListPopupWindow.WRAP_CONTENT, true
        )

        val revDropDownList = popupView.findViewById<RecyclerView>(R.id.revDropDownList)
        val layoutManager = LinearLayoutManager(this)
        revDropDownList.layoutManager = layoutManager
        revDropDownList.adapter = DropDownAdapter(cityList) { title, _ ->
            action.invoke(title)
            popupWindow.dismiss()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popupWindow.overlapAnchor = true
        }
        if (cityList.size > 5) {
            popupWindow.height = 500
        } else {
            popupWindow.height = ListPopupWindow.WRAP_CONTENT
        }

        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this, R.drawable.rectangel
            )
        )
        popupWindow.elevation = 20f

        if (cityList.size > 5) {
            popupWindow.showAsDropDown(v, 0, popupWindow.height + 90)
        } else {
            popupWindow.showAsDropDown(
                v, 0, +v.height + popupView.height
            )
        }
    }

}