package com.kol.jumhz.live;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.Application;
import com.kol.jumhz.GlobalConfig;
import com.kol.jumhz.R;
import com.kol.jumhz.anchor.BaseAssistantActivity;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;
import com.kol.jumhz.common.msg.ChatMsgListAdapter;
import com.kol.jumhz.common.msg.SimpleUserInfo;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.JWebSocketClient;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.BitmapUtil;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.utils.T;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.common.widget.UserAvatarListAdapter;
import com.kol.jumhz.common.widget.like.HeartLayout;
import com.kol.jumhz.view.LiveShareDialogFragment;
import com.kol.jumhz.view.SharePopwindowView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.meihu.beautylibrary.manager.LogManager;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import cn.andy.qpopuwindow.QPopuWindow;

/**
 * @ClassName: LiveAssistantActivity
 * @Author: LanLnk
 * @CreateDate: 2020-04-30 10:52
 * @Description: 助理拉流界面
 */
public class LiveAssistantActivity extends BaseAssistantActivity {
    private TXCloudVideoView mTXCloudVideoView;      // 本地预览的 View

    private RecyclerView mUserAvatarList;        // 用户头像的列表控件
    private UserAvatarListAdapter mAvatarListAdapter;     // 头像列表的 Adapter

    private ImageView mHeadIcon;              // 主播头像
    private ImageView mRecordBall;            // 表明正在录制的红点球
    private TextView mBroadcastTime;         // 已经开播的时间
    private TextView mHeat;                 // 热度/观看人数
    private TextView mLiveName;           // 主播名称
    private TextView mLikeName;           // 点赞数量
    private TextView mTvNotice;           // 公告

    private int pushCount;                  //推流成功标识

    private ObjectAnimator mObjAnim;               // 动画
    private HeartLayout mHeartLayout;           // 点赞动画的布局


    private RelativeLayout mRestLayout;           // 主播休息的布局

    private TXLivePlayer mLivePlayer;

    private boolean mIsPreView;
    protected Handler mListenerHandler = null;
    protected TXLivePlayListenerImpl mTXLivePlayListener;

    private String shareTitle = "";          //直播间标题
    private String shareImage = "";         //直播间封面
    private String shareWechatImg = "";    //小程序码
    private String shareHeadImg = "";      //主播头像


    private SharePopwindowView mPopwindow;
    private int mLiveId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.BeautyTheme);
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
        initPuller();

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_live_assistant);
        super.initView();

        mTXCloudVideoView = findViewById(R.id.anchor_video_view);
        mTXCloudVideoView.setLogMargin(10, 10, 45, 55);
        Button mBtnShare = findViewById(R.id.btn_share);
        RelativeLayout mRlGoods = findViewById(R.id.rl_goods);
        mRlGoods.setVisibility(View.INVISIBLE);

        // 用户头像的列表控件
        mUserAvatarList = findViewById(R.id.anchor_rv_avatar);
        //mAvatarListAdapter = new UserAvatarListAdapter(this, UserMgr.getInstance().getUserId());
        //mUserAvatarList.setAdapter(mAvatarListAdapter);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //mUserAvatarList.setLayoutManager(linearLayoutManager);

        mBroadcastTime = findViewById(R.id.anchor_tv_broadcasting_time);
        mBroadcastTime.setText(String.format(Locale.US, "%s", "00:00:00"));
        mRecordBall = findViewById(R.id.anchor_iv_record_ball);

        mHeadIcon = findViewById(R.id.anchor_iv_head_icon);
        mLiveName = findViewById(R.id.anchor_tv_live_name);

        mTvNotice = findViewById(R.id.tv_notice);

        mHeat = findViewById(R.id.anchor_tv_member_counts);
        mHeat.setText("0 观看");

        mHeartLayout = findViewById(R.id.heart_layout);
        mLikeName = findViewById(R.id.tv_likeNum);

        mRestLayout = findViewById(R.id.rl_live_rest);
        // 主播休息标题
        ActivityTitle mRestTitle = findViewById(R.id.live_rest_title);

        //分享直播间
        mBtnShare.setOnClickListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.btn_share)) { return; }
            //显示分享界面
            mPopwindow = new SharePopwindowView(this, itemsOnClick);
            mPopwindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        });

        mRestTitle.setMoreListener(v -> finish());

        mChatMsgListAdapter = new ChatMsgListAdapter(this, mLvMessage, mArrayListChatEntity);
        mChatMsgListAdapter.setOnItemLongClickListener((view, entity, position) -> QPopuWindow.getInstance(LiveAssistantActivity.this).builder
                .bindView(view, position)
                .setPopupItemList(new String[]{"禁止发言"})
                .setPointers(rawX, rawY)
                .setOnPopuListItemClickListener(new QPopuWindow.OnPopuListItemClickListener() {
                    /**
                     * @param anchorView 为pop的绑定view
                     * @param anchorViewPosition  pop绑定view在ListView的position
                     * @param position  pop点击item的position 第一个位置索引为0
                     */
                    @Override
                    public void onPopuListItemClick(View anchorView, int anchorViewPosition, int position) {
                        if(entity.getUserId() == null) {
                            Toast.makeText(LiveAssistantActivity.this, "无效操作:"+entity.getSenderName(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        UserMgr.getInstance().setBanned(entity.getUserId(), UserMgr.getInstance().getRoomId(),new HTTPMgr.Callback() {
                            @Override
                            public void onSuccess(JSONObject data) {
                                Handler mainHandler = new Handler(Looper.getMainLooper());
                                mainHandler.post(() -> T.showShort(getApplicationContext(), "成功禁言用户:"+entity.getSenderName()));
                            }
                            @Override
                            public void onFailure(int code, String msg) {
                                Handler mainHandler = new Handler(Looper.getMainLooper());
                                mainHandler.post(() -> TipDialog.show(LiveAssistantActivity.this, msg, TipDialog.TYPE.WARNING));
                            }
                        });
                    }
                }).show());
        mLvMessage.setAdapter(mChatMsgListAdapter);
    }

    /**
     * 为弹出窗口实现监听类
     */
    private final View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPopwindow.dismiss();
            mPopwindow.backgroundAlpha(LiveAssistantActivity.this, 1f);
            switch (v.getId()) {
                case R.id.rl_wechat:
                    WaitDialog.show(LiveAssistantActivity.this, "");
                    new Thread(() -> {
                        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
                        miniProgramObj.webpageUrl = "http://www.qq.com"; //兼容低版本的网页链接
                        miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE; //正式版:0，测试版:1，体验版:2
                        miniProgramObj.userName = GlobalConfig.WX_INIT_ID; //小程序原始id
                        miniProgramObj.path = "live/pages/lives/lives?liveid=" + mLiveId; //小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
                        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
                        msg.title = shareTitle; // 小程序消息title
                        msg.description = ""; // 小程序消息desc
                        Bitmap bitmap = Utils.urlToBitmap(LiveAssistantActivity.this,shareImage);
                        Bitmap minBitmap = BitmapUtil.compressImageSize(bitmap);
                        msg.thumbData = Utils.bitmapToByte(minBitmap); // 小程序消息封面图片，小于128k
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "miniProgram";
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession; //目前只支持会话
                        Application.api.sendReq(req);
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            TipDialog.show(LiveAssistantActivity.this, "", TipDialog.TYPE.SUCCESS);
                        });
                    }).start();
                    break;
                case R.id.rl_save_photo:
                    WaitDialog.show(LiveAssistantActivity.this, "");
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
        LiveShareDialogFragment liveShareDialogFragment = new LiveShareDialogFragment();
        Bundle args = new Bundle();
        args.putString("img1", shareImage);
        args.putString("img2", shareHeadImg);
        args.putString("img3", shareWechatImg);
        args.putString("name", mLiveName.getText().toString());
        args.putString("info", shareTitle);
        liveShareDialogFragment.setArguments(args);
        liveShareDialogFragment.setCancelable(false);
        if (liveShareDialogFragment.isAdded()) { liveShareDialogFragment.dismiss(); }
        else { liveShareDialogFragment.show(getFragmentManager(), ""); }
        TipDialog.show(LiveAssistantActivity.this, "", TipDialog.TYPE.SUCCESS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogManager.getInstance().writeData("MainActivity_onStart()");
        mTXCloudVideoView.setVisibility(View.VISIBLE);
        pull();
    }

    private void initPuller() {
        TXLivePlayConfig mLivePlayConfig = new TXLivePlayConfig();
        //创建 player 对象
        mLivePlayer = new TXLivePlayer(this);

        //自动模式, 极速模式:true 1 1, 流畅模式:false 5 5
        mLivePlayConfig.setAutoAdjustCacheTime(true);
        mLivePlayConfig.setMinAutoAdjustCacheTime(1);
        mLivePlayConfig.setMaxAutoAdjustCacheTime(5);
        mLivePlayer.setConfig(mLivePlayConfig);

        //关键 player 对象与界面 view
        mLivePlayer.setPlayerView(mTXCloudVideoView);

        mTXLivePlayListener = new TXLivePlayListenerImpl();
        mLivePlayer.setPlayListener(mTXLivePlayListener);

    }

    private void pull() {
        if (mIsPreView) { return; }
        WaitDialog.show(LiveAssistantActivity.this, "正在连接直播间");
        mIsPreView = true;

        //初始化IM
        NIMClient.initSDK();

        UserMgr.getInstance().assistantPull(new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                final int code = data.optInt("code");
                final JSONObject result = data.optJSONObject("result");
                if (code == 0 && result != null) {
                    final JSONObject resultDate = result.optJSONObject("data");
                    String imAddr = null;
                    try {
                        imAddr = Objects.requireNonNull(resultDate.optJSONArray("im_addr")).getString(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String roomid = resultDate.optString("roomid");
                    final String accid = resultDate.optString("accid");
                    final String imToken = resultDate.optString("im_token");
                    final String liveAddr = resultDate.optString("live_addr");
                    final String imgUrl = resultDate.optString("img_url");
                    final String username = resultDate.optString("username");
                    final int liveId = resultDate.optInt("live_id");
                    final String notice = resultDate.optString("notice");
                    final String wechatImg = resultDate.optString("wechat_img");
                    final String title = resultDate.optString("title");
                    final String image = resultDate.optString("image");
                    final JSONArray goods = resultDate.optJSONArray("goods");

                    ArrayList list = new ArrayList();
                    int mLength = goods.optJSONObject(0).optJSONObject("shop").optJSONArray("new_goods").length();
                    for (int i = 0; i < mLength; i++) {
                        JSONObject goodsData = goods.optJSONObject(0).optJSONObject("shop").optJSONArray("new_goods").optJSONObject(i);
                        LiveGoodsBean liveGoodsBean = new LiveGoodsBean(
                                goodsData.optInt("id"),
                                goodsData.optString("goods_image"),
                                goodsData.optString("goods_name"),
                                goodsData.optString("goods_marketprice"), 0, true
                        );
                        list.add(liveGoodsBean);
                    }
                    UserMgr.getInstance().saveLiveGoodsInfoAssistant(list);
                    UserMgr.getInstance().setRoomId(roomid);
                    Log.e("LiveAssistantActivity", "getPullUrl: 拉流地址" + liveAddr);

                    //WebSocket的连接
                    if (client.isOpen()) {
                        return;
                    }
                    try {
                        client.connectBlocking();
                        if (client.isOpen()) {
                            client.send("{\"handshake\":\"true\",\"live_id\":" + "\"" + liveId + "\"" + "}");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //3.开始拉流
                    String finalImAddr = imAddr;
                    startPullStream(liveAddr, new StandardCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
                            WaitDialog.dismiss();
                            Log.e("LiveAssistantActivity", "onError: " + errInfo);
                        }

                        @Override
                        public void onSuccess() {
                            //推流过程中，可能会重复收到PUSH_EVT_PUSH_BEGIN事件，onSuccess可能会被回调多次，如果已经创建的房间，直接返回
                            if (pushCount > 0) {
                                return;
                            }
                            pushCount++;

                            UserMgr.getInstance().saveLiveGoodsInfoAssistant(list);

                            showHeadIcon(mHeadIcon, imgUrl);
                            mTvNotice.setText(notice);
                            mLiveName.setText(username);
                            shareImage = image;
                            shareHeadImg = imgUrl;
                            shareWechatImg = wechatImg;
                            shareTitle = title;
                            mLiveId = liveId;

                            mRestLayout.setVisibility(View.INVISIBLE);
                            TipDialog.show(LiveAssistantActivity.this, "开始观看直播", TipDialog.TYPE.SUCCESS).setTipTime(4000);

                            // roomId 为聊天室id
                            EnterChatRoomData data = new EnterChatRoomData(roomid);
                            data.setNick(UserMgr.getInstance().getmNickNameAssistant());
                            // 独立模式的非匿名登录，帐号和密码必填，以account和token为例
                            data.setIndependentMode((roomId, account) -> {
                                //return ChatRoomHttpClient.getInstance().fetchChatRoomAddress(roomId, "accid");
                                return Collections.singletonList(finalImAddr);
                            }, accid, imToken);

                            NIMClient.getService(ChatRoomService.class).enterChatRoomEx(data, 1).setCallback(new RequestCallback<EnterChatRoomResultData>() {
                                @Override
                                public void onSuccess(EnterChatRoomResultData result) {
                                    Log.e("LiveAssistantActivity", "聊天室imAddr[0]" + finalImAddr);
                                }

                                @Override
                                public void onFailed(int code) {
                                    Log.e("LiveAssistantActivity", "onFailed: " + code);
                                }

                                @Override
                                public void onException(Throwable exception) {

                                }
                            });

                            mAvatarListAdapter = new UserAvatarListAdapter(LiveAssistantActivity.this, accid);
                            mUserAvatarList.setAdapter(mAvatarListAdapter);
                        }
                    });
                } else if (code == 702) {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        mRestLayout.setVisibility(View.VISIBLE);
                        TipDialog.show(LiveAssistantActivity.this, "主播正在休息", TipDialog.TYPE.WARNING);
                    });
                }
            }

            @Override
            public void onFailure(int code, final String msg) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    Log.e("LiveAssistantActivity", "assistantPull: " +msg);
                    TipDialog.show(LiveAssistantActivity.this, msg+":"+code, TipDialog.TYPE.WARNING);
                    T.showShort(LiveAssistantActivity.this, msg+":"+code);
                    finish();
                });
            }
        });
    }

    //WebSocket接收热度和点赞数
    JWebSocketClient client = new JWebSocketClient(URI.create(GlobalConfig.WebSocketUrl)) {
        @Override
        public void onMessage(String message) {
            JsonObject returnData = new JsonParser().parse(message).getAsJsonObject();
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                switch (returnData.getAsJsonPrimitive("type").getAsString()) {
                    //观看人数
                    case "audience":
                        mAudienceCount = Long.parseLong(returnData.getAsJsonPrimitive("audienceCount").getAsString());
                        mHeat.setText(Utils.formatNum(String.valueOf(mAudienceCount), false) + " 观看");
                        break;
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
                    default:
                }
            });
            Log.e("JWebSocketClient", "onMessage()"+returnData);
        }
    };


    protected void startPullStream(final String url, final StandardCallback callback) {
        //在主线程开启拉流
        Handler handler = new Handler(getApplicationContext().getMainLooper());
        handler.post(() -> {
            if (mLivePlayer != null) {
                mTXLivePlayListener.setCallback(callback);
                int ret = mLivePlayer.startPlay(url, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
                if (ret == -5) {
                    String msg = "拉流失败[license 校验失败]";
                    showErrorAndQuit(ret, msg);
                    Log.e("LiveAssistantActivity", "startPullStream: " +msg );
                    if (callback != null) {
                        callback.onError(-5, msg);
                    }
                } else { callback.onSuccess(); }
            } else {
                String msg = "拉流失败[TXLivePusher未初始化，请确保已经调用startLocalPreview]";
                Log.e("LiveAssistantActivity", "startPullStream: " +msg );
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

    protected class TXLivePlayListenerImpl implements ITXLivePlayListener {
        private StandardCallback mCallback = null;

        public void setCallback(StandardCallback callback) {
            mCallback = callback;
        }

        @Override
        public void onPlayEvent(final int event, final Bundle bundle) {
            Log.e("LiveAssistantActivity", "onPlayEvent: " +event);
            if (event == TXLiveConstants.PLAY_EVT_RTMP_STREAM_BEGIN) {
                Log.e("LiveAssistantActivity", "拉流成功");
                callbackOnThread(mCallback, "onSuccess");
            } else if (event == TXLiveConstants.PLAY_WARNING_RECV_DATA_LAG) {
                String msg = "拉流失败[网络来包不稳：可能是下行带宽不足，或由于主播端出流不均匀]";
                Log.e("LiveAssistantActivity", "onPlayEvent: " +msg );
                T.showLong(getApplicationContext(), "网络不稳定\n检查网络环境");
                callbackOnThread(mCallback, "onSuccess");
            } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT || event == TXLiveConstants.PLAY_WARNING_RECONNECT) {
                String msg = "拉流失败[网络断连，且经多次重连亦不能恢复，请自行重启播放]";
                Log.e("LiveAssistantActivity", "onPlayEvent: " +msg );
                final AlertDialog dlg = new AlertDialog.Builder(LiveAssistantActivity.this).create();
                Window window = dlg.getWindow();
                //这一句消除白块
                window.setBackgroundDrawable(new BitmapDrawable());
                dlg.show();
                dlg.getWindow().setContentView(R.layout.dialog_live_newworkerr);
                assert window != null;
                TextView tv = window.findViewById(R.id.tv);
                String str = event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT ? "当前网络环境无法观看直播\n建议改善后再观看" : "本场直播已结束";
                tv.setText(str);
                TextView ok = window.findViewById(R.id.tv_ok);
                ok.setOnClickListener(v -> {
                    dlg.dismiss();
                    stopPublish();
                });
            } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
                String msg = "拉流失败[视频播放结束]";
                Log.e("LiveAssistantActivity", "onPlayEvent: " +msg );
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
        mListenerHandler.post(runnable);
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
                    if (method.getName().equals(methodName)) {
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

    private void stopPush() {
        mLivePlayer.stopPlay(true); // true 代表清除最后一帧画面
        mTXCloudVideoView.onDestroy();
        //registerObservers(false);
        //exitChatRoom(TCUserMgr.getInstance().getRoomId());
        try {
            if ( client!= null) {
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
     * //                      点击事件与调用函数相关
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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



    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      成员进退房事件信息处理
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    @Override
    protected void handleMemberJoinMsg(SimpleUserInfo userInfo) {
        //更新头像列表 返回false表明已存在相同用户，将不会更新数据
        if (mAvatarListAdapter.addItem(userInfo)) {
            super.handleMemberJoinMsg(userInfo);
        }
    }

    @Override
    protected void handleMemberQuitMsg(SimpleUserInfo userInfo) {
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
    protected void onResume() {
        super.onResume();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onResume();
        }
        if (mLivePlayer != null) {
            mLivePlayer.resume();
        }

    }

    @Override
    protected void onStop() {
        Log.d("meihu_beauty", "TxMainActivity--onStop");
        LogManager.getInstance().writeData("MainActivity_onStop()");
        super.onStop();

        //stopPush();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onPause();
        }
        if (mLivePlayer.isPlaying()) {
            mLivePlayer.pause();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d("meihu_beauty", "TxMainActivity--onDestroy");
        LogManager.getInstance().writeData("MainActivity_onDestroy()");
        stopPush();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
        }
        if (mLivePlayer != null) {
            mLivePlayer.stopPlay(true);
        }
        LogManager.getInstance().closeFile();
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
