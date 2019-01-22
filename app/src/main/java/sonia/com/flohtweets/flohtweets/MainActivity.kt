package sonia.com.flohtweets.flohtweets

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_loading.*
import sonia.com.flohtweets.R
import sonia.com.flohtweets.adapter.TweetsAdapter
import sonia.com.flohtweets.model.Statuses
import sonia.com.flohtweets.model.TwitterAPIResponse
import sonia.com.flohtweets.utils.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), FlohContract.FlohView<FlohTweetsPresenter> {

    private val TAG by lazy {
        MainActivity::class.java.simpleName
    }

    private var tweetsList: ArrayList<Statuses?> = ArrayList()
    private lateinit var tweetsAdapter: TweetsAdapter

    private var loading = false
    private var pastVisibleItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    private var handler: Handler? = null

    override var presenter = FlohTweetsPresenter(this, FlohTweetsRepository())

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
                            showLogE(TAG, message = "Last Item Wow ! ${presenter.nextResultsUrl}")

                            tweetsList.add(null)
                            tweetsAdapter.notifyItemInserted(tweetsList.size - 1)

                            handler?.postDelayed({

                                if (presenter.nextResultsUrl != null && presenter.nextResultsUrl!!.isNotEmpty()) {
                                    presenter.endlessScrolling()
                                } else {
                                    dismissEndlessScrolling()
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

    override fun showLoader() {
        loadingLayout.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        loadingLayout.visibility = View.INVISIBLE
    }

    override fun populateFlohTweets(twitterAPIResponse: TwitterAPIResponse) {
        val tweets = twitterAPIResponse.statuses
        tweetsAdapter.addAll(tweets)

        swipeRefreshLayout.visibility = View.VISIBLE
    }

    override fun appendOldFlohTweets(twitterAPIResponse: TwitterAPIResponse) {
        val tweets = twitterAPIResponse.statuses

        dismissEndlessScrolling()
        tweetsAdapter.appendMoreTweets(tweets)
    }

    override fun dismissEndlessScrolling() {
        //Remove progress item
        tweetsList.removeAt(tweetsList.size - 1)
        tweetsAdapter.notifyItemRemoved(tweetsList.size)
        loading = false
    }

    override fun noMoreTweets(error: Throwable) {
        showLogE(TAG, "Error ${error.printStackTrace()}")
        showToast(context = this@MainActivity, message = resources.getString(R.string.no_more_tweets))
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

    override fun onDestroy() {
        super.onDestroy()

        presenter.onDestroy()
    }
}
