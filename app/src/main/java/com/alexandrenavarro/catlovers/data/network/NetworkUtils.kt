package com.alexandrenavarro.catlovers.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun <T> safeApiCall(apiCall: suspend () -> retrofit2.Response<T>): Result<T> =
    withContext(Dispatchers.IO) {
        try {
            val response = apiCall()

            if (!response.isSuccessful) {
                return@withContext Result.Error(Exception(response.message()))
            }

            Result.Success(response.body() ?: error("Empty response body"))

        } catch (e: Exception) {
            Result.NetworkError(e)
        }
    }