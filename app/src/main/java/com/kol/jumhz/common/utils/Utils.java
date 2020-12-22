package com.kol.jumhz.common.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.kol.jumhz.R;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.login.LoginActivity;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.tencent.cos.xml.utils.StringUtils;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.inter.IHandlerCallBack;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * @ClassName: Utils
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:10
 * @Description: 工具函数的集合类
 */
public class Utils {


    /**
     *
     * @param password 用户输入密码
     * @return 有效则返回true, 无效则返回false
     */
    public static boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.length() <= 16;
    }

    public static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) hex.append('0');
            hex.append(Integer.toHexString(i));
        }

        return hex.toString();
    }

    /**
     *
     * @param username 用户名
     * @return 同上
     */
    public static boolean isUsernameVaild(String username) {
        return !username.matches("[0-9]+") && username.matches("^[a-z0-9_-]{4,24}$");
    }

    /**
     * 配置图片选择器 GalleryConfig
     * @param iHandlerCallBack 监听接口IHandlerCallBack
     * @return galleryConfig
     */
    public static GalleryConfig galleryConfig(IHandlerCallBack iHandlerCallBack) {
        return new GalleryConfig.Builder()
                .imageLoader(new GlideLoader())    // ImageLoader 加载框架（必填）
                .iHandlerCallBack(iHandlerCallBack)      // 监听接口（必填）
                .provider("com.kol.jumhz.myfileprovider")   // provider(必填)
                //.pathList(path)                          // 记录已选的图片
                .multiSelect(false, 9)  // 配置是否多选的同时 配置多选数量   默认：false ， 9
                .maxSize(9)                              // 配置多选时 的多选数量。    默认：9
                .crop(false)                             // 快捷开启裁剪功能，仅当单选 或直接开启相机时有效
                .crop(true, 0, 0, 760, 1000) // 配置裁剪功能的参数，默认裁剪比例 1:1
                .isShowCamera(true)                      // 是否现实相机按钮  默认：false
                .filePath("/Gallery/Pictures")           // 图片存放路径
                .build();
    }


    // 根据原图绘制圆形图片
    static public Bitmap createCircleImage(Bitmap source, int min){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (0 == min){
            min = source.getHeight()>source.getWidth() ? source.getWidth() : source.getHeight();
        }
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        // 创建画布
        Canvas canvas = new Canvas(target);
        // 绘圆
        canvas.drawCircle(min/2, min/2, min/2, paint);
        // 设置交叉模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 绘制图片
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }


    /**
     * Bitmap转换为二进制流
     *
     * @param bitmap 图片bitmap
     * @return imgBytes
     */
    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * url转换为Bitmap
     *
     * @param url 图片url
     * @return Bitmap
     */
    public static Bitmap urlToBitmap(final Context context, final String url){
        URL imageurl = null;
        try {
            imageurl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context)
                    .load(imageurl)
                    .asBitmap()
                    .into(360, 480).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    /**
     * 检查更新
     */
    public static void upDate(final Context context){
        new PgyUpdateManager.Builder()
                .setForced(true)                //设置是否强制提示更新,非自定义回调更新接口此方法有用
                .setUserCanRetry(true)         //失败后是否提示重新下载，非自定义下载 apk 回调此方法有用
                .setDeleteHistroyApk(true)     // 检查更新前是否删除本地历史 Apk， 默认为true
                .setUpdateManagerListener(new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        //没有更新是回调此方法
                    }
                    @Override
                    public void onUpdateAvailable(AppBean appBean) {
                        //有更新回调此方法
                        //调用以下方法，DownloadFileListener 才有效；
                        //如果完全使用自己的下载方法，不需要设置DownloadFileListener
                        final AlertDialog dlg = new AlertDialog.Builder(context).create();
                        Window window = dlg.getWindow();
                        //这一句消除白块
                        window.setBackgroundDrawable(new BitmapDrawable());

                        dlg.show();
                        dlg.getWindow().setContentView(R.layout.dialog_update);
                        dlg.setCancelable(false);
                        assert window != null;
                        TextView tv = window.findViewById(R.id.tv);
                        tv.setText("更新版本号: v"+appBean.getVersionName()+"\n"+appBean.getReleaseNote());
                        //TextView on = window.findViewById(R.id.tv_no);
                        TextView ok = window.findViewById(R.id.tv_ok);
                        ok.setOnClickListener(v -> {
                            dlg.dismiss();
                            T.showShort(context, "准备更新，请稍后...");
                            PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
                        });
                        //on.setOnClickListener(v -> dlg.cancel());
                    }

                    @Override
                    public void checkUpdateFailed(Exception e) {
                        //更新检测失败回调
                        //更新拒绝（应用被下架，过期，不在安装有效期，下载次数用尽）以及无网络情况会调用此接口
                        Log.e("pgyer", "check update failed ", e);
                    }
                })
                //注意 ：
                //下载方法调用 PgyUpdateManager.downLoadApk(appBean.getDownloadURL()); 此回调才有效
                //此方法是方便用户自己实现下载进度和状态的 UI 提供的回调
                //想要使用蒲公英的默认下载进度的UI则不设置此方法
                //.setDownloadFileListener(new DownloadFileListener() {
                //    @Override
                //    public void downloadFailed() {
                //        //下载失败
                //        Log.e("pgyer", "download apk failed");
                //    }
                //
                //    @Override
                //    public void downloadSuccessful(File file) {
                //        Log.e("pgyer", "download apk success");
                //        // 使用蒲公英提供的安装方法提示用户 安装apk
                //        PgyUpdateManager.installApk(file);
                //    }
                //
                //    @Override
                //    public void onProgressUpdate(Integer... integers) {
                //        Log.e("pgyer", "update download apk progress" + integers);
                //    }})
                .register();
    }

    /*
     * @param num 格式化的数字
     * @param kBool 是否格式化千,为true,并且num大于999就显示999+,小于等于999就正常显示
     * @return
     */
    public static String formatNum(String num, Boolean kBool) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isEmpty(num)) {
            return "0";
        }
        if (kBool == null) {
            kBool = false;
        }
        BigDecimal b0 = new BigDecimal("1000");
        BigDecimal b1 = new BigDecimal("10000");
        BigDecimal b2 = new BigDecimal("10000000");
        BigDecimal b3 = new BigDecimal(num);

        String formatNumStr = "";
        String nuit = "";

        // 以千为单位处理
        if (kBool) {
            if (b3.compareTo(b0) == 0 || b3.compareTo(b0) == 1) {
                return "999+";
            }
            return num;
        }
        // 以万为单位处理
        if (b3.compareTo(b1) == -1) {
            sb.append(b3.toString());
        } else if ((b3.compareTo(b1) == 0 && b3.compareTo(b1) == 1) || b3.compareTo(b2) == -1) {
            formatNumStr = b3.divide(b1).toString();
            nuit = "W";
        } else if (b3.compareTo(b2) == 0 || b3.compareTo(b2) == 1) {
            formatNumStr = b3.divide(b2).toString();
            nuit = "KW";
        }
        if (!"".equals(formatNumStr)) {
            int i = formatNumStr.indexOf(".");
            if (i == -1) {
                sb.append(formatNumStr).append(nuit);
            } else {
                i = i + 1;
                String v = formatNumStr.substring(i, i + 1);
                if (!"0".equals(v)) {
                    sb.append(formatNumStr.substring(0, i + 1)).append(nuit);
                } else {
                    sb.append(formatNumStr.substring(0, i - 1)).append(nuit);
                }
            }
        }
        if (sb.length() == 0) {
            return "0";
        }
        return sb.toString();
    }

    // 字符串截断
    public static String getLimitString(String source, int length){
        if (null!=source && source.length()>length){
//            int reallen = 0;
            return source.substring(0, length)+"...";
        }
        return source;
    }

    // 字符串截断
    public static String getLimitStringWithoutNode(String source, int length){
        if (null != source && source.length() > length){
            return source.substring(0, length);
        }
        return source;
    }
    /**
     * 根据枚举值转换具体的性别
     *
     * @param genderType
     * @return
     */
    public static String EnumGenderToString (int genderType) {
        if (Constants.MALE == genderType)  return "男";
        if (Constants.FEMALE == genderType) return "女";

        return "";
    }

    /**
     * 时间格式化
     */
    public static String formattedTime(long second) {
        String hs, ms, ss, formatTime;

        long h, m, s;
        h = second / 3600;
        m = (second % 3600) / 60;
        s = (second % 3600) % 60;
        if (h < 10) {
            hs = "0" + h;
        } else {
            hs = "" + h;
        }

        if (m < 10) {
            ms = "0" + m;
        } else {
            ms = "" + m;
        }

        if (s < 10) {
            ss = "0" + s;
        } else {
            ss = "" + s;
        }
//        if (hs.equals("00")) {
//            formatTime = ms + ":" + ss;
//        } else {
            formatTime = hs + ":" + ms + ":" + ss;
//        }

        return formatTime;
    }

    public static String duration(long durationMs) {
        long duration = durationMs / 1000;
        long h = duration / 3600;
        long m = (duration - h * 3600) / 60;
        long s = duration - (h * 3600 + m * 60);

        String durationValue;

        if (h == 0) {
            durationValue = asTwoDigit(m) + ":" + asTwoDigit(s);
        } else {
            durationValue = asTwoDigit(h) + ":" + asTwoDigit(m) + ":" + asTwoDigit(s);
        }
        return durationValue;
    }

    public static String asTwoDigit(long digit) {
        String value = "";

        if (digit < 10) {
            value = "0";
        }

        value += String.valueOf(digit);
        return value;
    }

    public static int dp2pxConvertInt(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static float sp2px(Context context, float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }
    /**
     * 圆角显示图片
     *
     * @param context 一般为activtiy
     * @param view 图片显示类
     * @param url 图片url
     * @param defResId 默认图 id
     *
     */
    public static void showPicWithUrl(Context context, ImageView view, String url, int defResId) {
        if (context == null || view == null) {
            return;
        }
        try {
            if (TextUtils.isEmpty(url)){
                view.setImageResource(defResId);
            } else {
                RequestManager req = Glide.with(context);
                req.load(url).placeholder(defResId).transform(new GlideCircleTransform(context)).into(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 高斯模糊
     */
    public static void blurBgPic(final Context context, final ImageView view, final String url, int defResId) {
        if (context == null || view == null) {
            return;
        }

        if (TextUtils.isEmpty(url)) {
            view.setImageResource(defResId);
        } else {
            Glide.with(context.getApplicationContext())
                .load(url)
                .asBitmap()
                .into(view);
        }
    }

    /*
    * 获取网络类型
    */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取一段字符串的字符个数（包含中英文，一个中文算2个字符）
     */
    public static int getCharacterNum(final String content) {
        if (null == content || "".equals(content)) {
            return 0;
        } else {
            return (content.length() + getChineseNum(content));
        }
    }

    /**
     * 返回字符串里中文字或者全角字符的个数
     */
    public static int getChineseNum(String s) {
        int num = 0;
        char[] myChar = s.toCharArray();
        for (int i = 0; i < myChar.length; i++) {
            if ((char) (byte) myChar[i] != myChar[i]) {
                num++;
            }
        }
        return num;
    }


    /**
     * 显示被踢下线通知
     * @param context activity
     */
    public static void showKickOut(final Context context) {
        Toast.makeText(context, "您的账号已在其他地方登录，您被迫下线。", Toast.LENGTH_SHORT).show();
        UserMgr.getInstance().logout();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * 根据比例转化实际数值为相对值
     * @param gear 档位
     * @param max 最大值
     * @param curr 当前值
     * @return 相对值
     */
    public static int filtNumber(int gear, int max, int curr) {
        return curr / (max / gear);
    }

    /**
     * 悬浮窗权限检查
     * 当前仅对MIUI8作了适配，其余机型有待添加
     * @throws IOException
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void checkFloatWindowPermission(final Context context) throws IOException {

        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));

        String versionmame = properties.getProperty("ro.miui.ui.version.name");
        if(null != versionmame){
            //miui8以上需要悬浮窗权限才能正常显示悬浮窗
            if (versionmame.equals("V8")) {

                //检测悬浮窗权限，无权限则弹出提示
                if (!checkPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.NormalDialog);
                    builder.setTitle(context.getString(R.string.float_window_not_allow));
                    builder.setPositiveButton("开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            final Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + context.getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            context.startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("不，谢谢", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }
        }
    }

    /**
     * 权限检查
     * @param context context
     * @param permission permission
     * @return true -- 当前拥有该权限  false -- 当前无权限
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkPermission(@NonNull final Context context, String permission) {
        boolean result = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                final PackageInfo info = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0);
                int targetSdkVersion = info.applicationInfo.targetSdkVersion;
                //targetSDKVersion为23时，可以直接通过context.checkSelfPermission检查权限
                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    result = context.checkSelfPermission(permission)
                            == PackageManager.PERMISSION_GRANTED;
                } else {
                    result = PermissionChecker.checkSelfPermission(context, permission)
                            == PermissionChecker.PERMISSION_GRANTED;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
