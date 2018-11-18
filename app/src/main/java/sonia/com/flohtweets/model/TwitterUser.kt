package sonia.com.flohtweets.model

import com.google.gson.annotations.SerializedName

class TwitterUser(
    val id: String,
    val name: String,
    @SerializedName("screen_name")
    val userName: String,
    @SerializedName("profile_image_url")
    val userProfileURL: String
)