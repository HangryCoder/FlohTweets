package sonia.com.flohtweets.model

import android.databinding.BaseObservable
import com.google.gson.annotations.SerializedName

data class Statuses(
    @SerializedName("text")
    val tweetMessage: String,
    @SerializedName("user")
    val twitterUser: TwitterUser
) : BaseObservable()