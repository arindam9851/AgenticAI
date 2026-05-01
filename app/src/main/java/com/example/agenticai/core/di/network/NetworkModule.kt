package com.example.agenticai.core.di.network

import android.content.Context
import com.example.agenticai.data.api_service.ApiService
import com.example.agenticai.data.interceptor_token.HeaderInterceptor
import com.example.agenticai.data.interceptor_token.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideCache(
        @ApplicationContext context: Context
    ): Cache {
        val cacheSize = 10L * 1024 * 1024 // 10MB
        return Cache(File(context.cacheDir, "http_cache"), cacheSize)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        headerInterceptor: HeaderInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        authenticator: TokenAuthenticator
    ): OkHttpClient {

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authenticator)
            .addNetworkInterceptor { chain ->

                val response = chain.proceed(chain.request())

                response.newBuilder()
                    .header(
                        "Cache-Control",
                        "public, max-age=60"
                    )
                    .build()
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}