package com.androidx.framework.logic.api

import okhttp3.ResponseBody
import retrofit2.Converter

class ResponseBodyConvert<T>(private val bodyConvert: (value: ResponseBody)->ApiResponse<T>) : Converter<ResponseBody, ApiResponse<T>> {

    override fun convert(value: ResponseBody): ApiResponse<T>? {
        return try {
            return bodyConvert(value)
        }catch (e:Exception){
            ApiResponse.create<T>(e)
        }


    }
}