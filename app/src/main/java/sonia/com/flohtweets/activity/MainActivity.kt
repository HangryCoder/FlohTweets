package sonia.com.flohtweets.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import sonia.com.flohtweets.R
import sonia.com.flohtweets.network.RestClient
import sonia.com.flohtweets.adapter.TweetsAdapter
import sonia.com.flohtweets.model.Statuses
import sonia.com.flohtweets.model.TwitterToken
import sonia.com.flohtweets.utils.*
import sonia.com.flohtweets.viewmodel.TweetsViewModel
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

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

    private lateinit var tweetsViewModel: TweetsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpToolbar()

        setUpRecyclerView()

        pullToRefresh()

        endlessScrolling()

        tweetsViewModel = ViewModelProviders.of(this).get(TweetsViewModel::class.java)

        tweetsViewModel.getFlowTweets().observe(this, Observer { twitterResponse ->
            showLogE(TAG, "Normal Fetch")
            if (twitterResponse != null) {
                val tweets = twitterResponse.statuses

                nextResultsUrl = twitterResponse.search_metadata.nextResultUrl
                tweetsAdapter.addAll(tweets)

                showTweetsList()
            } else {
                swipeRefreshLayout.visibility = View.INVISIBLE
                loadingLayout.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                loadingPleaseWaitText.text = resources.getString(R.string.no_internet_connection)
            }
        })
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        changeToolbarFont(toolbar_layout)
    }

    private fun setUpRecyclerView() {
        tweetsAdapter = TweetsAdapter(
            context = this@MainActivity,
            tweetsList = tweetsList
        )
        tweetsRecyclerView.addItemDecoration(VerticalItemDecoration(10))

        tweetsRecyclerView.adapter = tweetsAdapter
    }

    private fun pullToRefresh() {
        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        swipeRefreshLayout.setOnRefreshListener {
            //fetchFlohTweets()
            tweetsViewModel.refreshFlowTweets().observe(this, Observer { twitterResponse ->
                showLogE(TAG, "Pull to Refresh")
                swipeRefreshLayout.isRefreshing = false

                if (twitterResponse != null) {
                    val tweets = twitterResponse.statuses

                    nextResultsUrl = twitterResponse.search_metadata.nextResultUrl
                    tweetsAdapter.addAll(tweets)

                    showTweetsList()
                } else {
                    swipeRefreshLayout.visibility = View.INVISIBLE
                    loadingLayout.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    loadingPleaseWaitText.text = resources.getString(R.string.no_internet_connection)
                }

            })
        }
    }

    private fun endlessScrolling() {
        handler = Handler()

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
                            recyclerView.post {
                                tweetsAdapter.notifyItemInserted(tweetsList.size - 1)
                            }

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
            key =
            Constants.CONSUMER_KEY + ":" + Constants.CONSUMER_SECRET
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

    private fun showErrorMessage(error: Throwable) {
        showLogE(TAG, "Error ${error.printStackTrace()}")

        swipeRefreshLayout.visibility = View.INVISIBLE
        loadingLayout.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        loadingPleaseWaitText.text = resources.getString(R.string.no_internet_connection)
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

        disposable?.dispose()
    }
}
