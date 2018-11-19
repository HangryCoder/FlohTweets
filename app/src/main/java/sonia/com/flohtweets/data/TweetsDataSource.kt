package sonia.com.flohtweets.data

import sonia.com.flohtweets.model.Statuses

interface TweetsDataSource {

    interface LoadTweetsCallback {

        fun onTweetsLoaded(tweets: List<Statuses>)

        fun onTweetsNotAvailable(error: Throwable)

        fun onTerminate()
    }

    interface LoadMoreTweetsCallback {

        fun onTweetsLoaded(tweets: List<Statuses>)

        fun onTweetsNotAvailable(error: Throwable)
    }

    fun getTweets(callback: LoadTweetsCallback)

    fun loadMoreTweets(remainingUrl: String, callback: LoadMoreTweetsCallback)

}