package dev.spiffocode.sigesmobile.data.remote

import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.data.remote.api.AuthApiService
import dev.spiffocode.sigesmobile.data.remote.dto.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton
import dagger.Lazy

/**
 * Interceptor encargado de:
 * 1. Añadir el token de acceso a las peticiones (si existe).
 * 2. Detectar errores 401 o 403 (sesión expirada o inválida).
 * 3. Intentar el refresco del token automáticamente.
 * 4. Si falla el refresco, limpiar la sesión.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val session: SessionManager,
    private val authApi: Lazy<AuthApiService>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = session.accessToken
        val timezone = java.util.TimeZone.getDefault().id

        // 1. Añadir el token y el timezone
        val authRequestBuilder = originalRequest.newBuilder()
            .header("X-Timezone", timezone)

        if (token != null && originalRequest.header("Authorization") == null) {
            authRequestBuilder.header("Authorization", "Bearer $token")
        }

        val authRequest = authRequestBuilder.build()
        val response = chain.proceed(authRequest)

        // 2. Si recibimos 401 o 403 y no es una petición de auth base (como login o refresh mismo)
        if ((response.code == 401 || response.code == 403) && 
            !authRequest.url.toString().contains("auth/login") &&
            !authRequest.url.toString().contains("auth/refresh")
        ) {
            synchronized(this) {
                // Volvemos a obtener el token por si otro hilo ya lo refrescó
                val currentToken = session.accessToken
                
                // Si el token ya cambió, reintentamos con el nuevo
                if (currentToken != token && currentToken != null) {
                    response.close()
                    val newRetryRequest = authRequest.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                    return chain.proceed(newRetryRequest)
                }

                // Intentamos refrescar
                val refreshToken = session.refreshToken
                if (refreshToken != null) {
                    val refreshResponse = runBlocking {
                        try {
                            authApi.get().refresh(RefreshRequest(refreshToken))
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (refreshResponse?.isSuccessful == true) {
                        val newTokens = refreshResponse.body()
                        if (newTokens != null) {
                            runBlocking { session.updateAccessToken(newTokens.accessToken) }
                            
                            response.close()
                            val newRetryRequest = authRequest.newBuilder()
                                .header("Authorization", "Bearer ${newTokens.accessToken}")
                                .build()
                            return chain.proceed(newRetryRequest)
                        }
                    }
                }

                // Si llegamos aquí, el refresco falló o no había refresh token
                runBlocking { session.clearSession() }
            }
        }

        return response
    }
}
