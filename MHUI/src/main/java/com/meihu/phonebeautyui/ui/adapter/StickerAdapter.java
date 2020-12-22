package com.meihu.phonebeautyui.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meihu.beautylibrary.constant.Constants;
import com.meihu.beautylibrary.manager.LogManager;
import com.meihu.beautylibrary.utils.ToastUtil;
import com.meihu.glide.Glide;
import com.meihu.phonebeautyui.R;
import com.meihu.phonebeautyui.ui.bean.StickerServiceBean;
import com.meihu.phonebeautyui.ui.interfaces.OnItemClickListener;
import com.meihu.phonebeautyui.ui.manager.StickerManager;
import com.meihu.phonebeautyui.ui.views.BeautyDataModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kxr on 2019/8/22.
 * 萌颜 贴纸
 */

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.Vh> {

    private static final String TAG = StickerAdapter.class.getSimpleName();
    private Context mContext;
    public List<StickerServiceBean> mList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Drawable mCheckDrawable;
    public int mCheckedPosition;
    public OnItemClickListener<StickerServiceBean> mOnItemClickListener;
    private static final int MAX_DOWNLOAD_TASK = 3;
    public SparseArray<String> mLoadingTaskMap;
    //    private DownloadUtil mDownloadUtil;
    public int touchPos;
    private boolean isInit;
    private String id;

    public StickerAdapter(Context context, String id) {
        this.id = id;
        mContext = context;
        requestStickerListData(id);
        mInflater = LayoutInflater.from(context.getApplicationContext());
        mCheckDrawable = ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.bg_item_tiezhi);
        mLoadingTaskMap = new SparseArray<>();
    }


    public void setOnItemClickListener(OnItemClickListener<StickerServiceBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_tiezhi_new, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setHolderData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        View mLoading;
        View mDownLoad;
        StickerServiceBean mBean;
        int mPosition;

        Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mLoading = itemView.findViewById(R.id.loading);
            mDownLoad = itemView.findViewById(R.id.download);
            itemView.setOnClickListener(v -> {
//                if (!VerifyManager.isAccess()) {
//                    Log.v("meihu_beauty", "尚未获取美颜sdk的有效授权");
//                    return;
//                }
                touchPos = mPosition;
                if (!mBean.isIs_downloaded()) {
                    if (mLoadingTaskMap.size() >= MAX_DOWNLOAD_TASK || mLoadingTaskMap.indexOfKey(mPosition) >= 0)
                        return;
                    if (TextUtils.isEmpty(mBean.getResource())) {
                        ToastUtil.show(mContext,"请检查鉴权码是否正确");
                        LogManager.getInstance().writeData("StickerServiceBean.getResource()=empty");
                    }
                    String name = mBean.getName();
                    mLoadingTaskMap.put(mPosition, name);
                    mBean.setDownLoading(true);
                    notifyItemChanged(mPosition, Constants.PAYLOAD);
                    StickerManager.getInstance().downloadSticker(StickerAdapter.this, mContext, mBean, mPosition, v, name);
                } else {
                    mCheckedPosition = mPosition;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mBean, mPosition);
                    }
                }
            });
        }

        void setHolderData(StickerServiceBean bean, int position, Object payload) {
            mBean = bean;
            mPosition = position;
            if (payload == null) {
                Glide.with(mContext).asBitmap().load(bean.getThumb()).into(mImg);
            }
            if (bean.isDownLoading()) {
                if (mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
            } else {
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.isIs_downloaded()) {
                if (mDownLoad.getVisibility() == View.VISIBLE) {
                    mDownLoad.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mDownLoad.getVisibility() != View.VISIBLE) {
                    mDownLoad.setVisibility(View.VISIBLE);
                }
            }
//            String curStickerName = StickerManager.getInstance().getmCurStickerName();
            String curStickerName = BeautyDataModel.getInstance().getStickerName();
            Log.d(TAG, "setHolderData: curStickerName=" + curStickerName);
            if (bean.getName().equals(curStickerName)) itemView.setBackground(mCheckDrawable);
            else itemView.setBackground(null);

//            if (bean.isChecked()) {
//                itemView.setBackground(mCheckDrawable);
//            } else {
//                itemView.setBackground(null);
//            }
        }

    }

    public void setData(List<StickerServiceBean> data) {
        mList = data;
        notifyDataSetChanged();
        if (mList != null && mList.size() > 0) {
            isInit = true;
        }
    }

    void clear() {
        if (mLoadingTaskMap != null) {
            for (int i = 0, size = mLoadingTaskMap.size(); i < size; i++) {
                String tag = mLoadingTaskMap.valueAt(i);
//                HttpClient.getInstance().cancel(tag);
            }
        }
        mOnItemClickListener = null;
        StickerManager.getInstance().release();
        mInflater = null;
        mCheckDrawable = null;
        mContext = null;
        mOnItemClickListener = null;
    }

    private void requestStickerListData(String id) {
        StickerManager.getInstance().getStickerThumbList(this, id);
    }

}
