package dev.spiffocode.sigesmobile.data.remote

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int, val message: String) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

/** Safely execute a Retrofit suspend call and wrap result. */
suspend fun <T> safeApiCall(call: suspend () -> retrofit2.Response<T>): NetworkResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) NetworkResult.Success(body)
            else NetworkResult.Error(response.code(), "Empty response body")
        } else {
            NetworkResult.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
        }
    } catch (e: Exception) {
        NetworkResult.Error(-1, e.localizedMessage ?: "Network exception")
    }
}