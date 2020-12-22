package com.kol.jumhz.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kol.jumhz.R;
import com.kol.jumhz.common.assistant.LotteryBean;

import java.util.ArrayList;

/**
 * @ProjectName: Jumhz
 * @Package: com.tencent.qcloud.jumhz.assistant
 * @ClassName: RvAdapter1
 * @Description:
 * @Author: Lanlnk
 * @CreateDate: 2020/5/7 11:30
 * @Version:
 */
public class LotteryListView extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private static final int ITEM_FOOTER = 0x1;
    private static final int ITEM_DATA = 0x2;
    private Context mContext;
    private RecyclerView recyclerView;
    private ArrayList<LotteryBean> mList;

    public LotteryListView(Context mContext, ArrayList<LotteryBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setmList(ArrayList<LotteryBean> mList) {
        this.mList = mList;
    }

    /**
     * 用于创建ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        RecyclerView.ViewHolder vh = null;
        view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_lottery_list,null);
        view.setOnClickListener(this);
        vh = new DataViewHolder(view);
        //使用代码设置宽高（xml布局设置无效时）
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        /*switch (viewType){
            case ITEM_DATA:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_lottery_list,null);
                view.setOnClickListener(this);
                vh = new DataViewHolder(view);
                //使用代码设置宽高（xml布局设置无效时）
                view.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            *//*case ITEM_FOOTER:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_footer,null);
                //使用代码设置宽高（xml布局设置无效时）
                vh = new FooterViewHolder(view);
                view.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                break;*//*
            default:
        }*/
        assert vh != null;
        return vh;
    }

    /**
     * 获取Item的View类型
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        //根据 Item 的 position 返回不同的 Viewtype
        if (position == (getItemCount())-1){
            return ITEM_FOOTER;
        }else{
            return ITEM_DATA;
        }
    }

    /**
     * 绑定数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DataViewHolder){
            DataViewHolder dataViewHolder = (DataViewHolder) holder;
            dataViewHolder.tvName.setText(mList.get(position).getName());
            dataViewHolder.tvId.setText(String.valueOf(mList.get(position).getId()));
            dataViewHolder.tvInfo.setText(mList.get(position).getInfo());
            String data = "null".equals(mList.get(position).getTime())? "0000-00-00" : mList.get(position).getTime().substring(0,10);
            dataViewHolder.tvTime.setText(data);
        }else if (holder instanceof FooterViewHolder){

            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            //footerViewHolder.notify();

        }
    }

    /**
     * 选项总数
     * @return
     */
    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View view) {
        //根据RecyclerView获得当前View的位置
        int position = recyclerView.getChildAdapterPosition(view);
        //程序执行到此，会去执行具体实现的onItemClick()方法
        //if (onItemClickListener!=null){
        //    onItemClickListener.onItemClick(recyclerView,view,position,mList.get(position));
        //}
    }

    /**
     * 创建ViewHolder
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvId;
        TextView tvTime;
        TextView tvInfo;
        DataViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvId = itemView.findViewById(R.id.tv_id);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvInfo = itemView.findViewById(R.id.tv_info);
        }
    }

    /**
     * 创建footer的ViewHolder
     */
    public static class FooterViewHolder extends RecyclerView.ViewHolder{
        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private OnItemClickListener onItemClickListener;
    void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 定义RecyclerView选项单击事件的回调接口
     */
    public interface OnItemClickListener{
        //参数（父组件，当前单击的View,单击的View的位置，数据）
        void onItemClick(RecyclerView parent, View view, int position, String data);
    }
    /**
     * 将RecycleView附加到Adapter上
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView= recyclerView;
    }
    /**
     * 将RecycleView从Adapter解除
     */
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }
}

