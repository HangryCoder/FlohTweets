package sonia.com.flohtweets.data

import android.arch.lifecycle.LiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import sonia.com.flohtweets.model.Statuses
import sonia.com.flohtweets.model.TwitterToken
import sonia.com.flohtweets.network.RestClient
import sonia.com.flohtweets.network.TweetAPI
import sonia.com.flohtweets.utils.Base64Encoding
import sonia.com.flohtweets.utils.Constants
import android.arch.lifecycle.MutableLiveData
import sonia.com.flohtweets.model.TwitterAPIResponse


class TweetsRepository {

    private var disposable: Disposable? = null
    private var tweetRestClient: TweetAPI = RestClient.getTweetAPI()

    fun getTweets(): LiveData<TwitterAPIResponse> {

        val data = MutableLiveData<TwitterAPIResponse>()

        getAuthToken()
            .flatMap { twitterToken ->
                return@flatMap tweetRestClient.getFlohTweets(
                    tweetName = Constants.TWEET_NAME,
                    count = Constants.TWEET_COUNT,
                    header = "${twitterToken.token_type} ${twitterToken.access_token}",
                    contentType = Constants.CONTENT_TYPE
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ twitterResponse ->
                data.value = twitterResponse
            }, { error ->
                data.value = null
            })
        return data
    }

    private fun getAuthToken(): Single<TwitterToken> {
        val encodedKey = Base64Encoding.encodeStringToBase64(
            key =
            Constants.CONSUMER_KEY + ":" + Constants.CONSUMER_SECRET
        )

        return tweetRestClient.getAuthToken(
            header = "Basic $encodedKey",
            grantType = Constants.GRANT_TYPE
        )
    }

    fun loadMoreTweets(remainingUrl: String): LiveData<TwitterAPIResponse> {

        val twitterAPIResponse = MutableLiveData<TwitterAPIResponse>()

        disposable = getAuthToken()
            .flatMap { twitterToken ->
                return@flatMap tweetRestClient.loadMoreFlohTweets(
                    url = Constants.TWEETS_API + remainingUrl,
                    header = "${twitterToken.token_type} ${twitterToken.access_token}",
                    contentType = Constants.CONTENT_TYPE
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ twitterResponse ->
                twitterAPIResponse.value = twitterResponse
            }, { error ->
                twitterAPIResponse.value = null
            })
        return twitterAPIResponse
    }
}