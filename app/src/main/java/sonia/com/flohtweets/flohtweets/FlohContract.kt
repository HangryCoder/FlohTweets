package sonia.com.flohtweets.flohtweets

import sonia.com.flohtweets.model.TwitterAPIResponse

interface FlohContract {

    interface FlohView<FlohTweetsPresenter> {

        var presenter: FlohTweetsPresenter

        fun showLoader()

        fun hideLoader()

        fun showPullToRefreshLoader()

        fun hidePullToRefreshLoader()

        fun populateFlohTweets(twitterAPIResponse: TwitterAPIResponse)

        fun appendOldFlohTweets(twitterAPIResponse: TwitterAPIResponse)

        fun noMoreTweets(error: Throwable)

        fun showErrorMessage(throwable: Throwable)
    }

    interface FlohTweets {

        interface OnFinishedListener {
            fun onSuccess(twitterAPIResponse: TwitterAPIResponse)
            fun onError(error: Throwable)
            fun onTerminate()
        }

        interface GetFlohTweetsCallback : OnFinishedListener {}
        interface LoadMoreFlohTweetsCallback : OnFinishedListener {}

        fun getFlohTweets(listener: GetFlohTweetsCallback)
        fun loadMoreFlohTweets(remainingUrl: String, listener: LoadMoreFlohTweetsCallback)
    }

    interface FlohTweetsPresenter {

        fun pullToRefresh()

        fun endlessScrolling()

        fun onDestroy()
    }
}