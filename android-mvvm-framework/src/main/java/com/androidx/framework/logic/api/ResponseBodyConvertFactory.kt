package com.androidx.androidmvvmframework.vm.retrofit

import com.androidx.framework.logic.api.ApiResponse
import com.androidx.framework.logic.api.ResponseBodyConvert
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.nio.charset.Charset

class ResponseBodyConvertFactory(private val bodyConvert: (value:ResponseBody,adapter: TypeAdapter<*>)-> ApiResponse<*>): Converter.Factory() {
    private val gson : Gson by lazy {
        Gson()
    }
    private lateinit var adapter: TypeAdapter<*>
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        adapter = gson.getAdapter(TypeToken.get(type))
        return RequestBodyConverter(gson,adapter)
    }
    @Suppress("UNCHECKED_CAST")
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
       val adapter = gson.getAdapter(TypeToken.get(type))
        return ResponseBodyConvert{
            bodyConvert(it,adapter) as ApiResponse<Any>
        }
    }
}

internal class RequestBodyConverter<T>(private val gson: Gson,private val adapter: TypeAdapter<T>):Converter<T, RequestBody>{
    companion object{
        private val MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8")
        private val UTF_8 = Charset.forName("UTF-8")
    }
    override fun convert(value: T): RequestBody? {
        val buffer = Buffer()
        val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
    }

}