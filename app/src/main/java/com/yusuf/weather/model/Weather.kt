package com.yusuf.weather.model


data class Weather (
    val consolidated_weather: List<ConsolidatedWeather>,
    val time: String,
    val sunRise: String,
    val sunSet: String,
    val timezoneName: String,
    val parent: Parent,
    val sources: List<Source>,
    val title: String,
    val locationType: String,
    val woeid: Long,
    val lattLong: String,
    val timezone: String
)

data class ConsolidatedWeather (
    val id: Long,
    val weather_state_name: String,
    val weather_state_abbr: String,
    val wind_direction_compass: String,
    val created: String,
    val applicable_date: String,
    val min_temp: Double,
    val max_temp: Double,
    val the_temp: Double,
    val wind_speed: Double,
    val wind_direction: Double,
    val air_pressure: Double,
    val humidity: Long,
    val visibility: Double,
    val predictability: Long



)

data class Parent (
    val title: String,
    val locationType: String,
    val woeid: Long,
    val lattLong: String
)

data class Source (
    val title: String,
    val slug: String,
    val url: String,
    val crawlRate: Long
)
