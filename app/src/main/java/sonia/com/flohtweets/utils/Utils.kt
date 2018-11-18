package sonia.com.flohtweets.utils

import android.support.v7.app.AppCompatActivity
import android.util.Log
import sonia.com.flohtweets.BuildConfig

fun AppCompatActivity.showLogE(TAG: String, message: String) {
    if (BuildConfig.DEBUG) {
        Log.e(TAG, message)
    }
}