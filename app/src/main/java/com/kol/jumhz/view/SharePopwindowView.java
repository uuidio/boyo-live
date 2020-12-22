package com.kol.jumhz.view;

/*
 * @ProjectName: Jumhz
 * @Package: com.tencent.qcloud.jumhz.view
 * @ClassName: TCRewritePopwindowView
 * @Description: 自定义popupWindow
 * @Author: Lanlnk
 * @CreateDate: 2020/4/12 14:58
 * @Version:
 */

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kol.jumhz.R;

public class SharePopwindowView extends PopupWindow {

    public SharePopwindowView(AppCompatActivity context, View.OnClickListener itemsOnClick) {
        super(context);
        initView(context, itemsOnClick);
    }

    private void initView(final AppCompatActivity context, View.OnClickListener itemsOnClick) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert mInflater != null;
        View mView = mInflater.inflate(R.layout.popupwindow_share, null);
        RelativeLayout wechat = mView.findViewById(R.id.rl_wechat);
        RelativeLayout savePhoto = mView.findViewById(R.id.rl_save_photo);

        TextView canale = mView.findViewById(R.id.tv_cancle);
        RelativeLayout canaleTv = mView.findViewById(R.id.btn_cancel);
        canale.setOnClickListener(v -> {
            //销毁弹出框
            dismiss();
            backgroundAlpha(context, 1f);
        });
        canaleTv.setOnClickListener(v -> {
            //销毁弹出框
            dismiss();
            backgroundAlpha(context, 1f);
        });
        //设置按钮监听
        wechat.setOnClickListener(itemsOnClick);
        savePhoto.setOnClickListener(itemsOnClick);

        //设置SelectPicPopupWindow的View
        this.setContentView(mView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(WindowManager.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置PopupWindow可触摸
        this.setTouchable(true);
        //设置非PopupWindow区域是否可触摸
        this.setOutsideTouchable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.sharedialog);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        backgroundAlpha(context, 0.5f);//0.0-1.0
        this.setOnDismissListener(() -> backgroundAlpha(context, 1f));
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(AppCompatActivity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

}