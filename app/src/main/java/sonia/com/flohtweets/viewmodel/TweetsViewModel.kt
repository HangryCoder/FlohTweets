package sonia.com.flohtweets.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import sonia.com.flohtweets.data.TweetsRepository
import sonia.com.flohtweets.model.TwitterAPIResponse
import sonia.com.flohtweets.utils.showLogE

class TweetsViewModel : ViewModel() {

    private var tweetsRepository: TweetsRepository? = null
    private val TAG by lazy {
        TweetsViewModel::class.java.simpleName
    }
    // private var nextResultsUrl: String? = ""

    init {
        tweetsRepository = TweetsRepository()
    }

    private var twitterResponse: LiveData<TwitterAPIResponse>? = null

    fun getFlowTweets(): LiveData<TwitterAPIResponse> {
        if (twitterResponse == null) {
            twitterResponse = tweetsRepository?.getTweets()
            // nextResultsUrl = (twitterResponse as MutableLiveData).value?.search_metadata?.nextResultUrl
            // showLogE(TAG, "NextUrl $nextResultsUrl")
        }
        return twitterResponse as LiveData<TwitterAPIResponse>
    }

    fun refreshFlowTweets(): LiveData<TwitterAPIResponse> {
        twitterResponse = tweetsRepository?.getTweets()
        return twitterResponse as LiveData<TwitterAPIResponse>
    }

    fun loadMoreTweets(): LiveData<TwitterAPIResponse> {
        // if (twitterResponse == null) {
        val nextResultUrl = (twitterResponse as MutableLiveData).value?.search_metadata?.nextResultUrl
        showLogE(TAG, "NextUrl $nextResultUrl")

        twitterResponse = tweetsRepository?.loadMoreTweets(remainingUrl = nextResultUrl!!)
        //nextResultUrl = (twitterResponse as MutableLiveData).value?.search_metadata?.nextResultUrl
        //}
        return twitterResponse as LiveData<TwitterAPIResponse>
    }

}