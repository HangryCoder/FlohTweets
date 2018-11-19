package sonia.com.flohtweets.viewmodel

import android.arch.lifecycle.ViewModel

class TweetsViewModel : ViewModel() {

    private val TAG by lazy {
        TweetsViewModel::class.java.simpleName
    }

}