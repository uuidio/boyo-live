package com.meihu.phonebeautyui.ui.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.filter.glfilter.resource.ResourceHelper;
import com.meihu.beautylibrary.filter.glfilter.resource.bean.ResourceData;
import com.meihu.beautylibrary.filter.glfilter.resource.bean.ResourceType;
import com.meihu.glide.Glide;
import com.meihu.phonebeautyui.R;
import com.meihu.phonebeautyui.ui.bean.FilterBean;
import com.meihu.phonebeautyui.ui.constant.Constants;
import com.meihu.phonebeautyui.ui.enums.FilterEnum;
import com.meihu.phonebeautyui.ui.views.BeautyDataModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kongxr on 2019/8/23.
 * filterAdapter
 */

public class FilterAdapter extends  BaseBeautyAdapter<FilterAdapter.ViewHolder, FilterBean> {

    private AssetManager assets;

    FilterAdapter(Context context) {
        super(context,1);
        assets = context.getAssets();
        mList = new ArrayList<>();
        List<ResourceData> resourceList = ResourceHelper.getResourceList();
        mList.add(new FilterBean(R.mipmap.icon_filter_orginal_new, 0, FilterEnum.NO_FILTER, 0, true));
        mList.add(new FilterBean(R.mipmap.icon_filter_langman_new, R.drawable.filter_langman, FilterEnum.ROMANTIC_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_qingxin_new, R.drawable.filter_qingxin, FilterEnum.FRESH_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_weimei_new, R.drawable.filter_weimei, FilterEnum.BEAUTIFUL_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_fennen_new, R.drawable.filter_fennen, FilterEnum.PINK_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_huaijiu_new, R.drawable.filter_huaijiu, FilterEnum.NOSTALGIC_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_qingliang_new, R.drawable.filter_qingliang, FilterEnum.COOL_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_landiao_new, R.drawable.filter_landiao, FilterEnum.BLUES_FILTER));
        mList.add(new FilterBean(R.mipmap.icon_filter_rixi_new, R.drawable.filter_rixi, FilterEnum.JAPANESE_FILTER));
        if (resourceList != null && resourceList.size() > 0) {
            for (ResourceData resourceData : resourceList) {
                if (resourceData.type == ResourceType.FILTER) {
                    mList.add(new FilterBean(true, resourceData.name, resourceData.thumbPath, FilterEnum.PRO_FILTER));
                }
            }
        }
        mOnClickListener = v -> {
            Object tag = v.getTag();
            if (tag != null) {
                int position = (int) tag;
                if (mCheckedPosition == position) {
                    return;
                }
                if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
                    mList.get(mCheckedPosition).setChecked(false);
                    notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
                }
                mList.get(position).setChecked(true);
                FilterBean filterBean = mList.get(position);
                saveFilterData(filterBean);
                notifyItemChanged(position, Constants.PAYLOAD);
                mCheckedPosition = position;
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mList.get(position), position);
                }
            }
        };
    }

    private void saveFilterData(FilterBean filterBean) {
        BeautyDataModel.getInstance().setFilterChanged(filterBean.getFilterEnum());
        BeautyDataModel.getInstance().setFilterBean(filterBean);
        FilterEnum filterEnum = filterBean.getFilterEnum();
        if (filterEnum == FilterEnum.PRO_FILTER) {
            BeautyDataModel.getInstance().setFilterName(filterBean.getmFilterName());
        }else {
            BeautyDataModel.getInstance().setFilterName(String.valueOf(filterBean.getFilterEnum().getValue()));
        }
    }

    public void setFilterChanged(FilterEnum filterEnum) {
        int value = filterEnum.getValue();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_list_filter_new, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {}

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position, List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        viewHolder.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class ViewHolder extends BaseBeautyAdapter.Vh{

//        ImageView mImg;
        ImageView mCheckImg;
//        TextView mFilterName;

        ViewHolder(View itemView) {
            super(itemView);
            mCheckImg = itemView.findViewById(R.id.check_img);
        }

        void setData(FilterBean bean, int position, Object payload) {
            itemView.setTag(position);
            if (payload == null) {
                if (bean.getFilterEnum() == FilterEnum.PRO_FILTER) {
                    String thumbPath = bean.getmImgSrcPath();
                    Glide.with(itemView.getContext()).asBitmap().load(thumbPath).into(mImg);
//                    InputStream inputStream=null;
//                    try {
//                        if (assets != null) {
//                            inputStream =assets.open(thumbPath);
//                            mImg.setImageBitmap(BitmapFactory.decodeStream(inputStream));
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }finally {
//                        if (inputStream != null) {
//                            try {
//                                inputStream.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
                    mBeautyName.setText(bean.getmFilterName());
                }else {
                    mImg.setImageResource(bean.getImgSrc());
                    mBeautyName.setText(bean.getFilterEnum().getStringId());
                }
            }
            if (position == 0 ){
                if (BeautyDataModel.getInstance().getFilterEnum() == null || BeautyDataModel.getInstance().getFilterEnum()==FilterEnum.NO_FILTER){
                    bean.setChecked(true);
                    mCheckedPosition = 0;
                }else {
                    bean.setChecked(false);
                }
            }else {
                if (bean.getFilterEnum() == FilterEnum.PRO_FILTER) {
                    FilterBean filterBean = BeautyDataModel.getInstance().getFilterBean();
                    if (filterBean != null && !TextUtils.isEmpty(bean.getmFilterName()) && bean.getmFilterName().equals(filterBean.getmFilterName())) {
                        bean.setChecked(true);
                    }else {
                        bean.setChecked(false);
                    }
                }else if (BeautyDataModel.getInstance().getFilterEnum() != null && bean.getFilterEnum()==BeautyDataModel.getInstance().getFilterEnum()){
                    bean.setChecked(true);
                    mCheckedPosition = position;
                }else {
                    bean.setChecked(false);
                }
            }
            if (bean.isChecked()) {
                if (mCheckImg.getVisibility() != View.VISIBLE) {
                    mCheckImg.setVisibility(View.VISIBLE);
                }
                mBeautyName.setTextColor(MHSDK.getInstance().getAppContext().getResources().getColor(R.color.shape_icon_select_color));
            } else {
                if (mCheckImg.getVisibility() == View.VISIBLE) {
                    mCheckImg.setVisibility(View.INVISIBLE);
                }
                mBeautyName.setTextColor(MHSDK.getInstance().getAppContext().getResources().getColor(R.color.bg_black));
            }
        }
    }
}
