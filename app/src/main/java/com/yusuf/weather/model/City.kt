package com.yusuf.weather.model


data class City(
    var distance : Integer,
    var title: String,
    var location_type: String,
    var woeid : Int,
    var latt_long : String
)