package dev.spiffocode.sigesmobile.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.spiffocode.sigesmobile.data.remote.AuthInterceptor
import dev.spiffocode.sigesmobile.data.remote.TokenAuthenticator
import dev.spiffocode.sigesmobile.data.remote.api.AuthApiService
import dev.spiffocode.sigesmobile.data.remote.api.BuildingApiService
import dev.spiffocode.sigesmobile.data.remote.api.EquipmentApiService
import dev.spiffocode.sigesmobile.data.remote.api.NotificationApiService
import dev.spiffocode.sigesmobile.data.remote.api.PasswordRecoveryApiService
import dev.spiffocode.sigesmobile.data.remote.api.ReportApiService
import dev.spiffocode.sigesmobile.data.remote.api.ReservationApiService
import dev.spiffocode.sigesmobile.data.remote.api.SpaceApiService
import dev.spiffocode.sigesmobile.data.remote.api.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://siges.lat/api/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY  // set to NONE for release
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .authenticator(tokenAuthenticator)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(LocalTime::class.java, object : JsonDeserializer<LocalTime> {
            override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) =
                LocalTime.parse(json.asString)
        })
        .registerTypeAdapter(LocalDate::class.java, object : JsonDeserializer<LocalDate> {
            override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) =
                LocalDate.parse(json.asString)
        })
        .registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
            override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) =
                LocalDateTime.parse(json.asString)
        })
        .create()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideBuildingApiService(retrofit: Retrofit): BuildingApiService =
        retrofit.create(BuildingApiService::class.java)

    @Provides
    @Singleton
    fun provideSpaceApiService(retrofit: Retrofit): SpaceApiService =
        retrofit.create(SpaceApiService::class.java)

    @Provides
    @Singleton
    fun provideEquipmentApiService(retrofit: Retrofit): EquipmentApiService =
        retrofit.create(EquipmentApiService::class.java)

    @Provides
    @Singleton
    fun provideReservationApiService(retrofit: Retrofit): ReservationApiService =
        retrofit.create(ReservationApiService::class.java)

    @Provides
    @Singleton
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService =
        retrofit.create(NotificationApiService::class.java)

    @Provides
    @Singleton
    fun providePasswordRecoveryApiService(retrofit: Retrofit): PasswordRecoveryApiService =
        retrofit.create(PasswordRecoveryApiService::class.java)

    @Provides
    @Singleton
    fun provideReportApiService(retrofit: Retrofit): ReportApiService =
        retrofit.create(ReportApiService::class.java)
}