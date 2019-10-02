package com.androidx.framework.ui.inflater

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import com.androidx.framework.ui.R
import com.androidx.framework.ui.utils.dp

object AppStyleLayoutInflater {

    fun init(appCompatActivity: AppCompatActivity){
        LayoutInflaterCompat.setFactory2(LayoutInflater.from(appCompatActivity),object : LayoutInflater.Factory2{

            override fun onCreateView(
                parent: View?,
                name: String,
                context: Context,
                attrs: AttributeSet
            ): View? {
                return onCreateView(name,context,attrs)

            }

            override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View?{
                val count = attrs.attributeCount
                for (index in 0 until count){
                    val name =   attrs.getAttributeName(index)
                    if (name == "shapeRadius"){

                    }

                }
               // val radius = attrs.getAttributeResourceValue(R.attr.shapeRadius,6.dp)
                val gradientDrawable =  GradientDrawable()
                gradientDrawable.cornerRadius = 24*1f
                val view = appCompatActivity.delegate.createView(null,name,context,attrs)
                view!!.background = gradientDrawable
                return view

//                return appCompatActivity.delegate.createView(null,name,context,attrs)
            }

        })
    }
}