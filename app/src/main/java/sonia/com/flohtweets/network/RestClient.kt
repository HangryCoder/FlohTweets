package sonia.com.flohtweets.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import sonia.com.flohtweets.BuildConfig
import sonia.com.flohtweets.utils.Constants
import java.util.concurrent.TimeUnit

class RestClient {

    companion object {

        private val httpLoggingInterceptor = HttpLoggingInterceptor().also {
            it.level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(Constants.RETROFIT_TIMEOUT, TimeUnit.MINUTES)
            .readTimeout(Constants.RETROFIT_TIMEOUT, TimeUnit.MINUTES)
            .addInterceptor(httpLoggingInterceptor)
            .build()

        private fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(Constants.MAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
        }

        fun getTweetAPI(): TweetAPI {
            return getRetrofit()
                .create(TweetAPI::class.java)
        }

    }
}