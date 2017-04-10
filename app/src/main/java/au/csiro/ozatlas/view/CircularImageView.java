package au.csiro.ozatlas.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import au.csiro.ozatlas.R;

/**
 * Created by sadat on 29/04/2016.
 */

public class CircularImageView extends android.support.v7.widget.AppCompatImageView {

    private SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
            RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            dr.setCircular(true);
            CircularImageView.this.setImageDrawable(dr);
        }
    };

    public CircularImageView(Context context) {
        super(context);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageURL(String url) {
        if (url != null && url.length() != 0) {
            Glide.with(getContext()).load(url.replace(" ", "%20")).asBitmap().placeholder(R.mipmap.ic_launcher).into(target);
        }
    }

    public void setImage(int res) {
        Glide.with(getContext()).load(res).asBitmap().into(target);
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    public void setImageViewBackground(int color) {
        Drawable background = this.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(color);
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(color);
        }
    }
}
