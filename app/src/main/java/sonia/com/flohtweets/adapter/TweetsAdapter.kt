package sonia.com.flohtweets.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import sonia.com.flohtweets.R
import sonia.com.flohtweets.model.Tweets
import kotlinx.android.synthetic.main.layout_tweet.view.*
import sonia.com.flohtweets.utils.Constants

class TweetsAdapter(
    private val context: Context,
    private val tweetsList: ArrayList<Tweets?>
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
                    .load(/*tweets.tweetUserProfile*/context.resources.getDrawable(R.drawable.ic_launcher_background))
                    .into(viewHolder.itemView.tweetUserProfile)

                viewHolder.itemView.tweetUsername.text = tweets?.tweetUsername
                //viewHolder.itemView.tweetMessage.text = tweets.tweetMessage
            }
            else -> {
                //(viewHolder as LoadMoreHolder).itemView.loadMoreProgressBar
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

    fun clear() {
        tweetsList.clear()
        notifyDataSetChanged()
    }

    fun addAll(freshTweetsList: List<Tweets>) {
        this.tweetsList.addAll(freshTweetsList)
        notifyDataSetChanged()
    }

    inner class TweetsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class LoadMoreHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}