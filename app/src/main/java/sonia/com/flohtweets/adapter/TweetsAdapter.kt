package sonia.com.flohtweets.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onBindViewHolder(p0: TweetsHolder, p1: Int) {
        val tweets = tweetsList[p1]

    }

    inner class TweetsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}