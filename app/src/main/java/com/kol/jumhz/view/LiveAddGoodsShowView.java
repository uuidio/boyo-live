package com.kol.jumhz.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kol.jumhz.R;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;
import com.kol.jumhz.livegoods.FilterListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: LiveAddGoodsShowView
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:16
 * @Description: 选择商品适配器
 */
public class LiveAddGoodsShowView extends RecyclerView.Adapter<LiveAddGoodsShowView.ViewHolder> implements Filterable {
    public ArrayList<LiveGoodsBean> list;
    private Context mContext;
    private MyFilter filter = null;// 创建MyFilter对象
    private FilterListener listener;// 接口对象

    public LiveAddGoodsShowView(Context mContent, ArrayList<LiveGoodsBean> list, FilterListener filterListener){
        this.mContext = mContent;
        this.list= list;
        this.listener = filterListener;
        init();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_liveaddgoodsshow_list, parent, false);
        return new ViewHolder(view);
    }

    /**
     * @ClassName: init
     * @Description: 初始化list内的数据状态，全部重置为false，即为选取状态
     */
    private void init() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setChecked(false);
        }
    }

    /**
     * @ClassName: all
     * @Description: 全选
     */
    public List<LiveGoodsBean> all() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setChecked(true);
        }
        notifyDataSetChanged();
        return list;
    }

    /**
     * @ClassName: cancelAll
     * @Description: 取消全选
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
        holder.item_tv_num.setText("" +list.get(position).getNum());
        holder.item_tv_price.setText(list.get(position).getPrice());
        holder.item_cb.setOnCheckedChangeListener(null);
        holder.item_cb.setChecked(list.get(position).isChecked());

        holder.item_cb.setTag(position);
        //设置checkBox改变监听
        holder.item_cb.setOnCheckedChangeListener((buttonView, isChecked) -> list.get(position).setChecked(isChecked));
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

    @Override
    public Filter getFilter() {
        // 如果MyFilter对象为空，那么重写创建一个
        if (filter == null) {
            filter = new MyFilter(list);
        }
        return filter;
    }
    /**
     * 创建内部类MyFilter继承Filter类，并重写相关方法，实现数据的过滤
     */
    class MyFilter extends Filter {

        // 创建集合保存原始数据
        private ArrayList<LiveGoodsBean> original;
        private MyFilter(ArrayList<LiveGoodsBean> list) {
            this.original = list;
        }

        /**
         * 该方法返回搜索过滤后的数据
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // 创建FilterResults对象
            FilterResults results = new FilterResults();

            //没有搜索内容的话就还是给results赋值原始数据的值和大小
            //执行了搜索的话，根据搜索的规则过滤即可，最后把过滤后的数据的值和大小赋值给results
            if(TextUtils.isEmpty(constraint)) {
                results.values = original;
                results.count = original.size();
            } else {
                // 创建集合保存过滤后的数据
                ArrayList<LiveGoodsBean> mList = new ArrayList<>();
                // 遍历原始数据集合，根据搜索的规则过滤数据
                for (int i = 0; i < original.size(); i++) {
                    // 这里就是过滤规则的具体实现【规则有很多，大家可以自己决定怎么实现】
                    if(original.get(i).getTitle().trim().toLowerCase().contains(constraint.toString().trim().toLowerCase())){
                        // 规则匹配的话就往集合中添加该数据
                        mList.add(original.get(i));
                    }
                }
                results.values = mList;
                results.count = mList.size();
            }

            // 返回FilterResults对象
            return results;
        }

        /**
         * 该方法用来刷新用户界面，根据过滤后的数据重新展示列表
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // 获取过滤后的数据
            list = (ArrayList<LiveGoodsBean>) results.values;
            // 如果接口对象不为空，那么调用接口中的方法获取过滤后的数据，具体的实现在new这个接口的时候重写的方法里执行
            if(listener != null){
                listener.getFilterData(list);
            }
            // 刷新数据源显示
            notifyDataSetChanged();
        }
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
