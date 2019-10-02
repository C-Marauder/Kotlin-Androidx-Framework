package com.androidx.framework.ui.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.androidx.framework.ui.utils.dp
import com.google.android.material.internal.ThemeEnforcement.createThemedContext
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.MaterialShapeUtils
import com.google.android.material.textview.MaterialTextView

class MaterialToolbar : Toolbar {
    private lateinit var mCenterTextView:MaterialTextView
    companion object {
        private const val DEF_STYLE_RES: Int = android.R.style.Widget_Material_Toolbar
    }

    constructor(context: Context) : super(context) {
        addCenterTitleView()

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
       // init()
        addCenterTitleView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        createThemedContext(context, attrs, defStyleAttr, DEF_STYLE_RES),
        attrs,
        defStyleAttr
    ) {

        initBackground(context)
        addCenterTitleView()
       // init()
    }

    private fun addCenterTitleView(){

        mCenterTextView = MaterialTextView(context).apply {
            gravity = Gravity.CENTER
        }
        addView(mCenterTextView,-2,-1)
        val lp = mCenterTextView.layoutParams as Toolbar.LayoutParams
        lp.gravity = Gravity.CENTER
        mCenterTextView.layoutParams = lp
    }

    fun initCenterTitleView(centerTitleSize:Float,centerTitleColor:Int,centerTitle:String?){
        centerTitle?.let {
            mCenterTextView.text = it
        }
        mCenterTextView.textSize = centerTitleSize

    }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        MaterialShapeUtils.setParentAbsoluteElevation(this)

    }


    override fun setElevation(elevation: Float) {
        super.setElevation(elevation)
        MaterialShapeUtils.setElevation(this, elevation)

    }

    private fun initBackground(context: Context) {
        val background = background
        if (background != null && background !is ColorDrawable) {
            return
        }
        val materialShapeDrawable = MaterialShapeDrawable()
        val backgroundColor =
            if (background != null) (background as ColorDrawable).color else Color.TRANSPARENT
        materialShapeDrawable.fillColor = ColorStateList.valueOf(backgroundColor)
        materialShapeDrawable.initializeElevationOverlay(context)
        materialShapeDrawable.elevation = ViewCompat.getElevation(this)
        ViewCompat.setBackground(this, materialShapeDrawable)
    }

    private fun init() {
        (this::class.java.superclass as Class<Toolbar>).run {
            val field = getDeclaredField("mNavButtonView")
            field.isAccessible = true
            val mImageButton = field.get(this@MaterialToolbar) as ImageButton
            mImageButton.setPadding(0,24.dp,0,0)

        }

    }

    override fun setNavigationIcon(icon: Drawable?) {
        super.setNavigationIcon(icon)
        init()
    }

}