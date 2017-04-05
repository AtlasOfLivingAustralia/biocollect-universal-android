package au.org.ala.mobile.ozatlas.ui

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.ImageView
import au.org.ala.mobile.ozatlas.OzAtlasApp
import com.squareup.picasso.Picasso

class BindingAdapters {
    companion object {
        @JvmStatic
        @BindingAdapter("app:error")
        fun setError(editText: EditText, value: CharSequence?) {
            editText.error = value
        }

        @JvmStatic
        @BindingAdapter("bind:imageUrl", "bind:error")
        fun loadImage(view: ImageView, url: String, error: Drawable) = OzAtlasApp[view.context].component.picasso().load(url).error(error).into(view)
    }

}