package com.kol.jumhz.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kol.jumhz.Application;
import com.kol.jumhz.GlobalConfig;
import com.kol.jumhz.R;
import com.kol.jumhz.common.liveforeshow.LiveForeshowBean;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.BitmapUtil;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.publish.AddLiveActivity;
import com.kongzue.dialog.v3.ShareDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: Jumhz
 * @Package: com.tencent.qcloud.jumhz.assistant
 * @ClassName: RvAdapter1
 * @Description:
 * @Author: Lanlnk
 * @CreateDate: 2020/5/7 11:30
 * @Version:
 */
public class LiveForeshowListView extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private static final int ITEM_FOOTER = 0x1;
    private static final int ITEM_DATA = 0x2;
    public ArrayList<LiveForeshowBean> list;
    private AppCompatActivity mContext;
    private RecyclerView recyclerView;
    private SharePopwindowView mPopwindow;
    private int liveId = 0;
    private String liveImgUrl = "";
    private String appImgUrl = "";
    private String liveTitle = "";
    private String liveTime = "";
    private String liveInfo = "";

    public LiveForeshowListView(AppCompatActivity mContext, ArrayList<LiveForeshowBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void setList(ArrayList<LiveForeshowBean> list) {
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
        view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_liveforeshow_list,null);
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

            List<ShareDialog.Item> itemList = new ArrayList<>();
            itemList.add(new ShareDialog.Item(mContext ,R.mipmap.ic_wechat,"微信"));
            itemList.add(new ShareDialog.Item(mContext ,R.mipmap.ic_save_photo,"生成海报"));
            Glide.with(mContext).load(list.get(position).getImage()).into(dataViewHolder.item_iv_image);
            dataViewHolder.item_tv_titile.setText(list.get(position).getTitle());
            dataViewHolder.item_tv_date.setText(list.get(position).getDate());

            dataViewHolder.item_iv_edit.setOnClickListener(v -> {
                //短时间多次点击
                if (ButtonUtils.isFastDoubleClick(R.id.layout_edit)) { return; }
                Intent intent=new Intent(mContext, AddLiveActivity.class);
                int liveShowId = list.get(position).getId();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("LiveForeshow", liveShowId);
                mContext.startActivity(intent);
            });

            dataViewHolder.item_iv_share.setOnClickListener(v -> {
                liveId = list.get(position).getLiveId();
                liveImgUrl = list.get(position).getImage();
                liveTime = list.get(position).getDate().substring(5, list.get(position).getDate().length()-3).replace("-","/");
                liveTitle = list.get(position).getTitle();
                liveInfo = list.get(position).getInfo();
                appImgUrl = list.get(position).getWechat_img();

                mPopwindow = new SharePopwindowView(mContext, itemsOnClick);
                mPopwindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            });

            dataViewHolder.item_iv_delete.setOnClickListener(v -> {
                //短时间多次点击
                if (ButtonUtils.isFastDoubleClick(R.id.layout_delete)) { return; }
                WaitDialog.show(mContext,"");
                UserMgr.getInstance().deleteLiveForeshow(list.get(holder.getAdapterPosition()).getId(),new HTTPMgr.Callback() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            TipDialog.show(mContext,"", TipDialog.TYPE.SUCCESS);
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(0,list.size());
                        });
                    }
                    @Override
                    public void onFailure(int code, final String msg) { }
                });
            });
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
     * 为弹出窗口实现监听类
     */
    private final View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPopwindow.dismiss();
            mPopwindow.backgroundAlpha(mContext, 1f);
            switch (v.getId()) {
                case R.id.rl_wechat:
                    WaitDialog.show(mContext,"");
                    new Thread(() -> {
                        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
                        miniProgramObj.webpageUrl = "http://www.qq.com"; // 兼容低版本的网页链接
                        miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE; // 正式版:0，测试版:1，体验版:2
                        miniProgramObj.userName = GlobalConfig.WX_INIT_ID; // 小程序原始id
                        miniProgramObj.path = "live/pages/lives/lives?liveid="+liveId; //小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
                        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
                        msg.title = liveTitle; // 小程序消息title
                        msg.description = ""; // 小程序消息desc
                        Bitmap bitmap = Utils.urlToBitmap(mContext,liveImgUrl);
                        Bitmap minBitmap = BitmapUtil.compressImageSize(bitmap);
                        msg.thumbData = Utils.bitmapToByte(minBitmap); // 小程序消息封面图片，小于128k
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "miniProgram";
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前只支持会话
                        Application.api.sendReq(req);
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            TipDialog.show(mContext, "", TipDialog.TYPE.SUCCESS);
                        });
                    }).start();
                    break;
                case R.id.rl_save_photo:
                    WaitDialog.show(mContext, "");
                    //显示分享界面
                    ShareDialogFragment shareDialogFragment = new ShareDialogFragment();
                    Bundle args = new Bundle();
                    args.putString("img1", liveImgUrl);
                    args.putString("img2", UserMgr.getInstance().getAvatar());
                    args.putString("img3", appImgUrl);
                    args.putString("time", liveTime);
                    args.putString("name", UserMgr.getInstance().getNickname());
                    args.putString("info", liveTitle);
                    shareDialogFragment.setArguments(args);
                    shareDialogFragment.setCancelable(false);
                    if (shareDialogFragment.isAdded()) { shareDialogFragment.dismiss(); }
                    else { shareDialogFragment.show(mContext.getFragmentManager(), ""); }
                    WaitDialog.dismiss();
                    break;
                default:
            }
        }

    };

    /**
     * 创建ViewHolder
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder{
        ImageView item_iv_image;
        TextView item_tv_titile;
        TextView item_tv_date;
        View item_iv_edit;
        View item_iv_share;
        View item_iv_delete;
        DataViewHolder(View itemView) {
            super(itemView);
            item_iv_image=itemView.findViewById(R.id.iv_image);
            item_tv_titile=itemView.findViewById(R.id.tv_titile);
            item_tv_date=itemView.findViewById(R.id.tv_date);
            item_iv_edit=itemView.findViewById(R.id.layout_edit);
            item_iv_share=itemView.findViewById(R.id.layout_share);
            item_iv_delete=itemView.findViewById(R.id.layout_delete);
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

