package com.yusuf.weather.ui.weather

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yusuf.weather.R
import com.yusuf.weather.connection.ApiClient
import com.yusuf.weather.model.Weather
import com.yusuf.weather.weatherIcon
import kotlinx.android.synthetic.main.list_weather_day.*
import kotlinx.android.synthetic.main.list_weather_day_detail.*
import kotlinx.android.synthetic.main.weather_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class WeatherFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.getInt("WOE_ID")?.let {
            fetchWeather(it)
        }

        container.setBackgroundColor( Color.parseColor(context?.resources?.getStringArray(R.array.colors)?.random()))

    }

    private fun fetchWeather(woeid: Int) {
        ApiClient.instance.fetchDetail(woeid).enqueue(object : Callback<Weather> {

            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                response.body()?.let { weather ->
                    txtCity.text = "${weather.parent.title}/${weather.title}"

                    context?.let{
                        listView.adapter = ConsolidatedWeatherAdapter(it,weather.consolidated_weather)
                    }

                    weather.consolidated_weather[0].let { consolidatedWeather ->
                        weatherIcon.weatherIcon(consolidatedWeather.weather_state_abbr)
                        maxTemp.text = "${consolidatedWeather.max_temp.roundToLong()}"
                        txtAbout.text =
                            "Bugün: Şu anki hava durumu ${consolidatedWeather.weather_state_name}. Sıcaklık ${consolidatedWeather.the_temp.roundToInt()}°, bugünkü en yüksek tahmini ${
                                consolidatedWeather.max_temp.roundToInt()
                            }°."
                        txtWindSpeed.text = "${consolidatedWeather.wind_speed}"
                        txtWindDirection.text = "${consolidatedWeather.wind_direction}"
                        txtAirPressure.text = "${consolidatedWeather.air_pressure}"
                        txtHumidity.text = "${consolidatedWeather.humidity}"
                        txtVisibility.text = "${consolidatedWeather.visibility}"
                        txtPredictability.text = "${consolidatedWeather.predictability}"

                    }


                }

            }

            override fun onFailure(call: Call<Weather>, t: Throwable) {

            }

        })
    }
}