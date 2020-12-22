package com.kol.jumhz.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kol.jumhz.R;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;

import java.util.ArrayList;

/**
 * @ClassName: AddLiveForeGoodsshowView
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:06
 * @Description: 添加直播商品适配器
 */
public class AddLiveForeGoodsshowView extends RecyclerView.Adapter<AddLiveForeGoodsshowView.ViewHolder> {
    public ArrayList<LiveGoodsBean> list;
    private Context mContext;

    public AddLiveForeGoodsshowView(Context mContent, ArrayList<LiveGoodsBean> list){
        this.mContext = mContent;
        this.list= list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_addliveforegoodsshow_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(mContext).load(list.get(position).getImage()).into(holder.itemIvImage);
        holder.itemTvTitile.setText(list.get(position).getTitle());
        holder.itemTvPrice.setText(list.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        if (list==null){
            return 0;
        }
        return list.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView itemIvImage;
        TextView itemTvTitile;
        TextView itemTvPrice;


        public ViewHolder(View itemView) {
            super(itemView);
            itemIvImage =itemView.findViewById(R.id.iv_image);
            itemTvTitile =itemView.findViewById(R.id.tv_titile);
            itemTvPrice =itemView.findViewById(R.id.tv_price);
        }
    }
}
