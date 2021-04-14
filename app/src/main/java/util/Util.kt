package util

import android.content.Context
import android.graphics.drawable.AdaptiveIconDrawable
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dany.chatapp.R
import java.text.DateFormat
import java.util.*

fun populateImage(
    context: Context, uri: String?, imageView: ImageView,
    errorDrawable: Int = R.drawable.empty
) {

    if (context != null) {
        val options = RequestOptions()

             .placeholder(progressDrawable(context)) //Add a circular progress Drawable in the placeholder
            .error(errorDrawable)
        Glide.with(context)
            .load(uri)
            .apply(options)
            .into(imageView)
    }
}

// This function is a spinner for the image when is loading:
fun progressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()

    }
}

fun getTime(): String{
    val df: DateFormat = DateFormat.getDateInstance()
    return df.format(Date())
}