package com.yusuf.weather


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



class YsfPermission private constructor(private val activity: Activity) {


    companion object {

        private var instance: YsfPermission? = null
        fun singletonBuild(activity: Activity): YsfPermission {
            return if (instance == null) {
                instance = YsfPermission(activity)
                instance!!
            } else {
                instance!!
            }

        }
    }

    private var REQUESTCODE = -1
    private var mPermissions = mutableListOf<String>()
    private val needPermissionList = mutableListOf<String>()
    private var mDeniedTitle: CharSequence = "Info"
    private var mDeniedMessage: CharSequence = ""
    private var mDeniedNegativeButton: CharSequence = "Cancel"
    private var mDeniedPositiveButton: CharSequence = "Settings"

    fun setDeniedTitle(title: CharSequence) = apply {
        mDeniedTitle = title
    }

    fun setDeniedMessage(msg: CharSequence) = apply {
        mDeniedMessage = msg
    }


    fun setDeniedNegativeButton(str: CharSequence) = apply {
        mDeniedNegativeButton = str
    }

    fun setDeniedPositiveButton(str: CharSequence) = apply {
        mDeniedPositiveButton = str
    }

    fun setRequestCode(requestCode: Int) = apply {
        REQUESTCODE = requestCode
    }

    fun setPermission(vararg permissions: String) = apply {
        mPermissions = permissions.toMutableList()
    }

    fun setPermissionListener(listener: PermissionListener) = apply {
        mPermissionListener = listener
    }


    fun check() {
        if (REQUESTCODE == -1) {
            REQUESTCODE = (1..100).random()
        }
        askPermission()
    }

    private fun askPermission() {
        for (item in mPermissions) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    item
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                needPermissionList.add(item)
            }
        }

        if (needPermissionList.isEmpty()) {
            mPermissionListener?.onPermissionGranted()
        } else {

            ActivityCompat.requestPermissions(
                activity,
                mPermissions.map { it }.toTypedArray(),
                REQUESTCODE
            )
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUESTCODE) {
            if (grantResults.filter { it == -1 }.count() > 0) {
                val deniedList = mutableListOf<String>()
                grantResults.forEachIndexed { index, i ->
                    if (i == -1) {
                        deniedList.add(permissions[index])
                    }
                }
                mPermissionListener?.onPermissionDenied(deniedList)
                AlertDialog.Builder(activity)
                    .setTitle(mDeniedTitle)
                    .setMessage(mDeniedMessage)
                    .setNegativeButton(mDeniedNegativeButton) { d, _ ->
                        d.dismiss()
                    }
                    .setPositiveButton(mDeniedPositiveButton) { d, _ ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                        intent.data = uri
                        activity.startActivity(intent)
                        d.dismiss()
                    }
                    .show()
            } else {
                mPermissionListener?.onPermissionGranted()
            }

        }

    }

    private var mPermissionListener: PermissionListener? = null

    interface PermissionListener {
        fun onPermissionGranted()
        fun onPermissionDenied(deniedPermissions: List<String>)
    }


}
