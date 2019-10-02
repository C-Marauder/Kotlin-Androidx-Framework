package com.androidx.framework.logic.vm

import android.app.Application
import android.util.Log

import androidx.lifecycle.*
import com.androidx.framework.logic.api.ApiEmptyResponse
import com.androidx.framework.logic.api.ApiErrorResponse
import com.androidx.framework.logic.api.ApiResponse
import com.androidx.framework.logic.api.ApiSuccessResponse
import com.androidx.framework.logic.network.NetworkObserver
import com.androidx.framework.logic.network.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Service(val serviceName: String)


@Suppress("LeakingThis")
abstract class AppViewModel(application: Application) : AndroidViewModel(application),LifecycleEventObserver {
    private val mNetworkBoundResources: ConcurrentHashMap<String, NetworkBoundResource<*, *>> by lazy {
        ConcurrentHashMap<String, NetworkBoundResource<*, *>>()
    }

    init {
        if (this is NetworkObserver){
            NetworkUtils.addNetworkObserver(this)
        }
        this::class.java.run {
            try {
                declaredFields.filter {
                    it.isAnnotationPresent(Service::class.java)
                }.forEach { field ->
                    field.isAccessible = true
                    field.getAnnotation(Service::class.java)?.serviceName?.let {
                        val mInstance = Class.forName(field.type.canonicalName!!).newInstance()
                        field.set(this@AppViewModel, mInstance)
                        if (mInstance is NetworkBoundResource<*, *>){
                            mNetworkBoundResources[it] = mInstance

                        }
                    }

                }
            } catch (e: Exception) {

            }
        }
    }

    open fun onIOCCreated(){

    }
    open fun onIOCStarted(){

    }
    open fun onIOCResume(){

    }
    open fun onIOCPause(){

    }
    open fun onIOCStop(){

    }
    open fun onIOCDestroy(){

    }
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event){
            Lifecycle.Event.ON_CREATE-> onIOCCreated()
            Lifecycle.Event.ON_START-> onIOCStarted()
            Lifecycle.Event.ON_RESUME-> onIOCResume()
            Lifecycle.Event.ON_PAUSE-> onIOCPause()
            Lifecycle.Event.ON_STOP-> onIOCStop()
            Lifecycle.Event.ON_DESTROY-> onIOCDestroy()
            else -> {}
        }
    }
    @Suppress("UNCHECKED_CAST")
    suspend fun <ResultType, RequestType> observe(
        lifecycleOwner: LifecycleOwner,
        params:RequestType,
        service: String,
        onLoading: () -> Unit,
        onSuccess: (data: ResultType?) -> Unit,
        onError: (message: String) -> Unit
    ) {

        withContext(viewModelScope.coroutineContext) {
            (mNetworkBoundResources[service]!! as NetworkBoundResource<ResultType, RequestType>).request(params)
        }.observe(lifecycleOwner) {
            when (it.status) {
                Status.LOADING -> onLoading()
                Status.SUCCESS -> onSuccess(it.data)
                Status.ERROR -> onError(it.message!!)
            }
        }
    }
}

abstract class NetworkBoundResource<ResultType, RequestType> {
    protected abstract fun loadFromRemote(): ApiResponse<ResultType>
    fun request(params: RequestType): LiveData<Resource<ResultType>> =
        liveData<Resource<ResultType>>(Dispatchers.IO) {
            emit(Resource.loading(null))//loading
            this.emitSource(requestFromDB(params))
            val resource = when (val result = loadFromRemote()) {
                is ApiSuccessResponse -> Resource.success(
                    result.data
                )
                is ApiErrorResponse -> Resource.error(
                    result.errorMessage,
                    null
                )
                is ApiEmptyResponse -> Resource.success(
                    null
                )
            }
            emit(resource)
        }

    protected abstract fun loadFromDB(params: RequestType?): ResultType?
    private fun requestFromDB(params: RequestType?): LiveData<Resource<ResultType>> =
        liveData(Dispatchers.IO) {
            loadFromDB(params)?.let {
                emit(Resource.success(it))
            }


        }
}


//// ResultType: Type for the Resource data.
//// RequestType: Type for the API response.
//abstract class NetworkBoundResource<ResultType, RequestType> {
//    init {
//
//
//    }
//    // Called to save the result of the API response into the database
//    @WorkerThread
//    protected abstract fun saveCallResult(item: RequestType)
//
//    // Called with the data in the database to decide whether to fetch
//    // potentially updated data from the network.
//    @MainThread
//    protected abstract fun shouldFetch(data: ResultType?): Boolean
//
//    // Called to get the cached data from the database.
//    @MainThread
//    protected abstract fun loadFromDb(): LiveData<ResultType>
//
//    // Called to create the API call.
//    @MainThread
//    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
//
//    // Called when the fetch fails. The child class may want to reset components
//    // like rate limiter.
//    protected open fun onFetchFailed() {}
//
//    // Returns a LiveData object that represents the resource that's implemented
//    // in the base class.
//    fun asLiveData(): LiveData<ResultType> = TODO()
//}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(
                Status.ERROR,
                data,
                msg
            )
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(
                Status.LOADING,
                data,
                null
            )
        }
    }
}