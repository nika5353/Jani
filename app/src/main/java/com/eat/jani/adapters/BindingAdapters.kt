package com.eat.jani.adapters

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.eat.jani.R
import java.net.URLEncoder

object BindingAdapters {
    private const val baseUrl = "https://84e6-95-104-0-109.ngrok-free.app"

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            val fullUrl = baseUrl + url
//            if(fullUrl.contains("//"))
            Glide.with(view.context)
                .load(fullUrl)
                .error(R.drawable.no_pictures)
                .into(view)
        }/* else {
            Glide.with(view.context)
                .load(R.drawable.placeholder)
                .into(view)
        }*/
    }

    /*private fun encodeUrl(url: String): String {
        return url.split("/").joinToString("/") {
            URLEncoder.encode(it, "UTF-8")
        }
    }*/

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("formatedText")
    fun TextView.setFormattedDate(updatedAt: String?) {
        if(updatedAt == null) {
            text = "განახლების თარიღი: უცნობია"
            return
        }

        val parts = updatedAt.split("T")
        if (parts.size < 2) {
            text = "განახლების თარიღი: უცნობია"
            return
        }

        val date = parts[0]
        val time = parts[1].substring(0, 5)
        text = "განახლების თარიღი: $date $time"
    }
}