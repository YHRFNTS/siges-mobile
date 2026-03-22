package dev.spiffocode.sigesmobile.data.remote

import retrofit2.Response

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int, val message: String) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

/** Safely execute a Retrofit suspend call and wrap result. */
suspend fun <T> safeApiCall(call: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            when {
                response.code() == 204 || response.code() == 205 -> {
                    @Suppress("UNCHECKED_CAST")
                    NetworkResult.Success(Unit as T)
                }
                body != null -> NetworkResult.Success(body)
                else -> NetworkResult.Error(response.code(), "Empty response body")
            }
        } else {
            NetworkResult.Error(
                response.code(),
                response.errorBody()?.string() ?: "Unknown error"
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error(-1, e.localizedMessage ?: "Network exception")
    }
}