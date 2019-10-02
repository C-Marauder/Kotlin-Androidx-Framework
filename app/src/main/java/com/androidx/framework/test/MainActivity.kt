package com.androidx.framework.test

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.recyclerview.widget.RecyclerView
import com.androidx.framework.logic.api.ApiResponse
import com.androidx.framework.logic.network.NetworkObserver
import com.androidx.framework.logic.network.isNetworkConnected
import com.androidx.framework.logic.vm.AppViewModel
import com.androidx.framework.logic.vm.NetworkBoundResource
import com.androidx.framework.logic.vm.Service
import com.androidx.framework.logic.vm.viewModel
import com.androidx.framework.test.utils.translucentStatusBar
import com.androidx.framework.ui.dialog.AppDialogUtils
import com.androidx.framework.ui.fragment.TemplateFragment
import com.androidx.framework.ui.inflater.AppStyleLayoutInflater
import com.google.gson.Gson
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        translucentStatusBar()
        Logger.addLogAdapter(AndroidLogAdapter())
        setContentView(R.layout.activity_main)
        textView.setOnClickListener {
            AppDialogUtils.show(this){
                dialogLayout {
                    R.layout.dialog
                }
            }
        }

        val gson = Gson()
//        RecyclerView(this).addOnScrollListener()
        lifecycleScope.launchWhenCreated {
            viewModel<MyViewModel>().observe<String,String>(this@MainActivity,
                "1111",
                "login",
                onLoading = {
                    Logger.e("loading")
                },onSuccess = {
                    Logger.e("=======$it======")

                },onError = {
                    Logger.e("=======1111======")

                })
        }
//
//        val fragment = AppFragment()
//        supportFragmentManager.commit {
//            replace(container.id,fragment)
//            addToBackStack(null)
//            show(fragment)
//        }
//        textView.setOnClickListener {
//            supportFragmentManager.commit {
//                replace(container.id,fragment)
//                show(fragment)
//            }
//        }
    }

}

//class MyFragment:BaseFragment<>(){
//    companion object{
//        fun getInstance():MyFragment{
//            return MyFragment()
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString("a","=====111")
//    }
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        Logger.e("onActivityCreated")
//        savedInstanceState?.let {
//            Logger.e(it.getString("a")?:"kkkk")
//
//        }
//
//    }
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        Logger.e("onViewStateRestored")
//        savedInstanceState?.let {
//            Logger.e(it.getString("a")?:"2222")
//
//        }
//
//
//    }
//}

class MyViewModel(application: Application) : AppViewModel(application),NetworkObserver {
    override fun onNetworkStateChanged(state: Int, type: Int, name: String?) {
        Logger.e("$state========$type=======$name")
    }

    @Service("login")
    private lateinit var myService: MyService
    suspend fun login(){

    }


    init {
        Logger.e("====>>>>${    application.isNetworkConnected}")

    }
    override fun onIOCCreated() {
        super.onIOCCreated()


    }
    override fun onCleared() {
        super.onCleared()
        Logger.e("==onCleared=>>")
    }


}

class MyService: NetworkBoundResource<String, String>() {

    override fun loadFromRemote(): ApiResponse<String> {
        return ApiResponse.create("11111")
    }

    override fun loadFromDB(params: String?): String? {
        return "222222"
    }

}

class AppFragment: TemplateFragment() {


    override val contentLayoutId: Int = R.layout.f_a

}



