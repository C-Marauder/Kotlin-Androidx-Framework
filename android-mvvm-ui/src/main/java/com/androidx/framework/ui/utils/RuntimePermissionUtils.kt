package com.androidx.framework.ui.utils

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

/**
 * 检测权限
 */
inline fun <reified T:AppCompatActivity> T.isPermissionGranted(permission: String):Boolean{
    return ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED
}

/**
 * 判断该权限是否还会提示
 */
inline fun<reified T:AppCompatActivity> T.shouldShowPermissionRationale(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

inline fun <reified T:AppCompatActivity> T.requestPermission(vararg permission: String){
    ActivityCompat.requestPermissions(this,permission,10086)
}