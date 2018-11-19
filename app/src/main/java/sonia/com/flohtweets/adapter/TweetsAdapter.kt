package sonia.com.flohtweets.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import sonia.com.flohtweets.R
import kotlinx.android.synthetic.main.layout_tweet.view.*
import sonia.com.flohtweets.model.Statuses
import sonia.com.flohtweets.utils.Constants

class TweetsAdapter(
    private val context: Context,
    private val tweetsList: ArrayList<Statuses?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            Constants.TWEET_ITEM -> {
                val view = LayoutInflater.from(context).inflate(R.layout.layout_tweet, container, false)
                TweetsHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.layout_load_more, container, false)
                LoadMoreHolder(view)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        when (viewHolder) {
            is TweetsHolder -> {

                val tweets = tweetsList[position]
                Glide.with(context)
                    .load(tweets?.twitterUser?.userProfileURL)
                    .into(viewHolder.itemView.tweetUserProfile)

                viewHolder.itemView.tweetUsername.text = tweets?.twitterUser?.name
                viewHolder.itemView.tweetScreename.text = "@${tweets?.twitterUser?.userName}"
                viewHolder.itemView.tweetMessage.text = tweets?.tweetMessage
            }
            else -> {
            }
        }

    }

    override fun getItemCount(): Int = tweetsList.size

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return if (tweetsList[position] != null) {
            Constants.TWEET_ITEM
        } else {
            Constants.LOAD_MORE_ITEM
        }
    }

    fun addAll(freshTweetsList: List<Statuses>) {
        tweetsList.clear()
        this.tweetsList.addAll(freshTweetsList)
        notifyDataSetChanged()
    }

    fun appendMoreTweets(tweetsList: List<Statuses>) {
        this.tweetsList.addAll(tweetsList)
    }

    inner class TweetsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class LoadMoreHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}