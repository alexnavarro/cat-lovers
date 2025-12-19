package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.network.Result

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> =
    when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this as Result<R>
        is Result.NetworkError -> this as Result<R>
    }