package sonia.com.flohtweets.network

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*
import sonia.com.flohtweets.model.TwitterToken
import sonia.com.flohtweets.utils.Constants

interface TweetAPI {

    @POST(Constants.MAIN_URL + Constants.TWITTER_TOKEN)
    @FormUrlEncoded
    fun getAuthToken(
        @Header("Authorization") header: String,
        @Field("grant_type") grantType: String
    ): Single<TwitterToken>

    @GET(Constants.TWEETS_API)
    fun getFlohTweets(
        @Query("q") tweetName: String,
        @Query("count") count: Int,
        @Header("Authorization") header: String,
        @Header("Content-Type") contentType: String
    ): Single<ResponseBody>

}