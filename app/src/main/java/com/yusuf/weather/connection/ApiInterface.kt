package com.yusuf.weather.connection

import com.yusuf.weather.model.City
import com.yusuf.weather.model.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiInterface {

    @GET("/api/location/search/")
    fun fetchCities(@Query("lattlong") lattlong: String): Call<Array<City>>


    @GET("/api/location/{woeid}")
    fun fetchDetail(@Path("woeid") woeid: Int): Call<Weather>
}