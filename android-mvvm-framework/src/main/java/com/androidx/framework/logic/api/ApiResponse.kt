package com.androidx.framework.logic.api

import retrofit2.Response
import java.util.regex.Pattern

sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error.message ?: "unknown error")
        }

        fun <T> create(data: T): ApiResponse<T> {
            return ApiSuccessResponse(data)
        }
    }
}

/**
 * separate class for HTTP 204 resposes so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T>(
    val data: T) : ApiResponse<T>() {

}

data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()