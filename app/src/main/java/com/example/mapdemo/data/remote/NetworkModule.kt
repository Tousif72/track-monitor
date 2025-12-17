package com.example.mapdemo.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

data object NetworkModule {
    private const val BASE_URL = "https://dev.nebarex.com/"
    private const val API_KEY_HEADER = "x-api-key"
    private const val API_KEY = "A9fK3P2Qx7MZ8JrC5LwH6BvTnE4DYSd"

    fun provideApi(): TrackingApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request()
                    .newBuilder()
                    .header(API_KEY_HEADER, API_KEY)
                    .build()
                chain.proceed(req)
            }
            .addInterceptor(logging)
            .build()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(TrackingApi::class.java)
    }
}
