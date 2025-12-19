package com.app.shortlovers.core.network

import com.app.shortlovers.core.models.MainResponse
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

        private val okHttpClient: OkHttpClient =
                OkHttpClient.Builder()
                        .addInterceptor(DirectusErrorInterceptor())
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build()

        private val gson =
                GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        .setLenient()
                        .registerTypeAdapter(MainResponse::class.java, MainResponseDeserializer())
                        .create()

        val gsonFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

        val api: ApiService by lazy {
                Retrofit.Builder()
                        .baseUrl("https://app.shortlovers.id")
                        .client(okHttpClient)
                        .addConverterFactory(gsonFactory)
                        .build()
                        .create(ApiService::class.java)
        }
}
