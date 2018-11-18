package sonia.com.flohtweets.model

import com.google.gson.annotations.SerializedName

data class SearchMetadata(
    @SerializedName("max_id")
    val maxId: String,
    @SerializedName("next_results")
    val nextResultUrl: String,
    @SerializedName("refresh_url")
    val refreshUrl: String
)