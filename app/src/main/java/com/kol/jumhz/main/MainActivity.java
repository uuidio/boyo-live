package com.kol.jumhz.main;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.assistant.AssistantActivity;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.utils.Constants;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.livegoods.RelevanceLiveGoodsActivity;
import com.kol.jumhz.login.LoginActivity;
import com.kol.jumhz.playback.PlaybackActivity;
import com.kol.jumhz.prepare.AnchorPrepareActivity;
import com.kol.jumhz.profile.EditUseInfoActivity;
import com.kol.jumhz.publish.LiveForeshowActivity;
import com.kol.jumhz.view.LineControllerView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: TCMainActivity
 * @Author: Dzy
 * @CreateDate: 2020-04-29 09:48
 * @Description: 主播主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener  {
    private ImageView mHead;
    private TextView mUserId;
    private ActivityTitle mTitle;
    private TextView tvXieyi;
    private TextView tvYinsi;
    private boolean mPermission = false;               // 是否已经授权
    private String isPlayLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        initData();
        initEvent();

        isPlayLive = getIntent().getStringExtra("nowLive");
        if(!"true".equals(isPlayLive)) {
            mPermission = checkPublishPermission();
            //检查更新
           // Utils.upDate(this);
        }
    }


    private void initUi() {
        mTitle = findViewById(R.id.rl_user_info_title);
        mHead = findViewById(R.id.iv_ui_head);
        mUserId = findViewById(R.id.tv_ui_user_id);
        tvXieyi = findViewById(R.id.tv_xieyi);
        tvYinsi = findViewById(R.id.tv_yinsi);
        TextView tvVersion = findViewById(R.id.tv_version);
        LineControllerView mPublishLive = findViewById(R.id.lcv_ui_publish_live);
        LineControllerView mRelevanceLive = findViewById(R.id.lcv_ui_relevance_live);
        LineControllerView mCheckAssistant = findViewById(R.id.lcv_ui_check_aid);
        LineControllerView mPlayback = findViewById(R.id.lcv_ui_playback);
        RelativeLayout mBtnClose = findViewById(R.id.lcv_ui_close);
        RelativeLayout mBtnLogout = findViewById(R.id.lcv_ui_logout);

        try {
            PackageInfo info=getPackageManager().getPackageInfo(getPackageName(),0);
            tvVersion.setText("版本号 "+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(); }

        mHead.setOnClickListener(this);
        mUserId.setText(UserMgr.getInstance().getNickname());
        mPublishLive.setOnClickListener(this);
        mRelevanceLive.setOnClickListener(this);
        mCheckAssistant.setOnClickListener(this);
        mPlayback.setOnClickListener(this);
        mBtnClose.setOnClickListener(this);
        mBtnLogout.setOnClickListener(this);
    }

    private void initEvent() {
        mTitle.setReturnListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.rl_user_info_title)) { return; }
            if("true".equals(isPlayLive)) {
                finish();
            } else { startActivity(new Intent(MainActivity.this, AnchorPrepareActivity.class)); }
        });

        tvXieyi.setOnClickListener(v -> {
            String str = initAssets("yhxy.txt");
            final View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_xieyi_yinsi_style, null);
            TextView tvTitle =  inflate.findViewById(R.id.tv_title);
            TextView tvContent = inflate.findViewById(R.id.tv_content);
            TextView tvCancle = inflate.findViewById(R.id.tv_cancle);
            tvTitle.setText("《聚美集用户协议与交易规则》");
            tvContent.setText(str);
            final Dialog dialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                    .setView(inflate)
                    .show();
            DisplayMetrics dm = new DisplayMetrics();
            //获取屏幕宽高
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (dm.widthPixels-dm.widthPixels*0.1f);
            params.height = (int) (dm.heightPixels-dm.heightPixels*0.15f);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            tvCancle.setOnClickListener(v12 -> dialog.dismiss());
        });

        tvYinsi.setOnClickListener(v -> {
            String str = initAssets("yszc.txt");
            final View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_xieyi_yinsi_style, null);
            TextView tvTitle = inflate.findViewById(R.id.tv_title);
            TextView tvContent = inflate.findViewById(R.id.tv_content);
            TextView tvCancle = inflate.findViewById(R.id.tv_cancle);
            tvTitle.setText("《聚美集隐私政策》");
            tvContent.setText(str);
            final Dialog dialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                    .setView(inflate).show();
            DisplayMetrics dm = new DisplayMetrics();
            //获取屏幕宽高
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (dm.widthPixels-dm.widthPixels*0.1f);
            params.height = (int) (dm.heightPixels-dm.heightPixels*0.15f);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            tvCancle.setOnClickListener(v1 -> dialog.dismiss());
        });
    }

    private void initData() {
        //页面展示之前，更新一下用户信息
        UserMgr.getInstance().fetchUserInfo(new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                MainActivity.this.runOnUiThread(() -> {
                    mUserId.setText(UserMgr.getInstance().getNickname());
                    Utils.showPicWithUrl(MainActivity.this, mHead, UserMgr.getInstance().getAvatar(), R.drawable.ic_camera_download_bg);
                });
            }
            @Override
            public void onFailure(int code, final String msg) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //设置用户信息
            case R.id.iv_ui_head:
                ////短时间多次点击
                if (ButtonUtils.isFastDoubleClick(R.id.iv_ui_head)) { return; }
                startActivity(new Intent(this, EditUseInfoActivity.class));
                break;
            //发布直播预告
            case R.id.lcv_ui_publish_live:
                if (ButtonUtils.isFastDoubleClick(R.id.lcv_ui_publish_live)) { return; }
                startActivity(new Intent(this, LiveForeshowActivity.class));
                break;
            //关联直播商品
            case R.id.lcv_ui_relevance_live:
                if (ButtonUtils.isFastDoubleClick(R.id.lcv_ui_relevance_live)) { return; }
                startActivity(new Intent(this, RelevanceLiveGoodsActivity.class));
                break;
            //查看助理
            case R.id.lcv_ui_check_aid:
                if (ButtonUtils.isFastDoubleClick(R.id.lcv_ui_check_aid)) { return; }
                startActivity(new Intent(this, AssistantActivity.class));
                break;
            //精彩回放
            case R.id.lcv_ui_playback:
                if (ButtonUtils.isFastDoubleClick(R.id.lcv_ui_playback)) { return; }
                startActivity(new Intent(this, PlaybackActivity.class));
                break;
            //强制关闭直播
            case R.id.lcv_ui_close:
                if (ButtonUtils.isFastDoubleClick(R.id.lcv_ui_close)) { return; }
                dialog("你确定要强制关闭直播吗?\n\n(该功能用与无法正常开启直播)");
                break;
            //退出登录
            case R.id.lcv_ui_logout:
                if (ButtonUtils.isFastDoubleClick(R.id.lcv_ui_logout)) { return; }
                dialog("你确定要退出登录吗?");
                break;
            default:
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dialog("确定要退出吗?");
            return true;
        }
        return true;
    }

    /**
     * 弹出对话框方法
     * @param text
     */
    private void dialog(String text) {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        Window window = dlg.getWindow();
        //这一句消除白块
        window.setBackgroundDrawable(new BitmapDrawable());
        dlg.show();
        dlg.getWindow().setContentView(R.layout.dialog_tips);
        assert window != null;
        TextView tv = window.findViewById(R.id.tv);
        tv.setText(text);
        TextView on = window.findViewById(R.id.tv_no);
        TextView ok = window.findViewById(R.id.tv_ok);
        //确定按钮
        ok.setOnClickListener(v -> {
            dlg.dismiss();
            if (text.contains("登录")) {
                loginout(UserMgr.getInstance().getAccessToken());
            } else if (text.contains("强制")) {
                closedLive();
            } else {
                //销毁任务栈
                ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                assert activityManager != null;
                List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
                for (ActivityManager.AppTask appTask : appTaskList) { appTask.finishAndRemoveTask(); }
            }
        });
        //取消按钮
        on.setOnClickListener(v -> dlg.cancel());
    }

    /**
     * 从assets下的txt文件中读取数据
     */
    public String initAssets(String fileName) {
        String str = null;
        InputStreamReader inputStreamReader;
        try {
            InputStream inputStream = getAssets().open(fileName);
            try {
                inputStreamReader = new InputStreamReader(inputStream, "gbk");
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    str = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return str;
    }


    /**
     * handler 处理返回的请求结果
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            ArrayList<LiveGoodsBean> list = new ArrayList<>();
            list = (ArrayList<LiveGoodsBean>) data.getSerializable("resultGoods");
            // TODO: 更新界面
            UserMgr.getInstance().saveLiveGoodsInfo(list);
        }
    };

    /**
     * 新线程进行网络请求
     */
    final Runnable runnable = () -> UserMgr.getInstance().fetchGoodsList(1, new HTTPMgr.Callback() {
        @Override
        public void onSuccess(JSONObject data) {
            Message msg = new Message();
            Bundle data1 = new Bundle();
            ArrayList list = new ArrayList();
            for (int i = 0; i < data.optJSONObject("result").optJSONArray("lists").length(); i++) {
                JSONObject result = data.optJSONObject("result").optJSONArray("lists").optJSONObject(i);
                if (!result.optBoolean("live_check")) { continue; }
                LiveGoodsBean liveGoodsBean = new LiveGoodsBean(
                        result.optInt("id"),
                        result.optString("goods_image"),
                        result.optString("goods_name"),
                        result.optString("goods_price"),
                        result.optInt("shop_id"),
                        result.optBoolean("live_check")
                );
                list.add(liveGoodsBean);
            }
            data1.putSerializable("resultGoods", (ArrayList<String>) list);
            msg.setData(data1);
            handler.sendMessage(msg);
        }
        @Override
        public void onFailure(int code, final String msg) {
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        //启动子线程
        new Thread(runnable).start();
        // 如果您的 App 没有做判断是否账号重复的登录的逻辑
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mUserId.setText(UserMgr.getInstance().getNickname());
        Utils.showPicWithUrl(MainActivity.this, mHead, UserMgr.getInstance().getAvatar(), R.drawable.ic_camera_download_bg);
    }

    private void loginout(String token) {
        UserMgr.getInstance().loginout(token, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(int code, final String msg) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void closedLive() {
        WaitDialog.show(this, "");
        UserMgr.getInstance().closedLive(new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> TipDialog.show(MainActivity.this,"强制关闭成功", TipDialog.TYPE.SUCCESS));
            }
            @Override
            public void onFailure(int code, final String msg) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> TipDialog.show(MainActivity.this,"强制关闭失败", TipDialog.TYPE.WARNING));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(runnable);
    }

    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      动态权限检查相关
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */

    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[0]),
                        Constants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }
}
