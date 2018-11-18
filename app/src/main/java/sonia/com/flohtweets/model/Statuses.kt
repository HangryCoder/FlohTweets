package sonia.com.flohtweets.model

import com.google.gson.annotations.SerializedName

data class Statuses(
    @SerializedName("text")
    val tweetMessage: String,
    @SerializedName("user")
    val twitterUser: TwitterUser)