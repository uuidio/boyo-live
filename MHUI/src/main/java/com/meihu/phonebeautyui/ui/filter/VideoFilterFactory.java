package com.meihu.phonebeautyui.ui.filter;

import android.util.Log;

import com.meihu.beautylibrary.manager.MHBeautyManager;
import com.zego.zegoavkit2.videofilter.ZegoVideoFilter;
import com.zego.zegoavkit2.videofilter.ZegoVideoFilterFactory;

/**
 * Created by robotding on 16/12/3.
 */

public class VideoFilterFactory extends ZegoVideoFilterFactory {
    private int mode = 6;
    private VideoFilterHybrid mFilter = null;
    private MHBeautyManager mhBeautyManager;

    public VideoFilterFactory(MHBeautyManager mhBeautyManager) {
//        this.mhBeautyManager = mhBeautyManager;
    }

    @Override
    public ZegoVideoFilter create() {
        Log.d("meihu_sdk_FilterFactory","------create");
        mFilter = new VideoFilterHybrid();
        if (mhBeautyManager != null) {
            mFilter.setMhBeautyManager(mhBeautyManager);
        }
        return mFilter;
    }

    public void setMhBeautyManager(MHBeautyManager mhBeautyManager) {
        Log.d("meihu_sdk_FilterFactory","------setMhBeautyManager");
        this.mhBeautyManager = mhBeautyManager;
        if (mFilter != null) {
            mFilter.setMhBeautyManager(mhBeautyManager);
        }
    }

    public void destroy(ZegoVideoFilter vf) {
        if (mhBeautyManager != null) {
            mhBeautyManager.destroy();
        }
        mhBeautyManager = null;
        mFilter = null;
    }
}
