package sonia.com.flohtweets.flohtweets

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import sonia.com.flohtweets.model.TwitterToken
import sonia.com.flohtweets.network.RestClient
import sonia.com.flohtweets.utils.Base64Encoding
import sonia.com.flohtweets.utils.Constants

class FlohTweetsRepository : FlohContract.FlohTweets {

    private var disposable: Disposable? = null

    override fun getFlohTweets(listener: FlohContract.FlohTweets.GetFlohTweetsCallback) {
        disposable = getAuthToken()
            .flatMap { twitterToken ->
                return@flatMap RestClient.getTweetAPI().getFlohTweets(
                    tweetName = Constants.TWEET_NAME,
                    count = Constants.TWEET_COUNT,
                    header = "${twitterToken.token_type} ${twitterToken.access_token}",
                    contentType = Constants.CONTENT_TYPE
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                listener.onTerminate()
            }
            .subscribe({ twitterResponse ->
                listener.onSuccess(twitterResponse)
            }, { error ->
                listener.onError(error)
            })
    }

    private fun getAuthToken(): Single<TwitterToken> {
        val encodedKey = Base64Encoding.encodeStringToBase64(
            key =
            Constants.CONSUMER_KEY + ":" + Constants.CONSUMER_SECRET
        )

        return RestClient.getTweetAPI().getAuthToken(
            header = "Basic $encodedKey",
            grantType = Constants.GRANT_TYPE
        )
    }

    override fun loadMoreFlohTweets(
        remainingUrl: String,
        listener: FlohContract.FlohTweets.LoadMoreFlohTweetsCallback
    ) {
        disposable = getAuthToken()
            .flatMap { twitterToken ->
                return@flatMap RestClient.getTweetAPI().loadMoreFlohTweets(
                    url = Constants.TWEETS_API + remainingUrl,
                    header = "${twitterToken.token_type} ${twitterToken.access_token}",
                    contentType = Constants.CONTENT_TYPE
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ twitterResponse ->
                listener.onSuccess(twitterResponse)
            }, { error ->
                listener.onError(error)
            })
    }

    fun dispose() {
        disposable?.dispose()
    }
}