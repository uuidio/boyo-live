package com.meihu.phonebeautyui.ui.interfaces;

import com.meihu.phonebeautyui.ui.bean.FilterBean;
import com.meihu.phonebeautyui.ui.enums.FilterEnum;

/**
 * Created by cxf on 2018/10/8.
 * 基础美颜回调
 */

public interface DefaultBeautyEffectListener extends BeautyEffectListener {

//    void onFilterChanged(FilterEnum filterEnumEnum);
    void onFilterChanged(FilterBean filterBean);

    void onMeiBaiChanged(int progress);

    void onMoPiChanged(int progress);

    void onFengNenChanged(int progress);

    void onBeautyOrigin();

}
