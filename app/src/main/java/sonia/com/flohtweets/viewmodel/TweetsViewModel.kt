package sonia.com.flohtweets.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import sonia.com.flohtweets.data.TweetsRepository
import sonia.com.flohtweets.model.TwitterAPIResponse

class TweetsViewModel : ViewModel() {

    private var tweetsRepository: TweetsRepository? = null

    init {
        tweetsRepository = TweetsRepository()
    }

    private val TAG by lazy {
        TweetsViewModel::class.java.simpleName
    }

    private var twitterResponse: LiveData<TwitterAPIResponse>? = null

    fun getFlowTweets(): LiveData<TwitterAPIResponse> {
        if (twitterResponse == null) {
            twitterResponse = tweetsRepository?.getTweets()
        }
        return twitterResponse as LiveData<TwitterAPIResponse>
    }

}