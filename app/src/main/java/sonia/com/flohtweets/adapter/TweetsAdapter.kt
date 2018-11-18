package sonia.com.flohtweets.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import sonia.com.flohtweets.R
import sonia.com.flohtweets.model.Tweets
import kotlinx.android.synthetic.main.layout_tweet.view.*

class TweetsAdapter(
    private val context: Context,
    private val tweetsList: ArrayList<Tweets>
) : RecyclerView.Adapter<TweetsAdapter.TweetsHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TweetsHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_tweet, p0, false)
        return TweetsHolder(view)
    }

    override fun getItemCount(): Int = tweetsList.size

    fun clear() {
        tweetsList.clear()
        notifyDataSetChanged()
    }

    fun addAll(freshTweetsList: List<Tweets>) {
        this.tweetsList.addAll(freshTweetsList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(p0: TweetsHolder, p1: Int) {
        val tweets = tweetsList[p1]

        Glide.with(context)
            .load(/*tweets.tweetUserProfile*/context.resources.getDrawable(R.drawable.ic_launcher_background))
            .into(p0.itemView.tweetUserProfile)

        p0.itemView.tweetUsername.text = tweets.tweetUsername
        //p0.itemView.tweetMessage.text = tweets.tweetMessage
    }

    inner class TweetsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}