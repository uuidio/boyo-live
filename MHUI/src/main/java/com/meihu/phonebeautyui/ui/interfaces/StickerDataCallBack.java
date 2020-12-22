package com.meihu.phonebeautyui.ui.interfaces;

import java.util.List;

public interface StickerDataCallBack {
    void onStart();
    void onSuccess(List responseList);
    void onError(Exception e);
    void onCancel();
}
