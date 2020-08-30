package com.yusuf.weather.ui.weather

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.yusuf.weather.R
import com.yusuf.weather.model.ConsolidatedWeather
import com.yusuf.weather.weatherIcon
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong


class ConsolidatedWeatherAdapter(mContext: Context, var mData: List<ConsolidatedWeather>) : BaseAdapter() {
    private var inflater: LayoutInflater = LayoutInflater.from(mContext)
    private var holder: ViewHolder? = null
    override fun getCount(): Int {
        return mData.count()
    }

    override fun getItem(position: Int): Any {
        return mData[position]
    }

    override fun getItemId(position: Int): Long {
        return mData[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var customView:View? = convertView
        if (customView == null) {
            customView = inflater.inflate(R.layout.list_weather, null)
            holder = ViewHolder()
            holder?.iconImageView = customView.findViewById(R.id.weatherIcon) as ImageView
            holder?.dayLabel = customView.findViewById(R.id.txtDay) as TextView
            holder?.maxTempLabel = customView.findViewById(R.id.txtMaxTemp) as TextView
            holder?.minTempLabel = customView.findViewById(R.id.txtMinTemp) as TextView
            customView.tag = holder
        } else {
            holder = convertView?.tag as ViewHolder
        }

       mData[position].let { item->
           holder?.dayLabel?.text = dayName(item.applicable_date)
           holder?.maxTempLabel?.text = "${item.max_temp.roundToLong()}"
           holder?.minTempLabel?.text = "${item.min_temp.roundToLong()}"
           holder?.iconImageView?.weatherIcon((item.weather_state_abbr))
       }

        return customView!!
    }
    private fun dayName(inputDate: String?): String? {
        var date: Date? = null
        try {
            date = SimpleDateFormat("yyyy-MM-dd").parse(inputDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return SimpleDateFormat("EEEE").format(date)
    }

    private class ViewHolder {
        var dayLabel: TextView? = null
        var maxTempLabel: TextView? = null
        var minTempLabel: TextView? = null
        var iconImageView: ImageView? = null
    }
}