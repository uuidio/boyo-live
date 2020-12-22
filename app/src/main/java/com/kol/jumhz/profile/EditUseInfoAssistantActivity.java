package com.kol.jumhz.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.utils.Constants;
import com.kol.jumhz.common.utils.GlideCircleTransform;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName: EditUseInfoAssistantActivity
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:05
 * @Description: 用户资料编辑页面助理端
 */
public class EditUseInfoAssistantActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = getClass().getName();
    private IHandlerCallBack iHandlerCallBack;
    private List<String> path = new ArrayList<>();
    private ImageView                   mIvAvatar;              // 头像控件类
    private EditText                    mEditView;              // 昵称
    private boolean                     mPermission;            // 权限检测

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info_assistant);
        initView();
        initGallery();

        mPermission = checkCropPermission();
    }

    private void initView() {
        // Activity 的标题控件
        ActivityTitle mTvTitle = findViewById(R.id.at_eui_edit);
        mIvAvatar = findViewById(R.id.iv_eui_head);
        mEditView = findViewById(R.id.et_eui_nickname);
        mEditView.setText(UserMgr.getInstance().getmNickNameAssistant());
        Utils.showPicWithUrl(this, mIvAvatar, UserMgr.getInstance().getmUserAvatarAssistant(), R.drawable.ic_camera_download_bg);
        path.clear();
        path.add(0, UserMgr.getInstance().getmUserAvatarAssistant());
        mTvTitle.setReturnListener(v -> {
            if ("".equals(path.get(0))) {
                TipDialog.show(EditUseInfoAssistantActivity.this, "请设置头像", TipDialog.TYPE.WARNING);
                return;
            }
            finish();
        });
        mTvTitle.setMoreListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.at_eui_edit)) { return; }
            WaitDialog.show(EditUseInfoAssistantActivity.this, "");
            if (!path.get(0).contains("http") && !"".equals(path.get(0))) {
                UserMgr.getInstance().uploadImage(path.get(0), new HTTPMgr.Callback() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        UserMgr.getInstance().uploadUserInfoAssistant(Objects.requireNonNull(data.optJSONObject("result")).optString("pic_url"),
                                mEditView.getText().toString(), new HTTPMgr.Callback() {
                                    @Override
                                    public void onSuccess(JSONObject data) {
                                        WaitDialog.dismiss();
                                        finish(); }
                                    @Override
                                    public void onFailure(int code, String msg) {
                                        Handler mainHandler = new Handler(Looper.getMainLooper());
                                        mainHandler.post(() -> TipDialog.show(EditUseInfoAssistantActivity.this, msg+":"+code, TipDialog.TYPE.ERROR)); }
                                });
                    }
                    @Override
                    public void onFailure(int code, String msg) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> TipDialog.show(EditUseInfoAssistantActivity.this, msg, TipDialog.TYPE.ERROR)); }
                });
            }
            else {
                if ("".equals(path.get(0))) {
                    TipDialog.show(EditUseInfoAssistantActivity.this, "请选择头像", TipDialog.TYPE.WARNING);
                    return;
                }
                UserMgr.getInstance().uploadUserInfoAssistant(UserMgr.getInstance().getmUserAvatarAssistant(),
                        mEditView.getText().toString(), new HTTPMgr.Callback() {
                            @Override
                            public void onSuccess(JSONObject data) {
                                WaitDialog.dismiss();
                                finish(); }
                            @Override
                            public void onFailure(int code, String msg) {
                                Handler mainHandler = new Handler(Looper.getMainLooper());
                                mainHandler.post(() -> TipDialog.show(EditUseInfoAssistantActivity.this, msg+":"+code, TipDialog.TYPE.ERROR)); }
                        });
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_eui_head) {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.rl_eui_head)) { return; }
            GalleryPick.getInstance().setGalleryConfig(Utils.galleryConfig(iHandlerCallBack)).open(EditUseInfoAssistantActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK){
            Log.e(TAG,"onActivityResult->failed for requestWithSign: " + requestCode + "/" + resultCode);
        }
    }
    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      选择头像相关
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    private void initGallery() {
        iHandlerCallBack = new IHandlerCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: 开启");
            }
            @Override
            public void onSuccess(List<String> photoList) {
                Log.i(TAG, "onSuccess: 返回数据");
                path.clear();
                path.addAll(photoList);
                Glide.with(getApplicationContext())
                        .load(path.get(0))
                        .placeholder(R.drawable.ic_camera_download_bg)
                        .transform(new GlideCircleTransform(getApplicationContext()))
                        .into(mIvAvatar);
            }
            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: 取消");
            }
            @Override
            public void onFinish() {
                Log.i(TAG, "onFinish: 结束");
            }
            @Override
            public void onError() {
                Log.i(TAG, "onError: 出错");
            }
        };
    }

    /**
     * 检查裁剪图像相关的权限
     *
     * @return 权限不足返回false，否则返回true
     */
    private boolean checkCropPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(EditUseInfoAssistantActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(EditUseInfoAssistantActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(EditUseInfoAssistantActivity.this,
                        permissions.toArray(new String[0]),
                        Constants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                mPermission = true;
                break;
            case Constants.CAMERA_PERMISSION_REQ_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //takePhoto();
                }
                break;
            default:
                break;
        }
    }
}
