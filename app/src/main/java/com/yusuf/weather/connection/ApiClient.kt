package com.yusuf.weather.connection


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object ApiClient {
    private var retrofit: ApiInterface? = null
    private val BASE_URL ="https://www.metaweather.com/"



    val instance: ApiInterface
        get() {
            val logging =  HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient =  OkHttpClient.Builder()
            httpClient.addInterceptor(logging)

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build().create(ApiInterface::class.java)
            }
            return retrofit!!
        }
}