package sonia.com.flohtweets.activity

import android.arch.lifecycle.MutableLiveData
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
import sonia.com.flohtweets.adapter.TweetsAdapter
import sonia.com.flohtweets.model.Statuses
import sonia.com.flohtweets.model.TwitterAPIResponse
import sonia.com.flohtweets.utils.*
import sonia.com.flohtweets.viewmodel.TweetsViewModel
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

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

    private var nextResultsUrl = ""

    private lateinit var tweetsViewModel: TweetsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpToolbar()

        setUpRecyclerView()

        pullToRefresh()

        endlessScrolling()

        setUpViewModelAndFetchTweets()
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

            tweetsViewModel.refreshFlowTweets().observe(this, Observer { twitterResponse ->

                showLogE(TAG, "Pull to Refresh")
                swipeRefreshLayout.isRefreshing = false

                flohTweetsResponse(twitterResponse)
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
                                    loadMoreTweets()
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

    private fun loadMoreTweets() {
        tweetsViewModel.loadMoreTweets().observe(this, Observer { twitterResponse ->
            showLogE(TAG, "endlessScroll!!!!")
            if (twitterResponse != null) {
                val tweets = twitterResponse.statuses

                nextResultsUrl = twitterResponse.search_metadata.nextResultUrl

                hideProgressBarAndResetLoadingFlag()
                tweetsAdapter.appendMoreTweets(tweets)

                //Updating the liveData again!
                val liveDataTwitterResponse = (tweetsViewModel.twitterResponse as MutableLiveData).value
                liveDataTwitterResponse?.statuses = tweetsList as List<Statuses>
                liveDataTwitterResponse?.search_metadata?.nextResultUrl = nextResultsUrl ?: ""
            } else {
                hideProgressBarAndResetLoadingFlag()
                showToast(context = this@MainActivity, message = resources.getString(R.string.no_more_tweets))
            }
        })
    }

    private fun setUpViewModelAndFetchTweets() {
        tweetsViewModel = ViewModelProviders.of(this).get(TweetsViewModel::class.java)

        tweetsViewModel.getFlowTweets().observe(this, Observer { twitterResponse ->
            showLogE(TAG, "Normal Fetch")
            flohTweetsResponse(twitterResponse)
        })
    }

    private fun flohTweetsResponse(twitterResponse: TwitterAPIResponse?) {
        if (twitterResponse != null) {
            val tweets = twitterResponse.statuses

            nextResultsUrl = twitterResponse.search_metadata.nextResultUrl
            tweetsAdapter.addAll(tweets)

            showTweetsList()
        } else {
            showErrorMessage()
        }
    }

    private fun hideProgressBarAndResetLoadingFlag() {
        //Remove progress item
        tweetsList.removeAt(tweetsList.size - 1)
        tweetsAdapter.notifyItemRemoved(tweetsList.size)
        loading = false
    }

    private fun showTweetsList() {
        loadingLayout.visibility = View.INVISIBLE
        tweetsRecyclerView.visibility = View.VISIBLE
    }

    private fun showErrorMessage() {
        tweetsRecyclerView.visibility = View.INVISIBLE
        loadingLayout.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        loadingPleaseWaitText.text = resources.getString(R.string.no_internet_connection)
    }
}
