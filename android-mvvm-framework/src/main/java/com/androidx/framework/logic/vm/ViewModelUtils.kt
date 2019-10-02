package com.androidx.framework.logic.vm

import android.app.Application
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner


object ViewModelUtils {
    lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory

    fun init(application: Application){
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
}

inline fun <reified VM: AppViewModel> LifecycleOwner.vm(){
    val savedStateRegistryOwner:SavedStateRegistryOwner
    val vmStoreOwner:ViewModelStore
    val application = when(this){
        is Fragment-> {
            vmStoreOwner = this.viewModelStore
            savedStateRegistryOwner = this
            activity?.application!!
        }

        is AppCompatActivity->{
            savedStateRegistryOwner = this
            vmStoreOwner  = this.viewModelStore
            application
        }
        else-> throw Exception("")
    }

    val savedStateViewModelFactory = SavedStateViewModelFactory(application,savedStateRegistryOwner)
    ViewModelProvider(vmStoreOwner,savedStateViewModelFactory)[VM::class.java]
}

inline fun <reified VM:AppViewModel> LifecycleOwner.viewModel():VM{
    return when (this) {
        is AppCompatActivity -> {
            val viewModel = viewModels<VM>().value
            lifecycle.addObserver(viewModel)
            viewModel
        }
        is Fragment -> {
            val viewModel = viewModels<VM>().value
            lifecycle.addObserver(viewModel)
            viewModel
        }
        else -> throw Exception("${LifecycleOwner::class.java.simpleName} is not AppCompatActivity or Fragment")
    }
}






