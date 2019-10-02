package com.androidx.framework.ui.utils

import android.app.Application
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

val Int.dp: Int
    get() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,this*1f, Resources.getSystem().displayMetrics).toInt()
    }
val Int.sp: Float
    get() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,this*1f, Resources.getSystem().displayMetrics)
    }


inline fun <reified T: AppCompatActivity> T.translucentStatusBar(){
    val decorView = window.decorView
    var newUiOptions =decorView.systemUiVisibility
    newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    decorView.systemUiVisibility = newUiOptions
    window.statusBarColor =    colorRes(android.R.color.transparent)
}

inline fun <reified T : AppCompatActivity> T.colorRes(@ColorRes resId: Int): Int {
    return this.application?.colorRes(resId)!!
}

inline fun <reified T : Application> T.colorRes(@ColorRes resId: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(resId)
    } else {
        ContextCompat.getColor(this, resId)
    }

}