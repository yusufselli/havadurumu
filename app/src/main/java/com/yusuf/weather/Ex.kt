package com.yusuf.weather

import android.app.Activity
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso


fun Activity.shortToast(msg:String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}


fun Fragment.shortToast(msg:String){
    activity?.shortToast(msg)
}

fun ImageView.picasso(path:String){
    Picasso.get().load(path).into(this)
}


fun ImageView.weatherIcon(abbr:String){
    picasso("https://www.metaweather.com/static/img/weather/png/$abbr.png")
}