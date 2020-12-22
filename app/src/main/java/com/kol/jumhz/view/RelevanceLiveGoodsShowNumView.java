package com.kol.jumhz.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kol.jumhz.R;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;

import java.util.ArrayList;

/**
 * @ClassName: RelevanceLiveGoodsShowView
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:32
 * @Description: 关联商品列表适配器
 */
public class RelevanceLiveGoodsShowNumView extends RecyclerView.Adapter<RelevanceLiveGoodsShowNumView.ViewHolder>{
    public ArrayList<LiveGoodsBean> list;
    private ArrayList num = new ArrayList();
    private Context mContext;

    public RelevanceLiveGoodsShowNumView(Context mContent, ArrayList<LiveGoodsBean> list){
        this.mContext = mContent;
        this.list= list;
        init();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_liveaddgoodsshow_list_num, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 初始化list内的数据状态，全部重置为false，即为选取状态
     */
    public void init() {
        if (list==null){
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setChecked(false);
            list.get(i).setNum(0);
        }
        num.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Glide.with(mContext).load(list.get(position).getImage()).into(holder.itemIvImage);
        holder.itemTvTitile.setText(list.get(position).getTitle());
        holder.itemTvNum.setText("" +list.get(position).getNum());
        holder.itemTvPrice.setText(list.get(position).getPrice());
        holder.itemCb.setOnCheckedChangeListener(null);
        holder.itemCb.setChecked(list.get(position).isChecked());

        holder.itemRlCb.setOnClickListener(v -> {
            if (holder.itemCb.isChecked()) {
                holder.itemCb.setChecked(false);
            } else {
                holder.itemCb.setChecked(true);
            }
        });

        holder.itemCb.setTag(position);
        //设置checkBox改变监听
        holder.itemCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            list.get(position).setChecked(isChecked);
            if (isChecked) {
                //添加编号
                num.add(list.get(position).getId());
                list.get(position).setNum(num.size());
                holder.itemCbNum.setText(String.valueOf(num.size()));
            } else {
                //去除编号,刷新数据
                num.remove(Integer.parseInt(holder.itemCbNum.getText().toString()) - 1);
                list.get(position).setNum(0);
                holder.itemCbNum.setText("");
                notifyDataSetChanged();
            }
        });

        if (list.get(position) == null) {
            list.get(position).setChecked(false);
        }

        if (list.get(position).getNum() != 0) {
            //根据list下标显示编号
            for (int i = 0; i < num.size(); i++) {
                if (list.get(position).getId() == Integer.parseInt(num.get(i).toString())) {
                    holder.itemCbNum.setText(String.valueOf(i + 1));
                }
            }
        } else {
            holder.itemCbNum.setText("");
        }
        holder.itemCb.setChecked(list.get(position).isChecked());
        //没有设置tag之前会有item重复选框出现，设置tag之后，此问题解决

        holder.itemCb.setOnClickListener(v -> { });
    }

    @Override
    public int getItemCount() {
        if (list==null){
            return 0;
        }
        return list.size();
    }

    public ArrayList getNum() {
        return num;
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{
        private ImageView itemIvImage;
        private TextView itemTvTitile;
        private TextView itemTvNum;
        private TextView itemTvPrice;
        private RelativeLayout itemRlCb;
        private CheckBox itemCb;
        private TextView itemCbNum;

        private ViewHolder(View itemView) {
            super(itemView);
            itemIvImage =itemView.findViewById(R.id.iv_image);
            itemTvTitile =itemView.findViewById(R.id.tv_titile);
            itemTvNum=itemView.findViewById(R.id.tv_num);
            itemTvPrice =itemView.findViewById(R.id.tv_price);
            itemRlCb =itemView.findViewById(R.id.rl_cb);
            itemCb =itemView.findViewById(R.id.cb);
            itemCbNum =itemView.findViewById(R.id.cb_num);
        }
    }
}
