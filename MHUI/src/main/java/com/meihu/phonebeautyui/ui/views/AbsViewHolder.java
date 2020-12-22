package com.meihu.phonebeautyui.ui.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.meihu.phonebeautyui.R;
import com.meihu.phonebeautyui.ui.util.ClickUtil;

public abstract class AbsViewHolder      {

    private String mTag;
    protected Context mContext;
    protected ViewGroup mParentView;
    protected View mContentView;
    protected View mBeautyEntryContainer;
    protected ImageView mBeautyEntryBtn;

    public AbsViewHolder(Context context, ViewGroup parentView) {
        mTag = getClass().getSimpleName();
        mContext = context.getApplicationContext();
        mParentView = parentView;
        mContentView = LayoutInflater.from(mContext).inflate(getLayoutId(), mParentView, false);
        mBeautyEntryContainer = LayoutInflater.from(mContext).inflate(R.layout.layout_beauty_entry, mParentView, false);
        init();
    }

    public AbsViewHolder(Context context, ViewGroup parentView, Object... args) {
        mTag = getClass().getSimpleName();
        processArguments(args);
        mContext = context.getApplicationContext();
        mParentView = parentView;
        mContentView = LayoutInflater.from(mContext).inflate(getLayoutId(), mParentView, false);
        mBeautyEntryContainer = LayoutInflater.from(mContext).inflate(R.layout.layout_beauty_entry, mParentView, false);
        init();
    }

    protected void processArguments(Object... args) {

    }

    protected abstract int getLayoutId();

    public abstract void init();

    protected View findViewById(int res) {
        return mContentView.findViewById(res);
    }

    public View getContentView() {
        return mContentView;
    }

    protected boolean canClick() {
        return ClickUtil.canClick();
    }

    public void addToParent() {
        if (mParentView != null && mContentView != null) {
            mParentView.addView(mContentView);
        }
    }

    public void removeFromParent() {
        if (mContentView == null) return;
        ViewParent parent = mContentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mContentView);
        }
    }

    protected void release(){
        removeFromParent();
        mContext = null;
        mContentView = null;
        mParentView = null;
        mBeautyEntryContainer = null;
    }

}
