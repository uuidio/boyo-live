package com.kol.jumhz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.VideoViewConfig;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.kol.jumhz.common.net.UserMgr;
import com.kongzue.dialog.util.DialogSettings;
import com.meihu.beautylibrary.MHSDK;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.rtmp.TXLiveBase;

import sm.utils.UtilsInitializer;

/*import com.meihu.beautylibrary.MHSDK;*/

public class Application extends android.app.Application {

    /**
     * bugly 组件的 AppId
     * <p>
     * bugly sdk 系腾讯提供用于 APP Crash 收集和分析的组件。
     */
    public static final String BUGLY_APPID = "779285d5a7";

    private static final String TAG = "TCApplication";

    public static IWXAPI api;

    @Override
    public void onCreate() {
        super.onCreate();
        //NaoManager.INSTANCE.init(this, BuildConfig.BUILD_TYPE);
        UtilsInitializer.init(this);
        // 必须：初始化 LiteAVSDK Licence。 用于直播推流鉴权。
        TXLiveBase.getInstance().setLicence(this, GlobalConfig.LICENCE_URL, GlobalConfig.LICENCE_KEY);

        // 必须：初始化 MLVB 组件
        //MLVBLiveRoomImpl.sharedInstance(this);

        // 初始化腾讯提供用于 APP Crash 收集和分析的组件
        if (!BuildConfig.DEBUG) {// 仅release版本才检测更新和上传错误日志
            CrashReport.initCrashReport(getApplicationContext(), BUGLY_APPID, false);
        }

        // 必须：初始化全局的 用户信息管理类，记录个人信息。
        UserMgr.getInstance().initContext(getApplicationContext());

        // 初始化美狐SDK
        MHSDK.getInstance().init(this, GlobalConfig.MHSDK_APPKEY);

        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.config(this, loginInfo(), options());

        // 初始化Dialog
        initDialogSettings();

        // 注册到微信
        initregToWx();

        // 精彩回放使用IjkPlayer解码
        VideoViewManager.setConfig(VideoViewConfig.newBuilder().setPlayerFactory(IjkPlayerFactory.create()).build());

    }

    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();
        return options;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo loginInfo() {
        return null;
    }


    private void initDialogSettings() {
        DialogSettings.isUseBlur = (true);                   //是否开启模糊效果，默认关闭
        DialogSettings.modalDialog = (false);                 //是否开启模态窗口模式，一次显示多个对话框将以队列形式一个一个显示，默认关闭
        DialogSettings.style = (DialogSettings.STYLE.STYLE_MATERIAL);          //全局主题风格，提供三种可选风格，STYLE_MATERIAL, STYLE_KONGZUE, STYLE_IOS
        DialogSettings.theme = (DialogSettings.THEME.LIGHT);          //全局对话框明暗风格，提供两种可选主题，LIGHT, DARK
        DialogSettings.tipTheme = (DialogSettings.THEME.LIGHT);       //全局提示框明暗风格，提供两种可选主题，LIGHT, DARK
        //DialogSettings.titleTextInfo = (TextInfo);              //全局对话框标题文字样式
        //DialogSettings.menuTitleInfo = (TextInfo);              //全局菜单标题文字样式
        //DialogSettings.menuTextInfo = (TextInfo);               //全局菜单列表文字样式
        //DialogSettings.contentTextInfo = (TextInfo);            //全局正文文字样式
        //DialogSettings.buttonTextInfo = (TextInfo);             //全局默认按钮文字样式
        //DialogSettings.buttonPositiveTextInfo = (Sty);     //全局焦点按钮文字样式（一般指确定按钮）
        //DialogSettings.inputInfo = (InputInfo);                 //全局输入框文本样式
        DialogSettings.backgroundColor = (0);            //全局对话框背景颜色，值0时不生效
        DialogSettings.cancelable = (false);                  //全局对话框默认是否可以点击外围遮罩区域或返回键关闭，此开关不影响提示框（TipDialog）以及等待框（TipDialog）
        DialogSettings.cancelableTipDialog = (false);         //全局提示框及等待框（WaitDialog、TipDialog）默认是否可以关闭
        DialogSettings.DEBUGMODE = (true);                   //是否允许打印日志
        DialogSettings.blurAlpha = (200);                       //开启模糊后的透明度（0~255）
        //DialogSettings.systemDialogStyle = (styleResId);        //自定义系统对话框style，注意设置此功能会导致原对话框风格和动画失效
        //DialogSettings.dialogLifeCycleListener = ();  //全局Dialog生命周期监听器
        //DialogSettings.defaultCancelButtonText = (String);      //设置 BottomMenu 和 ShareDialog 默认“取消”按钮的文字
        //DialogSettings.tipBackgroundResId = (drawableResId);    //设置 TipDialog 和 WaitDialog 的背景资源
        //DialogSettings.tipTextInfo = (InputInfo);               //设置 TipDialog 和 WaitDialog 文字样式
        DialogSettings.autoShowInputKeyboard = (true);       //设置 InputDialog 是否自动弹出输入法

        //检查 Renderscript 兼容性，若设备不支持，DialogSettings.isUseBlur 会自动关闭；
        boolean renderscriptSupport = DialogSettings.checkRenderscriptSupport(getApplicationContext());

        DialogSettings.init();                           //初始化清空 BaseDialog 队列
    }


    private void initregToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, GlobalConfig.WX_APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(GlobalConfig.WX_APP_ID);
        //建议动态监听微信启动广播进行注册到微信
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 将该app注册到微信
                api.registerApp(GlobalConfig.WX_APP_ID);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));

    }

}
