package com.yusuf.weather

import android.app.IntentService
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
object Constants {
    const val SUCCESS_RESULT = 0

    const val FAILURE_RESULT = 1

    private const val PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress"

    const val RECEIVER = "$PACKAGE_NAME.RECEIVER"

    const val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"

    const val LOCATION_DATA_EXTRA = "$PACKAGE_NAME.LOCATION_DATA_EXTRA"

    const val LAT_DATA_KEY = "$PACKAGE_NAME.LAT_DATA_KEY"

    const val LNG_DATA_KEY = "$PACKAGE_NAME.LNG_DATA_KEY"
}

class FetchAddressIntentService : IntentService("FetchAddress") {

    private val TAG = "FetchAddressService"

    private var receiver: ResultReceiver? = null


    override fun onHandleIntent(intent: Intent?) {
        var errorMessage = ""

        receiver = intent?.getParcelableExtra(Constants.RECEIVER)

        if (intent == null || receiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.")
            return
        }


        val location = intent.getParcelableExtra<Location>(Constants.LOCATION_DATA_EXTRA)

        if (location == null) {
            errorMessage = "no_location_data_provided"
            Log.wtf(TAG, errorMessage)
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)
            return
        }

        deliverResultToReceiver(
            Constants.SUCCESS_RESULT,"Success",location.latitude.toString(),location.longitude.toString()
        )
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private fun deliverResultToReceiver(
        resultCode: Int,
        message: String,
        lat: String?=null,
        lng: String?=null
    ) {
        val bundle = Bundle().apply {
            putString(Constants.RESULT_DATA_KEY, message)
            putString(Constants.LAT_DATA_KEY, lat)
            putString(Constants.LNG_DATA_KEY, lng)
        }
        receiver?.send(resultCode, bundle)
    }


}