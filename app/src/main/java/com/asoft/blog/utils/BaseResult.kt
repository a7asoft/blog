package com.asoft.blog.utils

sealed class BaseResult<out T : Any, out U : Any, out E : Any> {
    data class Success<T : Any>(val data: T) : BaseResult<T, Nothing, Nothing>()
    data class Error<U : Any>(val rawResponse: String?) : BaseResult<Nothing, U, Nothing>()
    data class Exception<E : Any>(val exception: E) : BaseResult<Nothing, Nothing, E>()
}