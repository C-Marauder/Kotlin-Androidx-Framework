package com.androidx.framework.logic.api

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class CallToResponseAdapterFactory: CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val mClz = getRawType(returnType)
        if (mClz != Response::class.java){
            return null
        }
        if (returnType !is ParameterizedType){
            return null
        }
        val responseType = getParameterUpperBound(0, returnType)
        return CallToResponseAdapter<Any>(responseType)
    }
}

internal class CallToResponseAdapter<T>(private val responseType:Type):CallAdapter<T,Response<T>>{
    @Suppress("UNCHECKED_CAST")
    override fun adapt(call: Call<T>): Response<T> {
        return call.execute().body() as Response<T>
    }
    override fun responseType(): Type = responseType

}
