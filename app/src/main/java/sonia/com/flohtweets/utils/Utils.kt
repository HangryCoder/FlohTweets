package sonia.com.flohtweets.utils

import android.graphics.Typeface
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.TextView
import sonia.com.flohtweets.BuildConfig

fun AppCompatActivity.showLogE(TAG: String, message: String) {
    if (BuildConfig.DEBUG) {
        Log.e(TAG, message)
    }
}

fun changeToolbarFont(collapsingToolbarLayout: CollapsingToolbarLayout) {
    val typeface = Typeface.createFromAsset(collapsingToolbarLayout.context.assets, "fonts/montserratbold.ttf")
    collapsingToolbarLayout.setCollapsedTitleTypeface(typeface)
    collapsingToolbarLayout.setExpandedTitleTypeface(typeface)
}