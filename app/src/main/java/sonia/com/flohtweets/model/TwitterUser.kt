package sonia.com.flohtweets.model

import android.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.gson.annotations.SerializedName
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("android:profileImage")
fun loadImage(imageView: CircleImageView, profileUrl: String) {
    Glide.with(imageView.context)
        .load(profileUrl)
        .into(imageView)
}

data class TwitterUser(
    val id: String,
    val name: String,
    @SerializedName("screen_name")
    val userName: String,
    @SerializedName("profile_image_url")
    val userProfileURL: String
)