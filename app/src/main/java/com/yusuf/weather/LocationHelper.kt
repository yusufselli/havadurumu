package com.yusuf.weather


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener




class LocationHelper(private val activity: Activity, private val savedInstanceState: Bundle?) {

    private val TAG = " LOCATION HELPER"

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private val ADDRESS_REQUESTED_KEY = "address-request-pending"
    private val LOCATION_ADDRESS_KEY = "location-address"

    private val LAT_KEY = "LAT_KEY"
    private val LNG_KEY = "LNG_KEY"


    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var lastLocation: Location? = null

    private var addressRequested = false


    private var addressOutput = ""
    private var cityOutput = ""
    private var countyOutput = ""
    private var resultReceiver: AddressResultReceiver


    init {
        resultReceiver = AddressResultReceiver(Handler())

        updateValuesFromBundle(savedInstanceState)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        updateUIWidgets()

        fetchAddressButtonHandler()
    }

    fun onStart() {

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getAddress()
        }
    }


    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        ADDRESS_REQUESTED_KEY.let {

            if (savedInstanceState.keySet().contains(it)) {
                addressRequested = savedInstanceState.getBoolean(it)
            }
        }

        LOCATION_ADDRESS_KEY.let {
            if (savedInstanceState.keySet().contains(it)) {
                addressOutput = savedInstanceState.getString(it).orEmpty()
                displayAddressOutput()
            }
        }

        LAT_KEY.let {
            if (savedInstanceState.keySet().contains(it)) {
                cityOutput = savedInstanceState.getString(it).orEmpty()
                displayAddressOutput()
            }
        }

        LNG_KEY.let {
            if (savedInstanceState.keySet().contains(it)) {
                countyOutput = savedInstanceState.getString(it).orEmpty()
                displayAddressOutput()
            }
        }


    }


    fun fetchAddressButtonHandler() {
        if (lastLocation != null) {
            startIntentService()
            return
        }

        addressRequested = true
        updateUIWidgets()
    }

    private fun startIntentService() {

        val intent = Intent(activity, FetchAddressIntentService::class.java).apply {

            putExtra(Constants.RECEIVER, resultReceiver)

            putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
        }

        activity.startService(intent)
    }

    @SuppressLint("MissingPermission")
    fun getAddress() {

        fusedLocationClient?.lastLocation?.addOnSuccessListener(
            activity,
            OnSuccessListener { location ->
                if (location == null) {
                    Log.w("TAG", "onSuccess:null")
                    return@OnSuccessListener
                }

                lastLocation = location

                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Toast.makeText(activity, "no_geocoder_available", Toast.LENGTH_LONG)
                        .show()
                    return@OnSuccessListener
                }

                // If the user pressed the fetch address button before we had the location,
                // this will be set to true indicating that we should kick off the intent
                // service after fetching the location.
                if (addressRequested) startIntentService()
            })?.addOnFailureListener(activity) { e -> Log.w("TAG", "getLastLocation:onFailure", e) }
    }


    private fun displayAddressOutput() {

        mOnLocationListener?.getLocation(cityOutput, countyOutput)
    }


    private fun updateUIWidgets() {
        mOnLocationListener?.isLoading(!addressRequested)
    }

    fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState

        with(savedInstanceState) {
            // Save whether the address has been requested.
            putBoolean(ADDRESS_REQUESTED_KEY, addressRequested)

            // Save the address string.
            putString(LOCATION_ADDRESS_KEY, addressOutput)

            putString(LAT_KEY, cityOutput)

            putString(LNG_KEY, countyOutput)
        }


    }

    private inner class AddressResultReceiver internal constructor(
        handler: Handler
    ) : ResultReceiver(handler) {


        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            // Display the address string or an error message sent from the intent service.
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY).orEmpty()
            cityOutput = resultData.getString(Constants.LAT_DATA_KEY).orEmpty()
            countyOutput = resultData.getString(Constants.LNG_DATA_KEY).orEmpty()
            displayAddressOutput()

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //Toast.makeText(activity, R.string.address_found, Toast.LENGTH_SHORT).show()
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            addressRequested = false
            updateUIWidgets()
        }
    }


    fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            AlertDialog.Builder(activity)
                .setMessage("permission_rationale")
                .setPositiveButton(activity.getString(android.R.string.ok)) { d, _ ->
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()

        } else {
            Log.i(TAG, "Requesting permission")
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    private var mOnLocationListener: OnLocationListener? = null

    fun setOnLocationListener(listener: OnLocationListener) {
        mOnLocationListener = listener
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")

        if (requestCode != REQUEST_PERMISSIONS_REQUEST_CODE) return

        when {
            grantResults.isEmpty() ->
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            grantResults[0] == PackageManager.PERMISSION_GRANTED -> // Permission granted.
                getAddress()
            else -> // Permission denied.

            {


                AlertDialog.Builder(activity)
                    .setMessage("permission_denied_explanation")
                    .setPositiveButton("settings") { d, _ ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                        intent.data = uri
                        activity.startActivity(intent)
                        d.dismiss()
                    }
                    .show()


            }

        }
    }

    interface OnLocationListener {
        fun isLoading(flag: Boolean)
        fun getLocation(lat: String, lng: String)
    }

}