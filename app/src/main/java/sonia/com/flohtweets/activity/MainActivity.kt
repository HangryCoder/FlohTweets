package sonia.com.flohtweets.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import sonia.com.flohtweets.R
import sonia.com.flohtweets.network.RestClient
import sonia.com.flohtweets.adapter.TweetsAdapter
import sonia.com.flohtweets.model.Tweets
import sonia.com.flohtweets.utils.Base64Encoding
import sonia.com.flohtweets.utils.Constants
import sonia.com.flohtweets.utils.VerticalItemDecoration
import sonia.com.flohtweets.utils.showLogE
import java.util.*

class MainActivity : AppCompatActivity() {

    private var tweetsList: ArrayList<Tweets> = ArrayList()
    private lateinit var tweetsAdapter: TweetsAdapter

    private var disposable: Disposable? = null

    private var loading = true
    private var pastVisibleItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    private val TAG by lazy {
        MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchTweets()

        tweetsAdapter = TweetsAdapter(
            context = this@MainActivity,
            tweetsList = tweetsList
        )
        tweetsRecyclerView.addItemDecoration(VerticalItemDecoration(10))

        tweetsRecyclerView.adapter = tweetsAdapter

        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        swipeRefreshLayout.setOnRefreshListener {
            fetchFlohTweets()
        }

        // endlessScrolling()
    }

    private fun endlessScrolling() {
        tweetsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false
                            showLogE(TAG, message = "Last Item Wow !")
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        })
    }

    private fun fetchFlohTweets() {
        val encodedKey = Base64Encoding.encodeStringToBase64(
            key =
            Constants.CONSUMER_KEY + ":" + Constants.CONSUMER_SECRET
        )

        disposable = RestClient.getTweetAPI().getAuthToken(
            header = "Basic $encodedKey",
            grantType = Constants.GRANT_TYPE
        ).flatMap { twitterToken ->
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
                swipeRefreshLayout.isRefreshing = false
            }
            .subscribe({ success ->

            }, { error -> })
    }

    private fun fetchTweets() {
        var tweets = Tweets(
            tweetId = 1, tweetUsername = "Sonia Wadji",
            tweetUserProfile = "dfvfv", tweetMessage = ""
        )
        tweetsList.add(tweets)

        tweets = Tweets(
            tweetId = 2, tweetUsername = "Stephen D'Souza",
            tweetUserProfile = "dfvfv", tweetMessage = ""
        )
        tweetsList.add(tweets)

        tweets = Tweets(
            tweetId = 3, tweetUsername = "Krupa Bhat",
            tweetUserProfile = "dfvfv", tweetMessage = ""
        )
        tweetsList.add(tweets)

        tweets = Tweets(
            tweetId = 4, tweetUsername = "Suhail Shaikh",
            tweetUserProfile = "dfvfv", tweetMessage = ""
        )
        tweetsList.add(tweets)

        tweets = Tweets(
            tweetId = 1, tweetUsername = "Shilpa Wadji",
            tweetUserProfile = "dfvfv", tweetMessage = ""
        )
        tweetsList.add(tweets)

        tweets = Tweets(
            tweetId = 1, tweetUsername = "Shilpa Wadji",
            tweetUserProfile = "dfvfv", tweetMessage = "",
            tweetType = Constants.LOAD_MORE_ITEM
        )
        tweetsList.add(tweets)
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable?.dispose()
    }
}
