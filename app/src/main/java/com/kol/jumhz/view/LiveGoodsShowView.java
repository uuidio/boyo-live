package com.kol.jumhz.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kol.jumhz.R;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;
import com.kol.jumhz.common.utils.T;

import java.util.ArrayList;
import java.util.List;


/**
 * Module:   TCRelevanceLiveGoodsShowView
 **/
/**
 * @ClassName: LiveGoodsShowView
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:25
 * @Description:
 */
public class LiveGoodsShowView extends RecyclerView.Adapter<LiveGoodsShowView.ViewHolder> {
    public ArrayList<LiveGoodsBean> list;
    private int mposition = -1;
    private Context mContext;

    public LiveGoodsShowView(Context mContent, ArrayList<LiveGoodsBean> list){
        this.mContext = mContent;
        this.list= list;
        init();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_liveaddgoodsshow_list, parent, false);
        return new ViewHolder(view);
    }

    //初始化list内的数据状态，全部重置为false，即为选取状态
    private void init() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setChecked(false);
        }
    }

    //全选
    public List<LiveGoodsBean> all() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setChecked(true);
        }
        notifyDataSetChanged();
        return list;
    }

    //取消全选
    public List<LiveGoodsBean> cancelAll() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setChecked(false);

        }
        notifyDataSetChanged();
        return list;
    }

    //删除
    public List<LiveGoodsBean> delete() {
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).isChecked()) {
                list.remove(i);
                //notifyItemRemoved(i);
                i--;
            }
        }
        notifyDataSetChanged();
        return list;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Glide.with(mContext).load(list.get(position).getImage()).into(holder.item_iv_image);
        holder.item_tv_titile.setText(list.get(position).getTitle());
        holder.item_tv_num.setText("" +list.get(position).getNum());
        holder.item_tv_price.setText(list.get(position).getPrice());
        holder.item_cb.setOnCheckedChangeListener(null);
        holder.item_cb.setChecked(list.get(position).isChecked());

        holder.item_cb.setTag(position);
        //设置checkBox改变监听
        holder.item_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                list.get(position).setChecked(isChecked);
                for (int i = 0; i <list.size(); i++) {
                    if (position != i) {
                        list.get(i).setChecked(false);
                    }
                }
                notifyDataSetChanged();
            }

        });
        if (list.get(position) == null) {
            list.get(position).setChecked(false);
        }
        holder.item_cb.setChecked(list.get(position).isChecked());
        //没有设置tag之前会有item重复选框出现，设置tag之后，此问题解决

        holder.item_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T.showShort(mContext,"点击"+position);
            }
        });
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
        private ImageView item_iv_image;
        private TextView item_tv_titile;
        private TextView item_tv_num;
        private TextView item_tv_price;

        private ViewHolder(View itemView) {
            super(itemView);
            item_cb=itemView.findViewById(R.id.cb);
            item_iv_image=itemView.findViewById(R.id.iv_image);
            item_tv_titile=itemView.findViewById(R.id.tv_titile);
            item_tv_num=itemView.findViewById(R.id.tv_num);
            item_tv_price=itemView.findViewById(R.id.tv_price);
        }
    }
}
