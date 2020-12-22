package com.kol.jumhz.common.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * @ClassName: GlideCircleTransform
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:07
 * @Description: Glide图像裁剪
 */
public class GlideCircleTransform extends BitmapTransformation {
    public GlideCircleTransform(Context context){
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return Utils.createCircleImage(toTransform, 0);
    }

    @Override
    public String getId() {
        return getClass().getName();
    }

}
