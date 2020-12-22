package com.meihu.phonebeautyui.ui.constant;

import android.os.Environment;
import android.support.annotation.Keep;

import java.io.File;

public class Constants {
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String DIR_NAME = "meihu";
    public static final String PAYLOAD = "payload";
    public static final String WATERMARK_ASSETS_FORDERNAME = "watermark";
    public static final String WATERMARK_ICON_FORDERNAME = "imgicons";
    public static final String WATERMARK_RES_FORDERNAME = "imgres";
    public static final String VIDEO_TIE_ZHI_RESOURCE_ZIP_PATH = ROOT_PATH + "/" + DIR_NAME + "/tieZhi/";
}
