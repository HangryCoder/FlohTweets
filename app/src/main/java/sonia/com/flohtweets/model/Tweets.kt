package sonia.com.flohtweets.model

import sonia.com.flohtweets.utils.Constants

data class Tweets(
    val tweetId: Int,
    val tweetUsername: String,
    val tweetMessage: String,
    val tweetUserProfile: String,
    val tweetType : Int = Constants.TWEET_ITEM
)