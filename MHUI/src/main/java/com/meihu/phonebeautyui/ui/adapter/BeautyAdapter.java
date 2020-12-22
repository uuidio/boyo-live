package com.meihu.phonebeautyui.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.meihu.phonebeautyui.R;
import com.meihu.phonebeautyui.ui.bean.BeautyBean;
import com.meihu.phonebeautyui.ui.enums.BeautyTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kongxr on 2019/8/23.
 * shapeAdapter
 */


public class BeautyAdapter extends BaseBeautyAdapter<BaseBeautyAdapter.Vh, BeautyBean> {

    public BeautyAdapter(Context context) {
        super(context,1);
        mList = new ArrayList<>();
        String[] beautyNames = context.getResources().getStringArray(R.array.beauty_name_array);
        TypedArray beautyArray = context.getResources().obtainTypedArray(R.array.beauty_icon_default);
        TypedArray beautyArraySel = context.getResources().obtainTypedArray(R.array.beauty_icon_selected);
        for (int i = 0; i < beautyNames.length; i++) {
            String beautyName = beautyNames[i];
            int imgSrc = beautyArray.getResourceId(i, R.mipmap.beauty_origin);
            int ImgSrcSel = beautyArraySel.getResourceId(i, R.mipmap.beauty_origin);
            BeautyBean beautyBean = new BeautyBean(imgSrc, ImgSrcSel, beautyName, BeautyTypeEnum.BEAUTY_TYPE_ENUM,false);
            mList.add(beautyBean);
        }
        beautyArray.recycle();
        beautyArraySel.recycle();

    }

    @NonNull
    @Override
    public BaseBeautyAdapter.Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_shape_new, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseBeautyAdapter.Vh vh, int position, List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
