package sonia.com.flohtweets.network

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*
import sonia.com.flohtweets.utils.Constants

interface TweetAPI {

    @GET(Constants.TWEETS_API)
    fun getFlohTweets(
        @Query("q") tweetName: String,
        @Query("result_type") resultType: String,
        @Query("count") count: Int,
        @Header("Authorization") header: String,
        @Header("Content-Type") contentType: String
    ): Observable<ResponseBody>

    @POST(Constants.MAIN_URL + Constants.TWITTER_TOKEN)
    @FormUrlEncoded
    fun getAuthToken(
        @Header("Authorization") header: String,
        @Field("grant_type") grantType: String
    ): Observable<ResponseBody>

}