package com.kol.jumhz.anchor;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.msg.ChatEntity;
import com.kol.jumhz.common.msg.ChatMsgListAdapter;
import com.kol.jumhz.common.msg.SimpleUserInfo;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.Constants;
import com.kol.jumhz.common.utils.T;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.common.widget.InputTextMsgDialog;
import com.kol.jumhz.common.widget.SwipeAnimationController;
import com.kol.jumhz.common.widget.like.HeartLayout;
import com.kol.jumhz.live.LiveActivity;
import com.kol.jumhz.live.SelectPicActivity;
import com.kol.jumhz.main.MainActivity;
import com.kol.jumhz.view.ErrorDialogFragment;
import com.kol.jumhz.view.FinishDetailDialogFragment;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @ClassName: BaseAnchorActivity
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:41
 * @Description: 主播推流的页面
 */
public abstract class BaseAnchorActivity extends BaseActivity implements View.OnClickListener, InputTextMsgDialog.OnTextSendListener {
    private static final String TAG = BaseAnchorActivity.class.getSimpleName();

    private RelativeLayout mRlGoods;
    private ImageView mGoods;
    private TextView mGoodsNum;
    private TextView mGoodsName;
    private TextView mGoodsPrice;
    private TextView mLiveName; //直播间名称
    private TextView mHeat; // 热度/观看人数

    protected ListView mLvMessage; // 消息控件
    private InputTextMsgDialog mInputTextMsgDialog; // 消息输入框
    protected ChatMsgListAdapter mChatMsgListAdapter; // 消息列表的Adapter
    protected ArrayList<ChatEntity> mArrayListChatEntity; // 消息内容

    private ErrorDialogFragment mErrDlgFragment; // 错误提示弹窗
    private HeartLayout mHeartLayout; // 点赞动画的布局

    protected SwipeAnimationController mSwipeAnimationController; // 动画控制类

    private String mTitle; // 直播标题
    private String mCoverPicUrl; // 直播封面图
    private String mAvatarPicUrl; // 个人头像地址
    private String mNickName; // 个人昵称
    private String mUserId; // 个人用户id
    protected long mTotalMemberCountTrue = -1; // 真实总进房观众数量
    protected long mHeatCount = 0; // 热度
    protected long mHeartCount = 0;  // 点赞数量
    protected long mAudienceCount = 0;  // 观众数量
    protected long mCurrentMemberCount = -1;// 当前观众数量

    protected Handler mMainHandler = new Handler(Looper.getMainLooper());


    // 定时的 Timer 去更新开播时间
    private Timer mBroadcastTimer; // 定时的 Timer
    private BroadcastTimerTask mBroadcastTimerTask; // 定时任务
    protected long mSecond = 0; //开播的时间，单位为秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getIntent();
        mUserId = intent.getStringExtra(Constants.USER_ID);
        mTitle = intent.getStringExtra(Constants.ROOM_TITLE);
        mCoverPicUrl = intent.getStringExtra(Constants.COVER_PIC);
        mAvatarPicUrl = intent.getStringExtra(Constants.USER_HEADPIC);
        mNickName = intent.getStringExtra(Constants.USER_NICK);

        mArrayListChatEntity = new ArrayList<>();
        mErrDlgFragment = new ErrorDialogFragment();

        initView();
        if (TextUtils.isEmpty(mNickName)) {
            mNickName = mUserId;
        }
        mLiveName.setText(mTitle);
        startPublish();
    }

    /**
     * 特别注意，以下几个 findViewById 由于是依赖于子类
     * {@link LiveActivity}
     * 的布局，所以id要保持一致。 若id发生改变，此处id也要同时修改
     */
    protected void initView() {
        RelativeLayout relativeLayout = findViewById(R.id.rl_noStart);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mSwipeAnimationController.processEvent(event);
            }
        });

        mRlGoods = findViewById(R.id.rl_goods);
        mRlGoods.setVisibility(View.INVISIBLE);
        mGoods = findViewById(R.id.iv_goods);
        mGoodsNum = findViewById(R.id.tv_goods_num);
        mGoodsName = findViewById(R.id.tv_goods_name);
        mGoodsPrice = findViewById(R.id.tv_goods_price);

        //选择展示商品按钮
        ImageView mSelectGoods = findViewById(R.id.iv_select_goods);
        mSelectGoods.setOnClickListener(this);

        // 消息列表相关
        RelativeLayout mrlMessageInput = findViewById(R.id.rl_message_input);
        //mbtnMessageInput =(Button) findViewById(R.id.btn_message_input);
        mrlMessageInput.setOnClickListener(this);

        mLiveName = findViewById(R.id.anchor_tv_live_name);
        mHeat = findViewById(R.id.anchor_tv_member_counts);

        RelativeLayout controllLayer = findViewById(R.id.anchor_rl_controllLayer);
        mSwipeAnimationController = new SwipeAnimationController(this);
        mSwipeAnimationController.setAnimationView(controllLayer);

        mLvMessage = findViewById(R.id.im_msg_listview);
        mHeartLayout = findViewById(R.id.heart_layout);

        mInputTextMsgDialog = new InputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);

        mChatMsgListAdapter = new ChatMsgListAdapter(this, mLvMessage, mArrayListChatEntity);
        /*mChatMsgListAdapter.setOnItemLongClickListener(new TCChatMsgListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, TCChatEntity entity, int position) {
                QPopuWindow.getInstance(TCBaseAnchorActivity.this).builder
                        .bindView(view, position)
                        .setPopupItemList(new String[]{"禁言发言", "抽奖"})
                        .setPointers(rawX, rawY)
                        .setOnPopuListItemClickListener(new QPopuWindow.OnPopuListItemClickListener() {
                            *//**
                             * @param anchorView 为pop的绑定view
                             * @param anchorViewPosition  pop绑定view在ListView的position
                             * @param position  pop点击item的position 第一个位置索引为0
                             *//*
                            @Override
                            public void onPopuListItemClick(View anchorView, int anchorViewPosition, int position) {

                                Toast.makeText(TCBaseAnchorActivity.this, entity.getUserId()+"禁言", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });*/
        mLvMessage.setAdapter(mChatMsgListAdapter);
    }

    protected int rawX;
    protected int rawY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        rawX= (int) ev.getRawX();
        rawY= (int) ev.getRawY();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_setting:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("nowLive","true");
                startActivity(intent);
                break;
            case R.id.rl_message_input:
                showInputMsgDialog();
                break;
            case R.id.iv_select_goods:
                Intent intent1 = new Intent(this, SelectPicActivity.class);
                intent1.putExtra("num",mGoodsNum.getText().toString());
                startActivity(intent1);
                break;
            default:
        }
    }


    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      Activity声明周期相关
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */

    @Override
    public void onBackPressed() {
        //showExitInfoDialog("当前正在直播，是否退出直播？", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoods != null) {
            SharedPreferences editor = getSharedPreferences("liveGoodsInfoPop", Context.MODE_PRIVATE);
            if (editor.contains("liveGoodsInfoUrl")) {
                String liveGoodsInfoUrl = editor.getString("liveGoodsInfoUrl", null);
                mRlGoods.setVisibility(View.VISIBLE);
                String liveGoodsInfoNum = editor.getString("liveGoodsInfoNum",null);
                String liveGoodsInfoName = editor.getString("liveGoodsInfoName", null);
                String liveGoodsInfoPrice = editor.getString("liveGoodsInfoPrice", null);
                Glide.with(getApplicationContext()).load(liveGoodsInfoUrl).into(mGoods);
                mGoodsNum.setText(liveGoodsInfoNum);
                mGoodsName.setText(liveGoodsInfoName);
                mGoodsPrice.setText(liveGoodsInfoPrice);
            } else {
                mGoodsNum.setText("0");
                mGoodsName.setText("");
                mGoodsPrice.setText("");
                mRlGoods.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        //stopPublish();
        SharedPreferences settings = getSharedPreferences("liveGoodsInfoPop", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }



    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      开始和停止推流相关
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */
    protected void startPublish() {
        startPublishView();
        startTimer();
    }


    protected void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
        Log.e("BaseAnchorActivity", "registerObservers: " +register);
    }
    Observer<List<ChatRoomMessage>> incomingChatRoomMsg = (Observer<List<ChatRoomMessage>>) messages -> {
        if (messages == null || messages.size() == 0) {
            return;
        }
        for (ChatRoomMessage message : messages) {
            List<String> accountId = new ArrayList<>();
            Log.e("BaseAnchorActivity", "onResult:accountId " +message.getFromAccount());
            accountId.add(message.getFromAccount());
            NIMClient.getService(ChatRoomService.class).fetchRoomMembersByIds(UserMgr.getInstance().getRoomId(), accountId).setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
                @Override
                public void onResult(int code, List<ChatRoomMember> result, Throwable exception) {
                    if(result == null || result.size() == 0) {
                        if (mCurrentMemberCount > 0) { mCurrentMemberCount--; }
                        mHeat.setText(Utils.formatNum(String.valueOf(mCurrentMemberCount), false) + " 观看");
                        Log.e("BaseAnchorActivity", "onResult: " +result);
                        return;
                    }
                    SimpleUserInfo userInfo = new SimpleUserInfo(message.getFromAccount(), result.get(0).getNick(), message.getUuid());
                    //文本消息
                    if (message.getMsgType() == MsgTypeEnum.text) {
                        Log.e("BaseAnchorActivity", "onResult:昵称:" +userInfo.nickname+" 发送方:" +userInfo.userid+" 内容:" +message.getContent());
                        handleTextMsg(userInfo, message.getContent());
                    }
                    //通知消息
                    else if (message.getMsgType() == MsgTypeEnum.notification) {
                        NotificationAttachment notificationManager = (NotificationAttachment)message.getAttachment();
                        Log.e("BaseAnchorActivity", "onResult:昵称:" +userInfo.nickname+" 发送方:" +userInfo.userid+" 内容:" +message.getContent());
                        if (notificationManager.getType() == NotificationType.ChatRoomMemberIn) {
                            handleMemberJoinMsg(userInfo); //成员进入聊天室
                        } else if (notificationManager.getType() == NotificationType.ChatRoomMemberExit) {
                            //handleMemberQuitMsg(userInfo); //成员离开聊天室
                        }
                    }
                }
            });
        }
    };


    /**
     * 创建直播间成功
     */
    protected void onCreateRoomSuccess() {
        startTimer();
    }

    /**
     * 停止直播
     */
    protected void stopPublish() {
        WaitDialog.show(BaseAnchorActivity.this,"");
        UserMgr.getInstance().fetchViewsNumber(new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                WaitDialog.dismiss();
                showPublishFinishDetailsDialog(Long.parseLong(data.optJSONObject("result").optJSONObject("data").optString("audience")));
                registerObservers(false);
                exitChatRoom(UserMgr.getInstance().getRoomId());
            }
            @Override
            public void onFailure(int code, final String msg) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> TipDialog.show(BaseAnchorActivity.this,msg+":"+code, TipDialog.TYPE.WARNING)); }
        });
    }

    protected void startPublishView() {
        NIMClient.initSDK();
        registerObservers(true);
        //relativeLayout.setEnabled(true);
        //mStart.setVisibility(View.GONE);
    }

    /**
     * 退出聊天室
     */
    protected void exitChatRoom(String roomId){
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
    }

    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      处理接收到的各种信息
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */
    protected void handleTextMsg(SimpleUserInfo userInfo, String text) {
        ChatEntity entity = new ChatEntity();
        entity.setUserId(userInfo.userid);
        entity.setSenderName(userInfo.nickname);
        entity.setContent(text);
        entity.setType(Constants.TEXT_TYPE);
        notifyMsg(entity);
    }

    /**
     * 处理观众加入信息
     *
     * @param userInfo
     */
    protected void handleMemberJoinMsg(SimpleUserInfo userInfo) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName("欢迎");
        if (TextUtils.isEmpty(userInfo.nickname)) { entity.setContent(userInfo.userid + "进入直播间"); }
        else { entity.setContent(userInfo.nickname + "进入直播间"); }
        entity.setType(Constants.MEMBER_ENTER);
        notifyMsg(entity);
    }

    /**
     * 处理观众退出信息
     *
     * @param userInfo
     */
    protected void handleMemberQuitMsg(SimpleUserInfo userInfo) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName("通知");
        if (TextUtils.isEmpty(userInfo.nickname)) { entity.setContent(userInfo.userid + "退出直播间"); }
        else { entity.setContent(userInfo.nickname + "退出直播间"); }
        entity.setType(Constants.MEMBER_EXIT);
        notifyMsg(entity);
    }

    /**
     * 处理点赞信息
     *
     * @param userInfo
     */
    protected void handlePraiseMsg(SimpleUserInfo userInfo) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName("通知");
        if (TextUtils.isEmpty(userInfo.nickname)) { entity.setContent(userInfo.userid + "点了个赞"); }
        else { entity.setContent(userInfo.nickname + "点了个赞"); }

        mHeartLayout.addFavor();
        mHeartCount++;

        //todo：修改显示类型
        entity.setType(Constants.PRAISE);
        notifyMsg(entity);
    }

    /**
     * 处理弹幕信息
     *
     * @param userInfo
     * @param text
     */
    protected void handleDanmuMsg(SimpleUserInfo userInfo, String text) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName(userInfo.nickname);
        entity.setContent(text);
        entity.setType(Constants.TEXT_TYPE);
        notifyMsg(entity);
    }

    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      发送文本信息
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */
    /**
     * 发消息弹出框
     */
    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }


    @Override
    public void onTextSend(String msg, boolean danmuOpen) {
        if (msg.length() == 0) { return; }
        byte[] byte_num = msg.getBytes(StandardCharsets.UTF_8);
        if (byte_num.length > 160) {
            T.showShort(this, "请输入内容");
            return;
        }

        // 创建聊天室文本消息
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(UserMgr.getInstance().getRoomId(), msg);
        // 将文本消息发送出去
        NIMClient.getService(ChatRoomService.class).sendMessage(message, true).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                // 成功
                ChatEntity entity = new ChatEntity();
                entity.setSenderName("我:");
                entity.setContent(msg);
                entity.setType(Constants.TEXT_TYPE);
                notifyMsg(entity);
                Log.e("InputTextMsgDialog", "onSuccess: 本消息发送成功" +param);
            }
            @Override
            public void onFailed(int code) {
                Log.e("InputTextMsgDialog", "onSuccess: 本消息发送失败" +code);
            }
            @Override
            public void onException(Throwable exception) {
                Log.e("InputTextMsgDialog", "onSuccess: 本消息发送错误" +exception);
            }
        });
    }


    private void notifyMsg(final ChatEntity entity) {
        runOnUiThread(() -> {
            if (mArrayListChatEntity.size() > 1000) {
                while (mArrayListChatEntity.size() > 900) {
                    mArrayListChatEntity.remove(0);
                }
            }
            mArrayListChatEntity.add(entity);
            mChatMsgListAdapter.notifyDataSetChanged();
        });
    }

    /**
     * 显示直播结果的弹窗
     *
     * 如：观看数量、本场热度、点赞数量、直播时长数
     */
    protected void showPublishFinishDetailsDialog(long number) {
        FinishDetailDialogFragment dialogFragment = new FinishDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString("time", Utils.formattedTime(mSecond));
        args.putString("totalMemberCount", String.format(Locale.CHINA, "%d", number));
        args.putString("heatCount", String.format(Locale.CHINA, "%d", mHeatCount));
        args.putString("heartCount", String.format(Locale.CHINA, "%d", mHeartCount));
        dialogFragment.setArguments(args);
        dialogFragment.setCancelable(false);
        if (dialogFragment.isAdded()) { dialogFragment.dismiss(); }
        else { dialogFragment.show(getFragmentManager(), ""); }
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            T.showLong(getApplicationContext(), "精彩回放可查看录制");
        });
    }

    /**
     * 显示错误并且退出直播的弹窗
     *
     * @param errorCode
     * @param errorMsg
     */
    protected void showErrorAndQuit(int errorCode, String errorMsg) {
        stopTimer();
        stopPublish();
        if (!mErrDlgFragment.isAdded() && !this.isFinishing()) {
            Bundle args = new Bundle();
            args.putInt("errorCode", errorCode);
            args.putString("errorMsg", errorMsg);
            mErrDlgFragment.setArguments(args);
            mErrDlgFragment.setCancelable(false);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(mErrDlgFragment, "loading");
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      开播时长相关
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */
    protected void onBroadcasterTimeUpdate(long second) {

    }

    /**
     * 记时器
     */
    private class BroadcastTimerTask extends TimerTask {
        @Override
        public void run() {
            //Log.i(TAG, "timeTask ");
            ++mSecond;
            runOnUiThread(() -> onBroadcasterTimeUpdate(mSecond));
        }
    }

    private void startTimer() {
        //直播时间
        if (mBroadcastTimer == null) {
            mBroadcastTimer = new Timer(true);
            mBroadcastTimerTask = new BroadcastTimerTask();
            mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
        }
    }

    private void stopTimer() {
        //直播时间
        if (null != mBroadcastTimer) {
            mBroadcastTimerTask.cancel();
        }
    }

}
