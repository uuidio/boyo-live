package com.kol.jumhz.common.utils;

/**
 * @ClassName: ButtonUtils
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:07
 * @Description: 判断用户点击按钮间隔时间，如果间隔时间太短，则认为是无效操作，否则进行相关业务处理
 */
public class ButtonUtils {
    private static long lastClickTime = 0;
    private static long DIFF          = 2000;
    private static int  lastButtonId  = -1;

    /**
     * 判断两次点击的间隔，如果小于2000，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(-1, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于2000，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId) {
        return isFastDoubleClick(buttonId, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param diff
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId, long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) { ;
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }
}
