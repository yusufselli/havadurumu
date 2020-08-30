package com.yusuf.weather.ui.city

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.yusuf.weather.MainActivity
import com.yusuf.weather.R
import com.yusuf.weather.connection.ApiClient
import com.yusuf.weather.model.City
import kotlinx.android.synthetic.main.city_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CityFragment : Fragment() {


    private val cityList = arrayListOf<City>();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.city_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        val location = (activity as MainActivity).getCurrentLocation()
        fetchCities( if (location.isNullOrEmpty()) "41.015137, 28.979530" else location)
    }

    private fun init() {

        listView.setOnItemClickListener { parent, view, position, id ->

            val bundle = Bundle()
            bundle.putInt("WOE_ID", cityList[position].woeid)
            findNavController(view).navigate(R.id.action_cityFragment_to_weatherFragment, bundle);
        }

    }

    private fun fetchCities(location:String) {

        ApiClient.instance.fetchCities(location).enqueue(object : Callback<Array<City>> {
            override fun onResponse(call: Call<Array<City>>, response: Response<Array<City>>) {
                Log.i("Cities", response.body().toString())


                response.body()?.let { cities ->
                    cityList.clear()
                    cityList.addAll(cities)
                    context?.let { mContext ->
                        val mAdaptor = ArrayAdapter<String>(
                            mContext,
                            android.R.layout.simple_list_item_1,
                            android.R.id.text1,
                            cities.map { it.title })
                        listView.adapter = mAdaptor
                    }
                }


            }

            override fun onFailure(call: Call<Array<City>>, t: Throwable) {

            }

        })

    }

}