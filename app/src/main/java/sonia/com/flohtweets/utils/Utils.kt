package sonia.com.flohtweets.utils

import android.content.Context
import android.graphics.Typeface
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import sonia.com.flohtweets.BuildConfig

fun showLogE(TAG: String, message: String) {
    if (BuildConfig.DEBUG) {
        Log.e(TAG, message)
    }
}

fun changeToolbarFont(collapsingToolbarLayout: CollapsingToolbarLayout) {
    val typeface = Typeface.createFromAsset(collapsingToolbarLayout.context.assets, "fonts/montserratbold.ttf")
    collapsingToolbarLayout.setCollapsedTitleTypeface(typeface)
    collapsingToolbarLayout.setExpandedTitleTypeface(typeface)
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}