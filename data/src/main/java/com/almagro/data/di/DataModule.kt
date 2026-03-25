package com.almagro.data.di

import android.content.Context
import java.util.concurrent.TimeUnit
import androidx.room.Room
import com.almagro.data.BuildConfig
import com.almagro.data.api.WeatherApi
import com.almagro.data.dao.WeatherDao
import com.almagro.data.database.AtmosDatabase
import com.almagro.data.repository.WeatherRepositoryImpl
import com.almagro.domain.repository.WeatherLocalRepository
import com.almagro.domain.repository.WeatherRemoteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindWeatherRemoteRepository(impl: WeatherRepositoryImpl): WeatherRemoteRepository

    @Binds
    abstract fun bindWeatherLocalRepository(impl: WeatherRepositoryImpl): WeatherLocalRepository

    companion object {

        private const val APP_ID = "appid"
        private const val BASE_URL = "https://api.openweathermap.org/"
        private const val UNITS = "units"
        private const val UNITS_PARAMETER = "metric"

        @Provides
        @Singleton
        fun provideJson(): Json = Json { ignoreUnknownKeys = true }

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            val apiKeyInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val urlWithApiKey = originalRequest.url.newBuilder()
                    .addQueryParameter(APP_ID, BuildConfig.WEATHER_API_KEY)
                    .addQueryParameter(UNITS, UNITS_PARAMETER)
                    .build()
                chain.proceed(originalRequest.newBuilder().url(urlWithApiKey).build())
            }

            return OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(
                            HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            }
                        )
                    }
                }
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()

        @Provides
        @Singleton
        fun provideWeatherApi(retrofit: Retrofit): WeatherApi =
            retrofit.create(WeatherApi::class.java)

        @Provides
        @Singleton
        fun provideAtmosDatabase(@ApplicationContext context: Context): AtmosDatabase =
            Room.databaseBuilder(context, AtmosDatabase::class.java, "atmos.db").build()

        @Provides
        @Singleton
        fun provideWeatherDao(database: AtmosDatabase): WeatherDao = database.weatherDao()
    }
}
