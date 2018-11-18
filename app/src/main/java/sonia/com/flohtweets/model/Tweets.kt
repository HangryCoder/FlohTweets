package sonia.com.flohtweets.model

data class Tweets(
    val tweetId: Int,
    val tweetUsername: String,
    val tweetMessage: String,
    val tweetUserProfile: String
)