package com.kol.jumhz.live;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Service;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.Application;
import com.kol.jumhz.GlobalConfig;
import com.kol.jumhz.R;
import com.kol.jumhz.anchor.BaseAnchorActivity;
import com.kol.jumhz.common.msg.SimpleUserInfo;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.JWebSocketClient;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.BitmapUtil;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.utils.Constants;
import com.kol.jumhz.common.utils.T;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.common.widget.UserAvatarListAdapter;
import com.kol.jumhz.common.widget.like.HeartLayout;
import com.kol.jumhz.view.LiveShareDialogFragment;
import com.kol.jumhz.view.SharePopwindowView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.manager.LogManager;
import com.meihu.beautylibrary.manager.MHBeautyManager;
import com.meihu.phonebeautyui.ui.bean.FilterBean;
import com.meihu.phonebeautyui.ui.enums.FilterEnum;
import com.meihu.phonebeautyui.ui.interfaces.DefaultBeautyEffectListener;
import com.meihu.phonebeautyui.ui.views.BaseBeautyViewHolder;
import com.meihu.phonebeautyui.ui.views.BeautyDataModel;
import com.meihu.phonebeautyui.ui.views.BeautyViewHolderFactory;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import static android.graphics.BitmapFactory.decodeResource;
import static com.tencent.rtmp.TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO;
import static com.tencent.rtmp.TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO;

/**
 * @ClassName: LiveActivity
 * @Author: LanLnk
 * @CreateDate: 2020-04-30 10:52
 * @Description: 主播端推流界面
 */
public class LiveActivity extends BaseAnchorActivity implements TXLivePusher.VideoCustomProcessListener, DefaultBeautyEffectListener {
    public TXCloudVideoView mTXCloudVideoView;      // 主播本地预览的 View

    // 观众头像列表控件
    private RecyclerView mUserAvatarList;        // 用户头像的列表控件
    private UserAvatarListAdapter mAvatarListAdapter;     // 头像列表的 Adapter

    private ImageView mRecordBall;            // 表明正在录制的红点球
    private TextView mBroadcastTime;         // 已经开播的时间
    private TextView mHeat;                 // 热度/观看人数
    private TextView mLiveName;           // 主播名称
    private View mAnchorInfo;             // 主播头像控件
    private TextView mLikeName;           // 点赞数量
    private TextView mTvNotice;           // 公告

    private RelativeLayout rlBtnTop; //顶部按钮布局
    private RelativeLayout rlNotice; //公告布局

    private RelativeLayout rlLottery; //开奖布局
    private TextView mTvLottery; //开奖秒数

    private int pushCount;                  //推流成功标识

    private ObjectAnimator mObjAnim;               // 动画
    private HeartLayout mHeartLayout;           // 点赞动画的布局


    protected MHBeautyManager mhBeautyManager;
    protected BaseBeautyViewHolder beautyViewHolder;
    private RelativeLayout rootLayout;
    public TXLivePusher mLivePusher;

    private Thread beautyThread;
    private TXLivePushConfig mLivePushConfig;
    private boolean isResume;
    private boolean mIsPreView;
    protected Handler mListenerHandler = null;
    protected LivePushListener mLivePushListener;

    private String mRoomTitle;
    private String mRoomImage;

    private String shareImage = "";
    private String shareWechat_img = "";

    private MyCount mTimer;

    private boolean                         mShowLog;    //log相关

    private SharePopwindowView mPopwindow;
    private int mLiveId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this).statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
        mRoomTitle = getIntent().getStringExtra(Constants.ROOM_TITLE);
        mRoomImage = getIntent().getStringExtra(Constants.COVER_PIC);
        getUserInfo();
        initPusher();

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_live);
        super.initView();

        rootLayout = findViewById(R.id.rl_root);

        mTXCloudVideoView = findViewById(R.id.anchor_video_view);
        mTXCloudVideoView.setLogMargin(10, 10, 45, 55);
        Button mBtnShare = findViewById(R.id.btn_share);
        RelativeLayout mRlGoods = findViewById(R.id.rl_goods);
        mRlGoods.setVisibility(View.INVISIBLE);

        // 用户头像的列表控件
        mUserAvatarList = findViewById(R.id.anchor_rv_avatar);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //mUserAvatarList.setLayoutManager(linearLayoutManager);

        mBroadcastTime = findViewById(R.id.anchor_tv_broadcasting_time);
        mBroadcastTime.setText(String.format(Locale.US, "%s", "00:00:00"));
        mRecordBall = findViewById(R.id.anchor_iv_record_ball);

        // 主播信息
        ImageView mHeadIcon = findViewById(R.id.anchor_iv_head_icon);
        showHeadIcon(mHeadIcon, UserMgr.getInstance().getAvatar());
        mLiveName = findViewById(R.id.anchor_tv_live_name);
        mAnchorInfo = findViewById(R.id.layout_live_pusher_info);

        rlBtnTop = findViewById(R.id.rl_btn_top);
        rlNotice = findViewById(R.id.rl_notice);

        mHeat = findViewById(R.id.anchor_tv_member_counts);
        mHeat.setText("0 观看");
        mTvNotice = findViewById(R.id.tv_notice);

        rlLottery = findViewById(R.id.rl_lottery);
        mTvLottery = findViewById(R.id.tv_lottery);

        mHeartLayout = findViewById(R.id.heart_layout);
        mLikeName = findViewById(R.id.tv_likeNum);

        Button mBtnGood = findViewById(R.id.btn_good);

        //点击分享
        mBtnShare.setOnClickListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.btn_share)) { return; }
            //显示分享界面
            mPopwindow = new SharePopwindowView(this, itemsOnClick);
            mPopwindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        });

        //长按显示log
        mBtnGood.setOnLongClickListener(view -> {
            Vibrator vib = (Vibrator)this.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(30);//只震动一秒，一次

            mShowLog = !mShowLog;
            mTXCloudVideoView.showLog(mShowLog);
            int isVisible = mShowLog ? View.INVISIBLE : View.VISIBLE;
            mAnchorInfo.setVisibility(isVisible);
            rlBtnTop.setVisibility(isVisible);
            rlNotice.setVisibility(isVisible);
            return true;
        });
        initBeautyView();
    }

    private void getUserInfo(){
        //页面展示之前，更新一下用户信息
        UserMgr.getInstance().fetchUserInfo(new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                LiveActivity.this.runOnUiThread(() -> mLiveName.setText(UserMgr.getInstance().getNickname()));
            }
            @Override
            public void onFailure(int code, final String msg) {
            }
        });
    }

    /**
     * 为弹出窗口实现监听类
     */
    private final View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPopwindow.dismiss();
            mPopwindow.backgroundAlpha(LiveActivity.this, 1f);
            switch (v.getId()) {
                case R.id.rl_wechat:
                    WaitDialog.show(LiveActivity.this, "");
                    new Thread(() -> {
                        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
                        miniProgramObj.webpageUrl = "http://www.qq.com"; //兼容低版本的网页链接
                        miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE; //正式版:0，测试版:1，体验版:2
                        miniProgramObj.userName = GlobalConfig.WX_INIT_ID; //小程序原始id
                        miniProgramObj.path = "live/pages/lives/lives?liveid="+mLiveId; //小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
                        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
                        msg.title = mRoomTitle; // 小程序消息title
                        msg.description = ""; // 小程序消息desc
                        Bitmap bitmap = Utils.urlToBitmap(LiveActivity.this,mRoomImage);
                        Bitmap minBitmap = BitmapUtil.compressImageSize(bitmap);
                        msg.thumbData = Utils.bitmapToByte(minBitmap); // 小程序消息封面图片，小于128k
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "miniProgram";
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession; //目前只支持会话
                        Application.api.sendReq(req);
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            TipDialog.show(LiveActivity.this, "", TipDialog.TYPE.SUCCESS);
                        });
                    }).start();
                    break;
                case R.id.rl_save_photo:
                    WaitDialog.show(LiveActivity.this, "");
                    getShareInfo();
                    break;
                default:
            }
        }

    };

    /**
     * 生成海报
     */
    private void getShareInfo() {
        UserMgr.getInstance().shareLiveRoom(new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    if (data.optJSONObject("result") == null) { return; }
                    if (data.optJSONObject("result").optJSONObject("data") == null) { return; }
                    try {
                        shareImage = data.optJSONObject("result").optJSONObject("data").getString("image");
                    } catch (JSONException e) {
                        WaitDialog.dismiss();
                        e.printStackTrace();
                    }
                    try {
                        shareWechat_img = data.optJSONObject("result").optJSONObject("data").getString("wechat_img");
                    } catch (JSONException e) {
                        WaitDialog.dismiss();
                        e.printStackTrace();
                    }
                    //显示分享界面
                    LiveShareDialogFragment liveShareDialogFragment = new LiveShareDialogFragment();
                    Bundle args = new Bundle();
                    args.putString("img1", shareImage);
                    args.putString("img2", UserMgr.getInstance().getAvatar());
                    args.putString("img3", shareWechat_img);
                    args.putString("name", UserMgr.getInstance().getNickname());
                    args.putString("info", mRoomTitle);
                    liveShareDialogFragment.setArguments(args);
                    liveShareDialogFragment.setCancelable(false);
                    if (liveShareDialogFragment.isAdded()) { liveShareDialogFragment.dismiss(); }
                    else { liveShareDialogFragment.show(getFragmentManager(), ""); }
                    TipDialog.show(LiveActivity.this, "", TipDialog.TYPE.SUCCESS);
                });
            }
            @Override
            public void onFailure(int code, String msg) {
                TipDialog.show(LiveActivity.this, msg, TipDialog.TYPE.WARNING);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogManager.getInstance().writeData("MainActivity_onStart()");
        mTXCloudVideoView.setVisibility(View.VISIBLE);
        push();

        isResume = true;
    }

    private void initBeautyView() {
        if (MHSDK.getInstance().isVerEnd()) {
            setBeautyView();
        } else {
            if (beautyThread != null) {
                beautyThread.interrupt();
                beautyThread = null;
            }
        }
    }

    private void setBeautyView() {
        LogManager.getInstance().writeData("MainActivity_setBeautyView_SDKVersion=" + MHSDK.getInstance().getVer());
        if (beautyViewHolder != null) {
            beautyViewHolder.release();
            beautyViewHolder = null;
        }
        beautyViewHolder = BeautyViewHolderFactory.getBeautyViewHolder(getApplicationContext(), rootLayout);
        //beautyViewHolder.show();
        beautyViewHolder.setEffectListener(this);
        beautyViewHolder.hide();
        beautyViewHolder.setMhBeautyManager(mhBeautyManager);
    }

    private void initPusher() {
        mLivePusher = new TXLivePusher(this);
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setVideoFPS(20); //设置视频帧率
        mLivePushConfig.setVideoEncodeGop(3); //视频编码 GOP
        mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_720_1280); //采集的视频的分辨率
        mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_HARDWARE); //编码
        mLivePushConfig.setAutoAdjustStrategy(TXLiveConstants.AUTO_ADJUST_BITRATE_STRATEGY_1); //动态调整码率的策略

        //自动模式, 极速模式:true 1 1, 流畅模式:false 5 5
        mLivePushConfig.setAutoAdjustBitrate(true); //是否开启码率自适应
        mLivePushConfig.setMaxVideoBitrate(1);
        mLivePushConfig.setMinVideoBitrate(5);

        // bitmap: 用于指定垫片图片，最大尺寸不能超过 1920*1920
        // time：垫片最长持续时间，单位是秒，300即代表最长持续300秒
        // fps：垫片帧率，最小值为 5fps，最大值为 20fps。
        Bitmap bitmap = decodeResource(getResources(), R.drawable.pause_publish);
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setPauseImg(300, 5);
        //表示同时暂停视频和音频采集
        mLivePushConfig.setPauseFlag(PAUSE_FLAG_PAUSE_VIDEO|PAUSE_FLAG_PAUSE_AUDIO);

        mLivePusher.setMirror(true);
        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.setVideoProcessListener(this);
        //setPushScene(TXLiveConstants.VIDEO_QUALITY_STANDARD_DEFINITION, false);
        mLivePusher.startCameraPreview(mTXCloudVideoView);
        mLivePushListener = new LivePushListener();
        mLivePusher.setPushListener(mLivePushListener);

        setTxFilter();
    }

    private void push() {
        if (mIsPreView) {
            return;
        }
        WaitDialog.show(LiveActivity.this,"正在准备开播");
        mIsPreView = true;

        //初始化IM
        NIMClient.initSDK();

        UserMgr.getInstance().anchorPush(UserMgr.getInstance().getLiveTitle(), UserMgr.getInstance().getCoverPic(), "true", new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                final int code = data.optInt("code");
                final JSONObject result = data.optJSONObject("result");
                if (code == 0 && result != null) {
                    final String notice = result.optString("notice");
                    final int liveId = result.optInt("live_id");
                    mLiveId = result.optInt("live_id");
                    final String pushUrl = result.optString("push_url");
                    final String roomid = result.optString("roomid");
                    final String accid = result.optString("accid");
                    final String imToken = result.optString("im_token");
                    String imAddr = null;
                    try {
                        imAddr = Objects.requireNonNull(result.optJSONArray("im_addr")).getString(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    UserMgr.getInstance().setRoomId(roomid);

                    Log.e("LiveActivity", "postPushUrl: 推流地址" + pushUrl);

                    //WebSocket的连接
                    if (client.isOpen()) { return; }
                    try {
                        client.connectBlocking();
                        if (client.isOpen()) {
                            client.send("{\"handshake\":\"true\",\"live_id\":"+"\""+liveId+"\""+"}");
                        }
                    } catch (InterruptedException e) { e.printStackTrace(); }

                    //3.开始推流
                    String finalImAddr = imAddr;
                    startPushStream(pushUrl, TXLiveConstants.VIDEO_QUALITY_SUPER_DEFINITION, new StandardCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
                            T.showShort(getApplicationContext(),errInfo);
                            Log.e("LiveActivity", "onError: " + errInfo);
                        }
                        @Override
                        public void onSuccess() {
                            Log.e("LiveActivity", "调用onSuccess: ");
                            //推流过程中，可能会重复收到PUSH_EVT_PUSH_BEGIN事件，onSuccess可能会被回调多次，如果已经创建的房间，直接返回
                            if (pushCount > 0) {
                                return;
                            }
                            pushCount++;
                            mTvNotice.setText(notice);
                            TipDialog.show(LiveActivity.this,"期待你的表现", TipDialog.TYPE.SUCCESS);

                            // roomId 为聊天室id
                            EnterChatRoomData data = new EnterChatRoomData(roomid);
                            data.setNick(UserMgr.getInstance().getNickname());
                            // 独立模式的非匿名登录，帐号和密码必填，以account和token为例
                            data.setIndependentMode((roomId, account) -> {
                                //return ChatRoomHttpClient.getInstance().fetchChatRoomAddress(roomId, "accid");
                                return Collections.singletonList(finalImAddr);
                            }, accid, imToken);

                            NIMClient.getService(ChatRoomService.class).enterChatRoomEx(data, 1).setCallback(new RequestCallback<EnterChatRoomResultData>() {
                                @Override
                                public void onSuccess(EnterChatRoomResultData result) {
                                    Log.e("LiveActivity", "聊天室imAddr[0]" + finalImAddr);
                                    mAvatarListAdapter = new UserAvatarListAdapter(LiveActivity.this, accid);
                                    mUserAvatarList.setAdapter(mAvatarListAdapter);
                                    //callbackOnThread(callback, "onSuccess", roomid);
                                }
                                @Override
                                public void onFailed(int code) {
                                    Log.e("LiveActivity", "onFailed: " + code);
                                }
                                @Override
                                public void onException(Throwable exception) {
                                }
                            });
                        }
                    });
                } else {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        Log.e("LiveActivity", "postPushUrl: " +data.optString("message"));
                        TipDialog.show(LiveActivity.this, data.optString("message"), TipDialog.TYPE.WARNING);
                        T.showShort(LiveActivity.this, "正在直播中，请稍等片刻\n或者使用强制关闭功能");
                        finish();
                    });
                }
            }
            @Override
            public void onFailure(int code, final String msg) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    Log.e("LiveActivity", "anchorPush: " +msg);
                    TipDialog.show(LiveActivity.this, msg+":"+code, TipDialog.TYPE.WARNING);
                    T.showShort(LiveActivity.this, msg+":"+code);
                    finish();
                });
            }
        });
    }

    // WebSocket接收热度、观看人数、点赞数
    JWebSocketClient client = new JWebSocketClient(URI.create(GlobalConfig.WebSocketUrl)) {
        @Override
        public void onMessage(String message) {
            JsonObject returnData = new JsonParser().parse(message).getAsJsonObject();
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                switch (returnData.getAsJsonPrimitive("type").getAsString()) {
                    //观看人数
                    //case "audience":
                    //    mAudienceCount = Long.parseLong(returnData.getAsJsonPrimitive("audienceCount").getAsString());
                    //    mHeat.setText(Utils.formatNum(String.valueOf(mAudienceCount), false) + " 观看");
                    //    break;
                    //热度
                    case "hot":
                        mHeatCount = Long.parseLong(returnData.getAsJsonPrimitive("hot_count").getAsString());
                        //mHeat.setText(Utils.formatNum(String.valueOf(mHeatCount), false) + " 热度");
                        break;
                    //点赞数
                    case "like":
                        //mHeartLayout.addFavor();
                        mHeartCount = Long.parseLong(returnData.getAsJsonPrimitive("count").getAsString());
                        mLikeName.setText(Utils.formatNum(String.valueOf(mHeartCount), false));
                        break;
                    //公告
                    case "notice":
                        mTvNotice.setText(returnData.getAsJsonPrimitive("notice").getAsString());
                        break;
                    //点抽奖
                    case "raffles":
                        if (mTimer == null) {
                            long time = Long.parseLong(returnData.getAsJsonPrimitive("time").getAsString());
                            mTimer = new MyCount(time * 1000, 1000,
                                    returnData.getAsJsonPrimitive("raffles_id").getAsString());
                            mTimer.start();
                        }
                        break;
                    default:
                }
        });
            Log.e("JWebSocketClient", "onMessage()"+returnData);
        }
    };


    protected void startPushStream(final String url, final int videoQuality, final StandardCallback callback) {
        //在主线程开启推流
        Handler handler = new Handler(getApplicationContext().getMainLooper());
        handler.post(() -> {
            if (mLivePusher != null) {
                mLivePushListener.setCallback(callback);
                mLivePusher.setVideoQuality(videoQuality, true, false);
                int ret = mLivePusher.startPusher(url);
                if (ret == -5) {
                    T.showShort(getApplicationContext(),ret);
                    String msg = "推流失败[license 校验失败]";
                    showErrorAndQuit(ret, msg);
                    Log.e("LiveActivity", "startPushStream: " +msg);
                    if (callback != null) {
                        callback.onError(-5, msg);
                    }
                } else { callback.onSuccess(); }
            } else {
                String msg = "推流失败[LivePusher未初始化，请确保已经调用startLocalPreview]";
                Log.e("LiveActivity", "startPushStream: " + msg);
                T.showShort(getApplicationContext(),msg);
                if (callback != null) {
                    callback.onError(-3, msg);
                }
            }
        });
    }
    public interface StandardCallback {
        /**
         * @param errCode 错误码
         * @param errInfo 错误信息
         */
        void onError(int errCode, String errInfo);

        void onSuccess();
    }

    protected class LivePushListener implements ITXLivePushListener {
        private StandardCallback mCallback = null;

        public void setCallback(StandardCallback callback) {
            mCallback = callback;
        }

        @Override
        public void onPushEvent(final int event, final Bundle param) {
            Log.e("LiveActivity", "onPushEvent: " +event);
            if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {
                Log.e("LiveActivity", "onPushEvent: " +"推流成功" );
                callbackOnThread(mCallback, "onSuccess");
            } else if (event == TXLiveConstants.PUSH_WARNING_NET_BUSY) {
                String msg = "推流警告[上行网速不够用，建议提示用户改善当前的网络环境]";
                Log.e("LiveActivity", "onPushEvent: " +msg );
                T.showLong(getApplicationContext(), "网络不给力\n检查网络环境");
                callbackOnThread(mCallback, "onSuccess");
            } else if (event == TXLiveConstants.PUSH_WARNING_READ_WRITE_FAIL) {
                String msg = "推流警告[RTMP 读/写失败，将会断开连接]";
                Log.e("LiveActivity", "onPushEvent: " +msg );
                final AlertDialog dlg = new AlertDialog.Builder(LiveActivity.this).create();
                Window window = dlg.getWindow();
                //这一句消除白块
                window.setBackgroundDrawable(new BitmapDrawable());
                dlg.show();
                dlg.getWindow().setContentView(R.layout.dialog_live_newworkerr);
                assert window != null;
                TextView tv = window.findViewById(R.id.tv);
                tv.setText("当前网络环境无法进行直播\n建议改善后再进行");
                TextView ok = window.findViewById(R.id.tv_ok);
                ok.setOnClickListener(v -> {
                    dlg.dismiss();
                    stopPublish();
                });
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
                String msg = "推流失败[打开摄像头失败]";
                Log.e("LiveActivity", "onPushEvent: " +msg );
                T.showShort(getApplicationContext(),msg);
                callbackOnThread(mCallback, "onError", event, msg);
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
                String msg = "推流失败[打开麦克风失败]";
                Log.e("LiveActivity", "onPushEvent: " +msg );
                T.showShort(getApplicationContext(),msg);
                callbackOnThread(mCallback, "onError", event, msg);
            } else if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || event == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
                String msg = "推流失败[网络断开]";
                Log.e("LiveActivity", "onPushEvent: " +msg );
                T.showShort(getApplicationContext(),msg);
                callbackOnThread(mCallback, "onError", event, msg);
            } else if (event == TXLiveConstants.PUSH_ERR_SCREEN_CAPTURE_START_FAILED) {
                String msg = "推流失败[录屏启动失败]";
                Log.e("LiveActivity", "onPushEvent: " +msg );
                T.showShort(getApplicationContext(),msg);
                callbackOnThread(mCallback, "onError", event, msg);
            }
        }

        @Override
        public void onNetStatus(Bundle status) {
        }
    }

    private void callbackOnThread(final Runnable runnable) {
        if (runnable == null) {
            return;
        }
        mListenerHandler.post(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    private void callbackOnThread(final Object object, final String methodName, final Object... args) {
        if (object == null || methodName == null || methodName.length() == 0) {
            return;
        }
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            Class objClass = object.getClass();
            while (objClass != null) {
                Method[] methods = objClass.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName() == methodName) {
                        try {
                            method.invoke(object, args);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
                objClass = objClass.getSuperclass();
            }
        });
    }

    //停止直播
    private void stopPush() {
        mLivePusher.stopCameraPreview(true);
        mLivePusher.setPushListener(null);
        mTXCloudVideoView.setVisibility(View.GONE);
        mIsPreView = false;
        try {
            if (client != null) {
                client.close();
                Log.e("JWebSocketClient", "断开连接");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
            Log.e("JWebSocketClient", "断开连接");
        }
    }

    /**
     * 显示确认消息
     *
     * @param msg     消息内容
     * @param isError true错误消息（必须退出） false提示消息（可选择是否退出）
     */
    private void showExitInfoDialog(String msg, Boolean isError) {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        Window window = dlg.getWindow();
        //这一句消除白块
        window.setBackgroundDrawable(new BitmapDrawable());
        dlg.show();
        dlg.getWindow().setContentView(R.layout.dialog_tips);
        TextView tv = window.findViewById(R.id.tv);
        tv.setText(msg);
        TextView on = window.findViewById(R.id.tv_no);
        TextView ok = window.findViewById(R.id.tv_ok);
        if (!isError) {
            ok.setOnClickListener(v -> {
                mLivePusher.stopPusher();
                dlg.dismiss();
                stopPublish();
            });
            on.setOnClickListener(v -> dlg.cancel());
        } else {
            //当情况为错误的时候，直接停止推流
            stopPublish();
            ok.setOnClickListener(v -> {
                dlg.dismiss();
                showPublishFinishDetailsDialog(0);
            });
        }
    }

    private void setTxFilter() {
        int[] currentBeautyMap = BeautyDataModel.getInstance().getCurrentBeautyMap();
        mLivePusher.setBeautyFilter(TXLiveConstants.BEAUTY_STYLE_SMOOTH, currentBeautyMap[1], currentBeautyMap[0], currentBeautyMap[2]);
    }

    @Override
    public void onFilterChanged(FilterBean filterBean) {
        if (mLivePusher == null || mhBeautyManager == null) {
            return;
        }
        FilterEnum filterEnum = filterBean.getFilterEnum();
        if (filterEnum == FilterEnum.PRO_FILTER) {
            mhBeautyManager.changeDynamicFilter(filterBean.getmFilterName());
            mLivePusher.setFilter(null);
        } else {
            Bitmap lookupBitmap = null;
            switch (filterEnum) {
                case NO_FILTER:
                    break;
                case ROMANTIC_FILTER:
                    lookupBitmap = decodeResource(getResources(), R.drawable.filter_langman);
                    break;
                case FRESH_FILTER:
                    lookupBitmap = decodeResource(getResources(), R.drawable.filter_qingxin);
                    break;
                case BEAUTIFUL_FILTER:
                    lookupBitmap = decodeResource(getResources(), R.drawable.filter_weimei);
                    break;
                case PINK_FILTER:
                    lookupBitmap = decodeResource(getResources(), R.drawable.filter_fennen);
                    break;
                case NOSTALGIC_FILTER:
                    lookupBitmap = decodeResource(getResources(), R.drawable.filter_huaijiu);
                    break;
                case COOL_FILTER:
                    lookupBitmap = decodeResource(getResources(), R.drawable.filter_qingliang);
                    break;
                case BLUES_FILTER:
                    lookupBitmap = decodeResource(getResources(), R.drawable.filter_landiao);
                    break;
                case JAPANESE_FILTER:
                    lookupBitmap = decodeResource(getResources(), R.drawable.filter_rixi);
                    break;
                default:
            }
            mhBeautyManager.changeDynamicFilter("");
            mLivePusher.setFilter(lookupBitmap);
        }
    }

    @Override
    public void onBeautyOrigin() {
        setTxFilter();
    }

    @Override
    public void onMeiBaiChanged(int progress) {
        setTxFilter();
    }

    @Override
    public void onMoPiChanged(int progress) {
        setTxFilter();
    }

    @Override
    public void onFengNenChanged(int progress) {
        setTxFilter();
    }

    /**
     * 加载主播头像
     *
     * @param view   view
     * @param avatar 头像链接
     */
    private void showHeadIcon(ImageView view, String avatar) {
        Utils.showPicWithUrl(this, view, avatar, R.drawable.ic_camera_download_bg);
    }


    @Override
    protected void onCreateRoomSuccess() {
        super.onCreateRoomSuccess();
        //startRecordAnimation();
    }

    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      界面动画与时长统计
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */
    /**
     * 开启红点与计时动画
     */
    private void startRecordAnimation() {
        mObjAnim = ObjectAnimator.ofFloat(mRecordBall, "alpha", 1f, 0f, 1f);
        mObjAnim.setDuration(1000);
        mObjAnim.setRepeatCount(-1);
        mObjAnim.start();
    }

    /**
     * 关闭红点与计时动画
     */
    private void stopRecordAnimation() {
        if (null != mObjAnim) {
            mObjAnim.cancel();
        }
    }

    @Override
    protected void onBroadcasterTimeUpdate(long second) {
        super.onBroadcasterTimeUpdate(second);
        if (!mSwipeAnimationController.isMoving()) {
            mBroadcastTime.setText(Utils.formattedTime(second));
        }
    }

    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      抽奖倒计时事件
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    class MyCount extends CountDownTimer{
        private String rafflesId;
        private MyCount(long millisInFuture, long countDownInterval, String rafflesId) {
            super(millisInFuture, countDownInterval);
            this.rafflesId = rafflesId;
        }
        @Override
        public void onTick(long millisUntilFinished) {
            rlLottery.setVisibility(View.VISIBLE);
            mTvLottery.setText((millisUntilFinished / 1000) + "秒后开奖......");
        }
        @Override
        public void onFinish() {
            UserMgr.getInstance().openLottery(rafflesId, new HTTPMgr.Callback() {
                        @Override
                        public void onSuccess(JSONObject data) {
                            Log.e("LiveActivity", "开奖onSuccess " +data);
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> {
                                rlLottery.setVisibility(View.INVISIBLE);
                                TipDialog.show(LiveActivity.this,"开奖成功", TipDialog.TYPE.SUCCESS); });
                        }
                        @Override
                        public void onFailure(int code, String msg) {
                        }
                    });
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }
    }

    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      点击事件与调用函数相关
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_cam:
                if (mLivePusher != null) {
                    mLivePusher.switchCamera();
                }
                break;
            case R.id.anchor_btn_flash:
                break;
            case R.id.beauty_btn:
                if (beautyViewHolder.isShowed()) {
                    beautyViewHolder.hide();
                } else {
                    beautyViewHolder.show();
                }
                break;
            case R.id.btn_close:
                showExitInfoDialog("当前正在直播，是否退出直播？", false);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void showErrorAndQuit(int errorCode, String errorMsg) {
        super.showErrorAndQuit(errorCode, errorMsg);
    }

    @Override
    public int onTextureCustomProcess(int texture, int width, int height) {
        int textureId = texture;
        try {
            if (isResume) {
                if (mhBeautyManager == null) {
                    mhBeautyManager = new MHBeautyManager(getApplicationContext());
                    mhBeautyManager.setBeautyDataModel(BeautyDataModel.getInstance());
                    if (beautyViewHolder != null) {
                        beautyViewHolder.setMhBeautyManager(mhBeautyManager);
                    }
                    return textureId;
                }

                textureId = mhBeautyManager.render(texture, width, height);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textureId;
    }

    /**
     * 销毁释放MHBeautyManager资源
     */
    @Override
    public void onTextureDestoryed() {
        if (mhBeautyManager != null) {
            mhBeautyManager.destroy();
            mhBeautyManager = null;
        }
    }

    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      成员进退房事件信息处理
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    @Override
    protected void handleMemberJoinMsg(SimpleUserInfo userInfo) {
        if (mAvatarListAdapter == null) { return; }
        mCurrentMemberCount++;
        //更新头像列表 返回false表明已存在相同用户，将不会更新数据
        if (mAvatarListAdapter.addItem(userInfo)) {
            super.handleMemberJoinMsg(userInfo);
        }
        mHeat.setText(Utils.formatNum(String.valueOf(mCurrentMemberCount), false) + " 观看");
    }

    @Override
    protected void handleMemberQuitMsg(SimpleUserInfo userInfo) {
        if (mAvatarListAdapter == null) { return; }
        mAvatarListAdapter.removeItem(userInfo.userid);
        super.handleMemberQuitMsg(userInfo);
        //mMemberCount.setText("观看 "+String.format(Locale.CHINA, "%d", mCurrentMemberCount));
    }

    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      权限相关
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int ret : grantResults) {
                if (ret != PackageManager.PERMISSION_GRANTED) {
                    showErrorAndQuit(-1314, "获取权限失败");
                    return;
                }
            }
            this.startPublish();
        }
    }

    @Override
    public void onDetectFacePoints(float[] floats) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onResume();
        }
    }

    @Override
    protected void onStop() {
        Log.d("meihu_beauty", "TxMainActivity--onStop");
        LogManager.getInstance().writeData("MainActivity_onStop()");
        super.onStop();
        isResume = false;

        // 进入隐私模式
        mLivePusher.pausePusher();

        //stopPush();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onPause();
        }
        if (isFinishing()) {
            mLivePusher.stopCameraPreview(true);
            mLivePusher.stopPusher();
        }

        if (isFinishing()) {
            if (beautyThread != null) {
                beautyThread.interrupt();
                beautyThread = null;
            }
            Log.d("meihu_beauty", "isFinishing");
            if (beautyViewHolder != null) {
                beautyViewHolder.release();
            }
            // TODO: 2020/4/15   这里也必须添加，文档说的。
            if (mhBeautyManager != null) {
                mhBeautyManager.destroy();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 退出隐私模式
        mLivePusher.resumePusher();
    }

    @Override
    protected void onDestroy() {
        Log.d("meihu_beauty", "TxMainActivity--onDestroy");
        LogManager.getInstance().writeData("MainActivity_onDestroy()");
        if (beautyViewHolder != null) {
            beautyViewHolder.release();
        }
        /*if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }*/
        stopPush();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
        }
        // TODO: 2020/4/15  这一步还是必须实现，按文章说onDestroy 会有延迟，目前还没发现，资源不释放多操作几次就崩溃了。
        if (mhBeautyManager != null) { mhBeautyManager.destroy(); }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        LogManager.getInstance().closeFile();
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
