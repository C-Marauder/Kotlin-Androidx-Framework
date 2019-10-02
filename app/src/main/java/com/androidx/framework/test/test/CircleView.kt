package com.androidx.framework.test.test

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class CircleView:View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private val mPaint:Paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 6f
            color = Color.MAGENTA
            isAntiAlias = true
        }
    }
    private var mRadius:Float = 200f
    fun start(){
        GlobalScope.async {
            while (isActive){
                mDegree += 1f
              //  invalidate()
                mCanvas.rotate(mDegree)
                delay(60)
            }

        }
    }
    private val mPath: Path by lazy {
        Path()
    }
    private var mDegree:Float = 0f
    private lateinit var mCanvas: Canvas
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            val centerX = width/2f
            val centerY =height/2f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mPath.addArc(centerX-mRadius,centerY-mRadius,centerX+mRadius,centerY+mRadius,0f,90f)
            }
            mCanvas = it

            it.drawPath(mPath,mPaint)

        }
    }
}