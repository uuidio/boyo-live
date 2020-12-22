package com.meihu.phonebeautyui.ui.interfaces;

import com.meihu.phonebeautyui.ui.bean.StickerCategaryBean;

import java.util.List;

/**
 * Created by cxf on 2018/12/13.
 */

public interface IBeautyViewHolder {

    void setEffectListener(DefaultBeautyEffectListener effectListener);

    void show();

    void hide();

    boolean isShowed();

    void release();

    void setVisibleListener(VisibleListener visibleListener);

    void setStickerCategaryData(List<StickerCategaryBean> titles);

    interface VisibleListener {
        void onVisibleChanged(boolean visible);
    }

    void setCameraClickListener(MHCameraClickListener cameraClickListener);
}
