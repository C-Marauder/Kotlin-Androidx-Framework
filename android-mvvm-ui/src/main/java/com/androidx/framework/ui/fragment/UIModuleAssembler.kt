package com.androidx.framework.ui.fragment

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.androidx.framework.ui.utils.dp
import com.androidx.framework.ui.utils.sp
import com.androidx.framework.ui.widget.MaterialToolbar
import com.google.android.material.appbar.AppBarLayout

class UITemplate private constructor(){
    internal var mCenterTitleSize:Float = 16.sp
    internal var mToolbarBackground:Int = android.R.color.background_dark
    internal var mCenterTitleColor:Int = android.R.color.background_light
    internal var mNavDrawable:Int = 0
    internal var mToolbarHeight:Int = 48.dp
    fun centerTitleSize(titleSize:()->Float){
        mCenterTitleSize = titleSize()
    }

    fun toolbarBackground(background:()->Int){
        mToolbarBackground = background()
    }

    fun toolbarHeight(height:()->Int){
        mToolbarHeight = height()
    }

    fun navDrawable(nav:()->Int){
        mNavDrawable = nav()
    }
    companion object{
        internal val mInstance:UITemplate by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            UITemplate()
        }
        fun builder(init:UITemplate.()->Unit){
            init(mInstance)
        }
    }
}
enum class UI{
    COORDINATOR,CUSTOMER
}

internal interface UIModuleAssembler {

        val mTemplate:UI
        fun getCustomerTemplateLayout():ViewGroup?
        fun assemble(context: Context):View{
            var parent:ViewGroup?=null
           when(mTemplate){
                UI.COORDINATOR-> {
                    parent = CoordinatorLayout(context)
                    if (this is AppBarLayoutModule){
                        createAppBarLayout(parent)
                    }
                    if (this is ContentLayoutModule){
                        createContentLayout(parent)
                    }
                }
                UI.CUSTOMER-> getCustomerTemplateLayout()
            }


            return parent!!
        }


     interface AppBarLayoutModule{
        val mCenterTitle:String?
        private fun setMaterialToolbarLogic(materialToolbar: MaterialToolbar){
            with(UITemplate.mInstance){
                materialToolbar.run {
                    if (mNavDrawable != 0) {
                        setNavigationIcon(mNavDrawable)
                        setNavigationOnClickListener {
                            (materialToolbar.context as Activity).onBackPressed()
                        }
                    }
                    initCenterTitleView(mCenterTitleSize, mCenterTitleColor, mCenterTitle)
                }
            }

        }
        fun assembleChild(appBarLayout: AppBarLayout){
            val materialToolbar = MaterialToolbar(appBarLayout.context)
            setMaterialToolbarLogic(materialToolbar)
            appBarLayout.addView(materialToolbar,-1,UITemplate.mInstance.mToolbarHeight)
        }
        fun createAppBarLayout(parent:ViewGroup){
            val appBarLayout = AppBarLayout(parent.context)
            assembleChild(appBarLayout)
            parent.addView(appBarLayout,-1,-2)
        }

    }



     interface   ContentLayoutModule{
        val contentLayoutId:Int
        fun inflaterContent(parent: ViewGroup):View{
            return LayoutInflater.from(parent.context).inflate(contentLayoutId,parent,false)
        }
        fun createContentLayout(parent: ViewGroup){
            val contentView = inflaterContent(parent)
            parent.addView(contentView)
            val lp =  contentView.layoutParams as CoordinatorLayout.LayoutParams
            lp.behavior = AppBarLayout.ScrollingViewBehavior()
        }


    }

}



