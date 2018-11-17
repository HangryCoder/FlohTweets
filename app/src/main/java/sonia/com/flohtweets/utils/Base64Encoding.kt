package sonia.com.flohtweets.utils

import android.util.Base64
import java.nio.charset.StandardCharsets

class Base64Encoding {

    companion object {

        fun encodeStringToBase64(key: String): String {
            val data = key.toByteArray(StandardCharsets.UTF_8)
            return Base64.encodeToString(data, Base64.NO_WRAP)
        }
    }
}