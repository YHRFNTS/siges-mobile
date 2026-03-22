package dev.spiffocode.sigesmobile.data.remote

import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.data.remote.api.AuthApiService
import dev.spiffocode.sigesmobile.data.remote.dto.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton
import dagger.Lazy

/**
 * OkHttp Authenticator — se dispara automáticamente en cada 401.
 * Intenta refrescar el accessToken una sola vez.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val session: SessionManager,
    private val authApi: Lazy<AuthApiService>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("X-Retry-After-Refresh") != null) {
            runBlocking { session.clearSession() }
            return null
        }

        val newAccessToken = runBlocking {
            val refreshToken = session.refreshToken ?: return@runBlocking null
            try {
                val refreshResponse = authApi.get().refresh(RefreshRequest(refreshToken))
                if (refreshResponse.isSuccessful) {
                    refreshResponse.body()?.accessToken?.also { token ->
                        session.updateAccessToken(token)
                    }
                } else {
                    session.clearSession()
                    null
                }
            } catch (e: Exception) {
                session.clearSession()
                null
            }
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .header("X-Retry-After-Refresh", "true")
            .build()
    }
}

@Singleton
class AuthInterceptor @Inject constructor(
    private val session: SessionManager
) : okhttp3.Interceptor {

    override fun intercept(chain: okhttp3.Interceptor.Chain): Response {
        val token = session.accessToken

        val request = if (token != null) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
