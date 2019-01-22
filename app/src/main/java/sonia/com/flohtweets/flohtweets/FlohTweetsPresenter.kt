package sonia.com.flohtweets.flohtweets

import sonia.com.flohtweets.model.TwitterAPIResponse

class FlohTweetsPresenter(
    private var flohTweetView: FlohContract.FlohView<FlohTweetsPresenter>?,
    private val flohTweetsRepository: FlohTweetsRepository
) : FlohContract.FlohTweetsPresenter {

    var nextResultsUrl: String? = ""

    override fun pullToRefresh() {
        flohTweetsRepository.getFlohTweets(object : FlohContract.FlohTweets.GetFlohTweetsCallback {
            override fun onSuccess(twitterAPIResponse: TwitterAPIResponse) {
                flohTweetView?.populateFlohTweets(twitterAPIResponse)

                nextResultsUrl = twitterAPIResponse.search_metadata.nextResultUrl
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

    override fun endlessScrolling() {
        flohTweetsRepository.loadMoreFlohTweets(
            remainingUrl = nextResultsUrl!!,
            listener = object : FlohContract.FlohTweets.LoadMoreFlohTweetsCallback {
                override fun onSuccess(twitterAPIResponse: TwitterAPIResponse) {
                    nextResultsUrl = twitterAPIResponse.search_metadata.nextResultUrl

                    flohTweetView?.appendOldFlohTweets(twitterAPIResponse)
                }

                override fun onError(error: Throwable) {
                    flohTweetView?.noMoreTweets(error)
                }

                override fun onTerminate() {
                    flohTweetView?.dismissEndlessScrolling()
                }
            })
    }

    override fun onDestroy() {
        flohTweetsRepository.dispose()
        flohTweetView = null
    }
}