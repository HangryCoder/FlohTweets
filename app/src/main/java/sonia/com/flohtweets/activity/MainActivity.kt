package sonia.com.flohtweets.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_loading.*
import sonia.com.flohtweets.BuildConfig
import sonia.com.flohtweets.R
import sonia.com.flohtweets.network.RestClient
import sonia.com.flohtweets.adapter.TweetsAdapter
import sonia.com.flohtweets.flohtweets.FlohContract
import sonia.com.flohtweets.flohtweets.FlohTweetsPresenter
import sonia.com.flohtweets.flohtweets.FlohTweetsRepository
import sonia.com.flohtweets.model.Statuses
import sonia.com.flohtweets.model.TwitterAPIResponse
import sonia.com.flohtweets.model.TwitterToken
import sonia.com.flohtweets.utils.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), FlohContract.FlohView<FlohTweetsPresenter> {

    override var presenter = FlohTweetsPresenter(this, FlohTweetsRepository())

    override fun showLoader() {
        loadingLayout.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        loadingLayout.visibility = View.INVISIBLE
    }

    override fun populateFlohTweets(twitterAPIResponse: TwitterAPIResponse) {
        val tweets = twitterAPIResponse.statuses

        nextResultsUrl = twitterAPIResponse.search_metadata.nextResultUrl

        tweetsAdapter.addAll(tweets)

        swipeRefreshLayout.visibility = View.VISIBLE
    }

    override fun appendOldFlohTweets(twitterAPIResponse: TwitterAPIResponse) {
        val tweets = twitterAPIResponse.statuses
        tweetsAdapter.appendMoreTweets(tweets)
    }

    override fun noMoreTweets(error: Throwable) {
        showLogE(TAG, "Error ${error.printStackTrace()}")
        showToast(context = this@MainActivity, message = resources.getString(R.string.no_more_tweets))
    }

    override fun showPullToRefreshLoader() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hidePullToRefreshLoader() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun showErrorMessage(throwable: Throwable) {
        showLogE(TAG, "Error ${throwable.printStackTrace()}")

        swipeRefreshLayout.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        loadingPleaseWaitText.text = resources.getString(R.string.no_internet_connection)
    }

    private val TAG by lazy {
        MainActivity::class.java.simpleName
    }

    private var tweetsList: ArrayList<Statuses?> = ArrayList()
    private lateinit var tweetsAdapter: TweetsAdapter

    private var disposable: Disposable? = null

    private var loading = false
    private var pastVisibleItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    private var handler: Handler? = null

    private var nextResultsUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        changeToolbarFont(toolbar_layout)

        handler = Handler()

        setUpTweetsAdapter()

        settingUpPullToRefresh()

        endlessScrolling()

        presenter.pullToRefresh()
    }

    private fun setUpTweetsAdapter() {
        tweetsAdapter = TweetsAdapter(
            context = this@MainActivity,
            tweetsList = tweetsList
        )
        tweetsRecyclerView.addItemDecoration(VerticalItemDecoration(10))

        tweetsRecyclerView.adapter = tweetsAdapter
    }

    private fun settingUpPullToRefresh() {
        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        swipeRefreshLayout.setOnRefreshListener {
            presenter.pullToRefresh()
        }
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

                    if (!loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = true
                            showLogE(TAG, message = "Last Item Wow ! $nextResultsUrl")

                            tweetsList.add(null)
                            tweetsAdapter.notifyItemInserted(tweetsList.size - 1)

                            handler?.postDelayed({

                                if (nextResultsUrl != null && nextResultsUrl.isNotEmpty()) {
                                    loadMoreFlohTweets(nextResultsUrl)
                                } else {
                                    hideProgressBarAndResetLoadingFlag()
                                    showToast(
                                        context = this@MainActivity,
                                        message = resources.getString(R.string.no_more_tweets)
                                    )
                                }
                            }, Constants.ENDLESS_SCROLL_DELAY)
                        }
                    }
                }
            }
        })
    }

    private fun hideProgressBarAndResetLoadingFlag() {
        //Remove progress item
        tweetsList.removeAt(tweetsList.size - 1)
        tweetsAdapter.notifyItemRemoved(tweetsList.size)
        loading = false
    }

    private fun fetchFlohTweets() {
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
                swipeRefreshLayout.isRefreshing = false
            }
            .subscribe({ twitterResponse ->
                val tweets = twitterResponse.statuses

                nextResultsUrl = twitterResponse.search_metadata.nextResultUrl
                tweetsAdapter.addAll(tweets)

                showTweetsList()

            }, { error ->
                showErrorMessage(error)
            })
    }

    private fun getAuthToken(): Single<TwitterToken> {
        val encodedKey = Base64Encoding.encodeStringToBase64(
            key = BuildConfig.ConsumerKey + ":" + BuildConfig.ConsumerSecret

        )

        return RestClient.getTweetAPI().getAuthToken(
            header = "Basic $encodedKey",
            grantType = Constants.GRANT_TYPE
        )
    }

    private fun showTweetsList() {
        loadingLayout.visibility = View.INVISIBLE
        swipeRefreshLayout.visibility = View.VISIBLE
    }

    private fun loadMoreFlohTweets(remainingUrl: String) {
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

                val tweets = twitterResponse.statuses

                nextResultsUrl = twitterResponse.search_metadata.nextResultUrl

                hideProgressBarAndResetLoadingFlag()
                tweetsAdapter.appendMoreTweets(tweets)


            }, { error ->
                showLogE(TAG, "Error ${error.printStackTrace()}")
                hideProgressBarAndResetLoadingFlag()
                showToast(context = this@MainActivity, message = resources.getString(R.string.no_more_tweets))
            })
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onDestroy()
    }
}
