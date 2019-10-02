package com.androidx.framework.ui.dialog

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.get
import com.androidx.framework.ui.utils.dp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AppBottomSheetDialog: BottomSheetDialogFragment() {

}



//class AppAlertDialog: AppCompatDialogFragment()


class AppDialogUtils private constructor(){
    private var gravity:Int = Gravity.CENTER //弹出方向
    private var width:Int = -1
    private var height:Int = -2
    private var  radius:Int = 8.dp //圆角
    private var dim:Float = 0.65f //透明度
    private var layoutResId:Int = 0
    private var outTouchCancel:Boolean= false
    companion object{
        const val BOTTOM:Int =1
        const val CENTER:Int =2
        const val TOP:Int =3

        fun show(activity: AppCompatActivity,init:AppDialogUtils.()->Unit){
            val mInstance = AppDialogUtils()
            init(mInstance)
            val contentContainer = (activity.window.decorView as ViewGroup).getChildAt(0) as ViewGroup
            val childCount = contentContainer.childCount
            for (index in 0 until childCount){
                val child = contentContainer.getChildAt(index)
                if (child is FrameLayout){
                    val dimBackgroundView = FrameLayout(activity).apply {
                        setBackgroundColor(Color.parseColor("#000000"))
                        alpha = mInstance.dim
                        setOnClickListener {
                            if (mInstance.outTouchCancel){

                            }
                        }
                    }
                    val dimAnimator = ObjectAnimator.ofFloat(dimBackgroundView,"alpha",0f,mInstance.dim)
                    dimAnimator.duration = 150
                    dimAnimator.interpolator = AccelerateDecelerateInterpolator()
                   val dialogView = LayoutInflater.from(activity).inflate(mInstance.layoutResId,dimBackgroundView,false)
                    val layoutParams = FrameLayout.LayoutParams(mInstance.width,mInstance.height,mInstance.gravity)
                    if (mInstance.width == -1){
                        layoutParams.marginStart = 48.dp
                        layoutParams.marginEnd = 48.dp
                    }

                    val gradientDrawable = GradientDrawable().apply {
                        cornerRadius = mInstance.radius *1f
                        setColor(Color.parseColor("#FFFFFF"))
                    }
                    dialogView.background = gradientDrawable
                    child.addView(dimBackgroundView)
                    child.addView(dialogView,layoutParams)
                    val animatorSet = AnimatorSet()
                    val objectAnimator = ObjectAnimator.ofFloat(dialogView,"scaleX",0.3f,1f)
                        .setDuration(250)
                    objectAnimator.interpolator = OvershootInterpolator()
                    animatorSet
                        .play(objectAnimator)
                        .with(dimAnimator)

                    animatorSet.start()
                }
            }
        }
    }
    fun  dialogDirection(gravity: ()->Int){
        this.gravity = gravity()
    }

    fun dialogWidth(width:()->Int){
        this.width = width()
    }

    fun dialogHeight(height:()->Int){
        this.height =height()
    }

    fun dialogRadius(radius:()->Int){
        this.radius = radius()
    }
    fun dialogDim(dim:()->Float){
        this.dim = dim()
    }
    fun dialogOutTouchCancel(outTouchCancel:()->Boolean){
        this.outTouchCancel =outTouchCancel()
    }
    fun dialogLayout(layoutResId:()->Int){
        this.layoutResId = layoutResId()
    }
}