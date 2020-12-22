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
import com.kol.jumhz.assistant.AssistanAnnunciateActivity;
import com.kol.jumhz.assistant.AssistantBannedActivity;
import com.kol.jumhz.assistant.AssistantLotteryActivity;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.utils.Constants;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.live.LiveAssistantActivity;
import com.kol.jumhz.login.LoginAssistantActivity;
import com.kol.jumhz.profile.EditUseInfoAssistantActivity;
import com.kol.jumhz.view.LineControllerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MainAssistantActivity
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:44
 * @Description: 助理端主界面
 */
public class MainAssistantActivity extends BaseActivity implements View.OnClickListener  {
    private ImageView mHead;
    private TextView mUserId;
    private ActivityTitle mTitile;
    private TextView tvXieyi;
    private TextView tvYinsi;
    private boolean mPermission = false;               // 是否已经授权
    private String isPlayLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_assistant);

        Intent intent = getIntent();
        isPlayLive = intent.getStringExtra("playLive");

        initUi();
        initData();
        initEvent();
        mPermission = checkPublishPermission();
        //检查更新
        Utils.upDate(this);
    }

    private void initUi() {
        mTitile = findViewById(R.id.rl_user_info_title);
        mHead =findViewById(R.id.iv_ui_head);
        mUserId = findViewById(R.id.tv_ui_user_id);
        tvXieyi = findViewById(R.id.tv_xieyi);
        tvYinsi = findViewById(R.id.tv_yinsi);
        LineControllerView mLottery = findViewById(R.id.lcv_ui_lottery);
        LineControllerView mBanned = findViewById(R.id.lcv_ui_banned);
        LineControllerView mAnnunciate = findViewById(R.id.lcv_ui_annunciate);
        TextView mVersion = findViewById(R.id.tv_version);
        RelativeLayout mBtnLogout = findViewById(R.id.lcv_ui_logout);

        mHead.setOnClickListener(this);
        mLottery.setOnClickListener(this);
        mBanned.setOnClickListener(this);
        mAnnunciate.setOnClickListener(this);
        mBtnLogout.setOnClickListener(this);
        mUserId.setText(HTTPMgr.getInstance().getUserId());

        try {
            PackageInfo info=getPackageManager().getPackageInfo(getPackageName(),0);
            mVersion.setText(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(); }
    }

    private void initData() {
        //页面展示之前，更新一下用户信息
        UserMgr.getInstance().fetchUserInfoAssistant(new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                MainAssistantActivity.this.runOnUiThread(() -> {
                    mUserId.setText(UserMgr.getInstance().getmNickNameAssistant());
                    Utils.showPicWithUrl(MainAssistantActivity.this, mHead, UserMgr.getInstance().getmUserAvatarAssistant(), R.drawable.ic_camera_download_bg);
                });
            }
            @Override
            public void onFailure(int code, final String msg) {
            }
        });
    }

    private void initEvent() {
        mTitile.setReturnListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.rl_user_info_title)) { return; }
            if("true".equals(isPlayLive)) {
                finish();
            } else {
                startActivity(new Intent(getApplicationContext(), LiveAssistantActivity.class));
            }
        });

        tvXieyi.setOnClickListener(v -> {
            String str = initAssets("yhxy.txt");
            final View inflate = LayoutInflater.from(MainAssistantActivity.this).inflate(R.layout.dialog_xieyi_yinsi_style, null);
            TextView tvTitle =  inflate.findViewById(R.id.tv_title);
            TextView tvContent = inflate.findViewById(R.id.tv_content);
            TextView tvCancle = inflate.findViewById(R.id.tv_cancle);
            tvTitle.setText("《聚美集用户协议与交易规则》");
            tvContent.setText(str);
            final Dialog dialog = new android.support.v7.app.AlertDialog.Builder(MainAssistantActivity.this)
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
            final View inflate = LayoutInflater.from(MainAssistantActivity.this).inflate(R.layout.dialog_xieyi_yinsi_style, null);
            TextView tvTitle = inflate.findViewById(R.id.tv_title);
            TextView tvContent = inflate.findViewById(R.id.tv_content);
            TextView tvCancle = inflate.findViewById(R.id.tv_cancle);
            tvTitle.setText("《聚美集隐私政策》");
            tvContent.setText(str);
            final Dialog dialog = new android.support.v7.app.AlertDialog.Builder(MainAssistantActivity.this)
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_ui_head: //设置助理用户信息
                //短时间多次点击
                if (ButtonUtils.isFastDoubleClick(R.id.iv_ui_head)) { return; }
                startActivity(new Intent(this, EditUseInfoAssistantActivity.class));
                break;
            case R.id.lcv_ui_lottery: //发起抽奖
                if (ButtonUtils.isFastDoubleClick(R.id.lcv_ui_lottery)) { return; }
                startActivity(new Intent(this, AssistantLotteryActivity.class));
                break;
            case R.id.lcv_ui_banned: //禁言列表
                if (ButtonUtils.isFastDoubleClick(R.id.lcv_ui_banned)) { return; }
                startActivity(new Intent(this, AssistantBannedActivity.class));
                break;
            case R.id.lcv_ui_annunciate: //直播间公告
                if (ButtonUtils.isFastDoubleClick(R.id.lcv_ui_annunciate)) { return; }
                startActivity(new Intent(this, AssistanAnnunciateActivity.class));
                break;
            case R.id.lcv_ui_logout: //退出登录
                if (ButtonUtils.isFastDoubleClick(R.id.lcv_ui_logout)) { return; }
                dialog("你确定要退出登录吗?");
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
                loginout(UserMgr.getInstance().getmAccessTokenAssistant());
            } else {
                //销毁任务栈
                ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                assert activityManager != null;
                List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
                for (ActivityManager.AppTask appTask : appTaskList) { appTask.finishAndRemoveTask(); }
            }
        });
        //取消按钮
        on.setOnClickListener(v -> dlg.cancel()); }

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


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //new Thread(runnable).start();  //启动子线程
        //if (TextUtils.isEmpty(UserMgr.getInstance().getmAccessTokenAssistant())) {
        //    if (Utils.isNetworkAvailable(this) && UserMgr.getInstance().hasUserAssistant()) {
        //        UserMgr.getInstance().autoLoginAssistant(null);
        //    }
        //}
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mUserId.setText(UserMgr.getInstance().getmNickNameAssistant());
        Utils.showPicWithUrl(MainAssistantActivity.this, mHead, UserMgr.getInstance().getmUserAvatarAssistant(), R.drawable.ic_camera_download_bg);
    }

    private void loginout(String token) {
        final UserMgr tcLoginoutMgr = UserMgr.getInstance();
        tcLoginoutMgr.loginoutAssistant(token, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                Intent intent = new Intent(getApplicationContext(), LoginAssistantActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(int code, final String msg) {
                Intent intent = new Intent(getApplicationContext(), LoginAssistantActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
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
