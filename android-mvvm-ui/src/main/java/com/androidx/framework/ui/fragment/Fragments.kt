package com.androidx.framework.ui.fragment
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.LayoutInflaterCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.androidx.framework.ui.R


abstract class TemplateFragment : Fragment(),UIModuleAssembler, UIModuleAssembler.AppBarLayoutModule,UIModuleAssembler.ContentLayoutModule {
    override val mTemplate: UI by lazy {
        UI.COORDINATOR
    }

    override fun getCustomerTemplateLayout(): ViewGroup? {
        return null
    }
    override val mCenterTitle: String? = null
    private lateinit var mDelegate: AppCompatDelegate
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mDelegate = (context as AppCompatActivity).delegate
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       return assemble(context!!)

    }

}

abstract class DataBindingFragment<T:ViewDataBinding>:TemplateFragment(){
    override fun inflaterContent(parent: ViewGroup): View {

       val dataBinding =  DataBindingUtil.inflate<T>(layoutInflater,contentLayoutId,parent,false)
        return dataBinding.root
    }
}
