package com.kol.jumhz.common.utils;

import android.app.Activity;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.kol.jumhz.R;
import com.yancy.gallerypick.inter.ImageLoader;
import com.yancy.gallerypick.widget.GalleryImageView;

/**
 * @ClassName: GlideLoader
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:08
 * @Description: ImageLoader 加载框架
 */
public class GlideLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, Context context, String path, GalleryImageView galleryImageView, int width, int height) {
        Glide.with(context)
                .load(path)
                .placeholder(R.mipmap.gallery_pick_photo)
                .centerCrop()
                .into(galleryImageView);
    }

    @Override
    public void clearMemoryCache() {

    }
}