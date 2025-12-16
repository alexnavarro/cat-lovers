package com.alexandrenavarro.catlovers.data.network

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class NetworkError(val exception: Throwable) : Result<Nothing>()
}
