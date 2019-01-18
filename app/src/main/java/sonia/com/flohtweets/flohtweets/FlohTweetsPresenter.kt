package sonia.com.flohtweets.flohtweets

class FlohTweetsPresenter(
    private var flohTweetView: FlohContract.FlohView<FlohTweetsPresenter>?,
    private val flohTweetsRepository: FlohTweetsRepository
) : FlohContract.FlohTweetsPresenter {

    override fun pullToRefresh() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun endlessScrolling() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        flohTweetsRepository.dispose()
        flohTweetView = null
    }
}