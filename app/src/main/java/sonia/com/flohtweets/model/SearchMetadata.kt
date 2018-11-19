package sonia.com.flohtweets.model

import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.Nullable

data class SearchMetadata(
    @SerializedName("max_id")
    val maxId: String,
    @SerializedName("next_results")
    @Nullable var nextResultUrl: String,
    @SerializedName("refresh_url")
    val refreshUrl: String
)