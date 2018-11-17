package sonia.com.flohtweets.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import sonia.com.flohtweets.R
import sonia.com.flohtweets.adapter.TweetsAdapter
import sonia.com.flohtweets.model.Tweets
import sonia.com.flohtweets.utils.VerticalItemDecoration

class MainActivity : AppCompatActivity() {

    private var tweetsList: ArrayList<Tweets> = ArrayList()
    private lateinit var tweetsAdapter: TweetsAdapter

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
    }
}
