package com.kol.jumhz.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.TextView;

import com.kol.jumhz.R;

import java.lang.reflect.Field;

/**
 * @ClassName: FinishDetailDialogFragment
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:36
 * @Description: 统计了观看人数、本场热度、点赞数量、开播时间
 */
public class FinishDetailDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog mDetailDialog = new Dialog(getActivity(), R.style.dialog);
        mDetailDialog.setContentView(R.layout.dialog_publish_detail);
        mDetailDialog.setCancelable(false);

        TextView tvCancel = mDetailDialog.findViewById(R.id.anchor_btn_cancel);
        tvCancel.setOnClickListener(v -> {
            mDetailDialog.dismiss();
            getActivity().finish();
        });

        TextView tvDetailTime = mDetailDialog.findViewById(R.id.tv_time);
        TextView tvDetailWatchCount = mDetailDialog.findViewById(R.id.tv_members);
        TextView tvDetailHeatCount = mDetailDialog.findViewById(R.id.tv_heat);
        TextView tvDetailAdmires = mDetailDialog.findViewById(R.id.tv_admires);

        //确认则显示观看detail
        tvDetailTime.setText(getArguments().getString("time"));
        tvDetailWatchCount.setText(getArguments().getString("totalMemberCount"));
        tvDetailHeatCount.setText(getArguments().getString("heatCount"));
        tvDetailAdmires.setText(getArguments().getString("heartCount"));

        return mDetailDialog;
    }

    //@Override
    //    //public void show(FragmentManager manager, String tag) {
    //    //    FragmentTransaction fragmentTransaction = manager.beginTransaction();
    //    //    fragmentTransaction.add(this, tag);
    //    //    fragmentTransaction.commitAllowingStateLoss();
    //    //}

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            Field mDismissed = this.getClass().getSuperclass().getDeclaredField("mDismissed");
            Field mShownByMe = this.getClass().getSuperclass().getDeclaredField("mShownByMe");
            mDismissed.setAccessible(true);
            mShownByMe.setAccessible(true);
            mDismissed.setBoolean(this, false);
            mShownByMe.setBoolean(this, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void dismiss() {
//        super.dismiss();
        dismissAllowingStateLoss();
    }
}
