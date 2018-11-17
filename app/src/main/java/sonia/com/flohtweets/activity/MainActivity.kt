package sonia.com.flohtweets.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import sonia.com.flohtweets.R
import sonia.com.flohtweets.network.RestClient
import sonia.com.flohtweets.adapter.TweetsAdapter
import sonia.com.flohtweets.model.Tweets
import sonia.com.flohtweets.utils.Constants
import sonia.com.flohtweets.utils.VerticalItemDecoration
import java.nio.charset.StandardCharsets
import java.util.*

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

        //val data = API_KEY.toByte(StandardCharsets.UTF_8)
        //  val base64 = //Base64.encodeToString(data, Base64.DEFAULT)

        RestClient.getTweetAPI().getAuthToken(
            header = "Basic dHp0TEVFYWRYamNwU0U0NWxBUkxiQ3F4Rzp1ZGlkNFpIZWxmODE2Z1lTQ1VyMW16bmx2VzRLdkl6" +
                    "dXhtaXNoR0hBUlVzaFZqRUt3WA==",
            grantType = Constants.GRANT_TYPE
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

            }

        RestClient.getTweetAPI().getFlohTweets(
            tweetName = "nasa",
            resultType = "popular",
            count = 5,
            header = "Bearer AAAAAAAAAAAAAAAAAAAAAKqy8wAAAAAAZbaTrRDd1t%2FYUybanASNr3W2%2B2s%3DhkIq21jrT9zbzViaEgxsap2D1J2nlHgUlTtzl41po3egIhaon2",
            contentType = Constants.CONTENT_TYPE
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

            }
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
