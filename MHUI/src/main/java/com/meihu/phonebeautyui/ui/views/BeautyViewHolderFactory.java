package com.meihu.phonebeautyui.ui.views;

import android.content.Context;
import android.view.ViewGroup;

import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.utils.ToastUtil;
import com.meihu.phonebeautyui.R;

public class BeautyViewHolderFactory {

    public static BaseBeautyViewHolder getBeautyViewHolder(Context context, ViewGroup viewGroup){
        BaseBeautyViewHolder beautyViewHolder;
        String ver = MHSDK.getInstance().getVer();
        System.out.println("MHSDK.getInstance().getVer()"+ver);
        if ("-1".equals(ver)) {
            ToastUtil.show(MHSDK.getInstance().getAppContext(), R.string.license_status_error);
        }
        if (ver == null) {
            ToastUtil.show(MHSDK.getInstance().getAppContext(), R.string.license_status_nose);
            ver = "-1";
        }
        switch (ver){
//                case "0":
//                beautyViewHolder = new DefaultBeautyViewHolder(context, viewGroup);
//                break;
            case "1":
                beautyViewHolder = new BeautyViewHolder(context, viewGroup);
                break;
            default:
                beautyViewHolder = new DefaultBeautyViewHolder(context, viewGroup);
                break;
        }
        return beautyViewHolder;
    }
}
