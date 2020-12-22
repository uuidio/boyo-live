package com.kol.jumhz.view;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.PrepareView;
import com.dueeeke.videoplayer.player.VideoView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.kol.jumhz.R;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.playback.PlayBackBean;
import com.kol.jumhz.common.utils.ButtonUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @Package: com.tencent.qcloud.jumhz.assistant
 * @ClassName: RvAdapter1
 * @Description:
 * @Author: Lanlnk
 * @CreateDate: 2020/5/7 11:30
 */
public class PlayBackListView extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private static final int ITEM_FOOTER = 0x1;
    private static final int ITEM_DATA = 0x2;
    private AppCompatActivity mContext;
    private RecyclerView recyclerView;
    private ArrayList<PlayBackBean> list;

    private VideoView mCurVideoView;

    public PlayBackListView(AppCompatActivity mContext, ArrayList<PlayBackBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void setList(ArrayList<PlayBackBean> list) {
        this.list = list;
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
        view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_playback_list,null);
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

            PrepareView prepareView = new PrepareView(mContext);//准备播放界面
            ImageView thumb = prepareView.findViewById(R.id.thumb); //封面图

            dataViewHolder.item_tv_titile.setText(list.get(position).getTitle());
            String data = list.get(position).getCreated_at().substring(5, list.get(position).getCreated_at().length()-3).replace("-","/");
            dataViewHolder.item_tv_time.setText(data);
            dataViewHolder.item_player.setUrl(list.get(position).getPlayback());
            Glide.with(mContext).load(list.get(position).getSurface_img()).into(thumb);

            StandardVideoController controller = new StandardVideoController(mContext);
            controller.addDefaultControlComponent(list.get(position).getTitle(), false);
            controller.setEnableOrientation(true);
            controller.addControlComponent(prepareView);

            dataViewHolder.item_rl_delete.setOnClickListener(v -> {
                //短时间多次点击
                if (ButtonUtils.isFastDoubleClick(R.id.rl_delete)) { return; }
                WaitDialog.show(mContext,"");
                UserMgr.getInstance().deletePlayBack(list.get(holder.getAdapterPosition()).getId(),new HTTPMgr.Callback() {
                    @Override
                    public void onSuccess(JSONObject data1) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            TipDialog.show(mContext,"", TipDialog.TYPE.SUCCESS);
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(0,list.size());
                            //notifyDataSetChanged();
                        });
                    }
                    @Override
                    public void onFailure(int code, final String msg) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> TipDialog.show(mContext, msg+":"+code, TipDialog.TYPE.WARNING));
                    }
                });
            });

            dataViewHolder.item_player.setOnStateChangeListener(new VideoView.SimpleOnStateChangeListener() {
                @Override
                public void onPlayStateChanged(int playState) {
                    //监听VideoViewManager释放，重置状态
                    if (playState == VideoView.STATE_PLAYING) {
                        mCurVideoView = dataViewHolder.item_player;
                    }
                }
            });

            dataViewHolder.start.setOnClickListener(v -> {
                if (onTcItemClickListener != null) {
                    onTcItemClickListener.onTcItemClickListener(holder, position);
                }
            });

            dataViewHolder.item_player.setVideoController(controller);

        } else if (holder instanceof FooterViewHolder){
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
        return list.size();
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
        RelativeLayout llContent;
        TextView item_tv_titile;
        TextView item_tv_time;
        RelativeLayout item_rl_delete;
        RelativeLayout item_createing;
        public VideoView item_player;
        public View start;
        DataViewHolder(View itemView) {
            super(itemView);
            llContent=itemView.findViewById(R.id.llContent);
            item_tv_titile=itemView.findViewById(R.id.tv_titile);
            item_tv_time=itemView.findViewById(R.id.tv_time);
            item_rl_delete=itemView.findViewById(R.id.rl_delete);
            item_player=itemView.findViewById(R.id.player);
            item_createing=itemView.findViewById(R.id.rl_createing);
            start=itemView.findViewById(R.id.start);
        }
    }

    public void release(){
        if (mCurVideoView != null) {
            mCurVideoView.release();
        }
    }

    private OnTcItemClickListener onTcItemClickListener;

    public void setOnTcItemClickListener(OnTcItemClickListener onTcItemClickListener) {
        this.onTcItemClickListener = onTcItemClickListener;
    }

    public interface OnTcItemClickListener{
        void onTcItemClickListener(RecyclerView.ViewHolder holder, final int position);
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

