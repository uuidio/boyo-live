package com.kol.jumhz.common.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kol.jumhz.R;

import java.util.List;

/**
 * @ClassName: AddLivePhotoAdapter
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:13
 * @Description: 图片选择器适配器
 */
public class AddLivePhotoAdapter extends RecyclerView.Adapter<AddLivePhotoAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater mLayoutInflater;
    private List<String> result;

    public AddLivePhotoAdapter(Context context, List<String> result) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.result = result;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_add_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        holder.ivPhoto.measure(
                View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.AT_MOST));

        Glide.with(context)
                .load(result.get(position))
                .centerCrop()
                .into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }

    }


}
