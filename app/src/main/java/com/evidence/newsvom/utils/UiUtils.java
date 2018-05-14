package com.evidence.newsvom.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.evidence.newsvom.R;
import com.evidence.newsvom.components.ApplicationLoader;
import com.evidence.newsvom.models.Headline;
import com.evidence.newsvom.ui.widgets.LoadingImageView;

import net.danlew.android.joda.DateUtils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import java.util.Date;


public class UiUtils {
    //Allow Non-Instantiability
    private UiUtils() {

    }

    public static void blinkView(View mView) {
        try {
            Animation mFadeInFadeIn = getAnimation(ApplicationLoader.getInstance(), android.R.anim.fade_in);
            mFadeInFadeIn.setRepeatMode(Animation.REVERSE);
            animateView(mView, mFadeInFadeIn);
        } catch (IllegalStateException | NullPointerException ignored) {

        }
    }

    private static synchronized void animateView(View view, Animation animation) {
        if (view != null) {
            view.startAnimation(animation);
        }
    }

    private static Animation getAnimation(ApplicationLoader instance, int animationResId) {
        return AnimationUtils.loadAnimation(instance, animationResId);
    }

    public static void loadImage(final String imageUrl, final ImageView imageView) {
        if (imageView instanceof LoadingImageView) {
            LoadingImageView loadingImageView = (LoadingImageView) imageView;
            loadingImageView.startLoading();
        }
        RequestOptions requestOptions = new RequestOptions().error(R.drawable.boss_my_brain).placeholder(R.drawable.boss_my_brain);

        Glide.with(ApplicationLoader.getInstance()).setDefaultRequestOptions(requestOptions).load(imageUrl).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if (imageView instanceof LoadingImageView) {
                    LoadingImageView loadingImageView = (LoadingImageView) imageView;
                    loadingImageView.stopLoading();
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (imageView instanceof LoadingImageView) {
                    LoadingImageView loadingImageView = (LoadingImageView) imageView;
                    loadingImageView.stopLoading();
                }
                return false;
            }
        }).into(imageView);
    }

    public static String getDate(Headline headLine) {
        String date = headLine.getPublishedDate();
        if (StringUtils.isNotEmpty(date)) {
            Instant dateInstance = Instant.parse(date);
            if (dateInstance != null) {
                LocalDate localDate = LocalDate.fromDateFields(new Date(dateInstance.getMillis()));
                String dateString = NewsConstants.DATE_FORMATTER_IN_BIRTHDAY_FORMAT.format(localDate.toDate());
                if (DateUtils.isToday(localDate)) {
                    return "Today";
                } else if (DateUtils.isToday(localDate.plusDays(1))) {
                    return "Yesterday";
                } else {
                    return dateString;
                }
            } else {
                return "Today";
            }
        } else {
            return "Today";
        }
    }

}
