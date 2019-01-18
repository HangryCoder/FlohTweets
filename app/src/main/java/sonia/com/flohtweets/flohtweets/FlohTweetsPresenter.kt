package sonia.com.flohtweets.flohtweets

import sonia.com.flohtweets.model.TwitterAPIResponse

class FlohTweetsPresenter(
    private var flohTweetView: FlohContract.FlohView<FlohTweetsPresenter>?,
    private val flohTweetsRepository: FlohTweetsRepository
) : FlohContract.FlohTweetsPresenter {

    override fun pullToRefresh() {
        flohTweetsRepository.getFlohTweets(object : FlohContract.FlohTweets.GetFlohTweetsCallback {
            override fun onSuccess(twitterAPIResponse: TwitterAPIResponse) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(error: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTerminate() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    override fun endlessScrolling() {
        flohTweetsRepository.loadMoreFlohTweets(
            remainingUrl = "",
            listener = object : FlohContract.FlohTweets.LoadMoreFlohTweetsCallback {
                override fun onSuccess(twitterAPIResponse: TwitterAPIResponse) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onError(error: Throwable) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onTerminate() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
    }

    override fun onDestroy() {
        flohTweetsRepository.dispose()
        flohTweetView = null
    }
}