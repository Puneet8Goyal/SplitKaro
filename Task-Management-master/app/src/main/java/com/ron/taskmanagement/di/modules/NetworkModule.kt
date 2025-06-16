package com.ron.taskmanagement.di.modules

import com.ron.taskmanagement.di.network.RestApis
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {


    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://colormoon.in/quick_loans/api/V1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient())
            .build()
    }

    @Singleton
    @Provides
    fun getRestApis(retrofit: Retrofit): RestApis {
       return retrofit.create(RestApis::class.java)
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        val okhttpClientBuilder = OkHttpClient.Builder()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        okhttpClientBuilder.addInterceptor(Interceptor { chain ->
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
//                .header("Authorization", "Bearer ${"IConstants.JwtKeys.token_key"}")
                .build()
            chain.proceed(request)
        })
        val timeOutSec = 45
        okhttpClientBuilder.connectTimeout(timeOutSec.toLong(), TimeUnit.SECONDS)
        okhttpClientBuilder.readTimeout(timeOutSec.toLong(), TimeUnit.SECONDS)
        okhttpClientBuilder.writeTimeout(timeOutSec.toLong(), TimeUnit.SECONDS)
        okhttpClientBuilder.addInterceptor(loggingInterceptor)
        return okhttpClientBuilder.build()
    }

}