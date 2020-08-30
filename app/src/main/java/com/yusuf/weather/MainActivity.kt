package com.yusuf.weather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI


class MainActivity : AppCompatActivity() {
    private lateinit var mLocationHelper: LocationHelper

    private lateinit var navController: NavController
    private var location =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)


        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
       // NavigationUI.setupActionBarWithNavController(this, navController);

        mLocationHelper =
            LocationHelper(
                this,
                savedInstanceState
            )


        mLocationHelper.setOnLocationListener(object : LocationHelper.OnLocationListener {
            override fun isLoading(flag: Boolean) {
                // findViewById<ConstraintLayout>(R.id.container).snack("$flag")
            }


            override fun getLocation(lat: String, lng: String) {
                print("$lat $lng")
                location = "$lat,$lng"
            }

        })
    }

    fun getCurrentLocation(): String? {
        mLocationHelper.getAddress()
        return location
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        YsfPermission.singletonBuild(this)
            .onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null as DrawerLayout?)
    }
}