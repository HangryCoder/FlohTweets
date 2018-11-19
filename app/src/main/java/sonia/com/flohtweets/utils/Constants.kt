package sonia.com.flohtweets.utils

class Constants {

    companion object {

        const val CONSUMER_KEY = "tztLEEadXjcpSE45lARLbCqxG"

        const val CONSUMER_SECRET = "udid4ZHelf816gYSCUr1mznlvW4KvIzuxmishGHARUshVjEKwX"

        const val ENDLESS_SCROLL_DELAY = 2000L

        //Retrofit

        const val MAIN_URL = "https://api.twitter.com/"

        const val TWEETS_API = "1.1/search/tweets.json"

        const val TWITTER_TOKEN = "oauth2/token"

        const val RETROFIT_TIMEOUT: Long = 2

        const val CONTENT_TYPE = "application/json"

        const val GRANT_TYPE: String = "client_credentials"

        //API Call

        const val TWEET_NAME = "FlohNetwork"

        const val TWEET_COUNT = 5

        //Tweets Adapter

        const val LOAD_MORE_ITEM = 0

        const val TWEET_ITEM = 1
    }
}