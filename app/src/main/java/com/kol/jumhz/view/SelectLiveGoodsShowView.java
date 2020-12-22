package com.kol.jumhz.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kol.jumhz.R;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: SelectLiveGoodsShowView
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:36
 * @Description: 直播间选择悬浮商品适配器
 */
public class SelectLiveGoodsShowView extends RecyclerView.Adapter<SelectLiveGoodsShowView.ViewHolder> {
    public ArrayList<LiveGoodsBean> list;
    private int mposition = -1;
    private Context mContext;
    private String num;

    public SelectLiveGoodsShowView(Context mContent, ArrayList<LiveGoodsBean> list, String num){
        this.mContext = mContent;
        this.list= list;
        this.num= num;
        init();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_select_liveaddgoodsshow_list, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 初始化list内的数据状态，全部重置为false，即为选取状态
     */
    private void init() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setNum(i+1);
            if(!"".equals(num) && i+1 == Integer.parseInt(num)) {
                list.get(i).setChecked(true);
                continue;
            }
            list.get(i).setChecked(false);
        }
    }

    /**
     * 取消全选
     */
    public List<LiveGoodsBean> cancelAll() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setChecked(false);

        }
        notifyDataSetChanged();
        return list;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Glide.with(mContext).load(list.get(position).getImage()).into(holder.item_iv_image);
        holder.item_tv_titile.setText(list.get(position).getTitle());
        //holder.item_tv_num.setText("" +list.get(position).getNum());
        holder.item_tv_price.setText(list.get(position).getPrice());
        holder.item_tv_goods_num.setText(String.valueOf(list.get(position).getNum()));
        holder.item_cb.setOnCheckedChangeListener(null);
        holder.item_cb.setChecked(list.get(position).isChecked());

        holder.item_cb.setTag(position);
        //设置checkBox改变监听
        holder.item_cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            list.get(position).setChecked(isChecked);
            for (int i = 0; i <list.size(); i++) {
                if (position != i) {
                    list.get(i).setChecked(false);
                }
            }
            notifyDataSetChanged();
        });
        if (list.get(position) == null) {
            list.get(position).setChecked(false);
        }
        holder.item_cb.setChecked(list.get(position).isChecked());
        //没有设置tag之前会有item重复选框出现，设置tag之后，此问题解决

        holder.item_cb.setOnClickListener(v -> { });
    }

    @Override
    public int getItemCount() {
        if (list==null){
            return 0;
        }
        return list.size();
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{
        private CheckBox item_cb;
        private TextView item_tv_goods_num;
        private ImageView item_iv_image;
        private TextView item_tv_titile;
        //private TextView item_tv_num;
        private TextView item_tv_price;

        private ViewHolder(View itemView) {
            super(itemView);

            item_cb=itemView.findViewById(R.id.cb);
            item_tv_goods_num=itemView.findViewById(R.id.tv_goods_num);
            item_iv_image=itemView.findViewById(R.id.iv_image);
            item_tv_titile=itemView.findViewById(R.id.tv_titile);
            //item_tv_num=itemView.findViewById(R.id.tv_num);
            item_tv_price=itemView.findViewById(R.id.tv_price);
        }
    }
}
