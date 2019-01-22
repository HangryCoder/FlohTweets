package sonia.com.flohtweets.flohtweets

import sonia.com.flohtweets.model.TwitterAPIResponse

class FlohTweetsPresenter(
    private var flohTweetView: FlohContract.FlohView<FlohTweetsPresenter>?,
    private val flohTweetsRepository: FlohTweetsRepository
) : FlohContract.FlohTweetsPresenter {

    override fun pullToRefresh() {
        flohTweetsRepository.getFlohTweets(object : FlohContract.FlohTweets.GetFlohTweetsCallback {
            override fun onSuccess(twitterAPIResponse: TwitterAPIResponse) {
                flohTweetView?.populateFlohTweets(twitterAPIResponse)
            }

            override fun onError(error: Throwable) {
                flohTweetView?.showErrorMessage(error)
            }

            override fun onTerminate() {
                flohTweetView?.hideLoader()
                flohTweetView?.hidePullToRefreshLoader()
            }

        })
    }

    override fun endlessScrolling(remainingUrl: String) {
        flohTweetsRepository.loadMoreFlohTweets(
            remainingUrl = remainingUrl,
            listener = object : FlohContract.FlohTweets.LoadMoreFlohTweetsCallback {
                override fun onSuccess(twitterAPIResponse: TwitterAPIResponse) {
                    flohTweetView?.appendOldFlohTweets(twitterAPIResponse)
                }

                override fun onError(error: Throwable) {
                    flohTweetView?.noMoreTweets(error)
                }

                override fun onTerminate() {
                    flohTweetView?.hideLoader()
                }
            })
    }

    override fun onDestroy() {
        flohTweetsRepository.dispose()
        flohTweetView = null
    }
}