package com.kol.jumhz.prepare;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.utils.Constants;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.live.LiveActivity;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName: AnchorPrepareActivity
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:32
 * @Description: 主播开播设置页面
 */
public class AnchorPrepareActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = AnchorPrepareActivity.class.getSimpleName();

    private TextView                        mTvPicTip;      // 封面提示
    private EditText                        mTvTitle;       // 直播标题
    private ImageView                       mIvCover;       // 图片封面
    private RadioGroup                      mRGRecordType;  // 推流类型：摄像头推流或录制直播推流
    private int                             mRecordType = Constants.RECORD_TYPE_RECORD;   // 默认摄像头推流

    private boolean                          mPermission = false;               // 是否已经授权

    private IHandlerCallBack iHandlerCallBack;
    private List<String> path = new ArrayList<>();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_prepare);
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        ActivityTitle mTitle = findViewById(R.id.rl_layout_title);
        mTvTitle = findViewById(R.id.anchor_tv_title);
        mTvPicTip = findViewById(R.id.anchor_pic_tips);
        // 开始直播
        TextView mTvPublish = findViewById(R.id.anchor_btn_publish);
        mIvCover = findViewById(R.id.anchor_btn_cover);
        //mRGRecordType = (RadioGroup) findViewById(R.id.anchor_rg_record_type);
        mIvCover.setOnClickListener(this);
        mTvPublish.setOnClickListener(this);
        //mRGRecordType.setOnCheckedChangeListener(this);

        mPermission = checkPublishPermission();
        initData();
        initGallery();

        mTitle.setMoreListener(v -> finish());
    }

    /**
     * 初始化
     */
    private void initData() {
        if (!"".equals(pref.getString("liveCover", ""))) {
            Glide.with(AnchorPrepareActivity.this).load(pref.getString("liveCover", "")).into(mIvCover);
            mTvTitle.setText(pref.getString("liveTitle", ""));
            mTvPicTip.setVisibility(View.GONE);
            path.clear();
            path.add(pref.getString("liveCover", ""));
        } else {
            mIvCover.setImageResource(R.drawable.publish_background);
            path.clear();
        }
    }

    private void initGallery() {
        iHandlerCallBack = new IHandlerCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: 开启");
            }
            @Override
            public void onSuccess(List<String> photoList) {
                path.clear();
                path.addAll(photoList);
                Glide.with(AnchorPrepareActivity.this).load(path.get(0)).into(mIvCover);
                mTvPicTip.setVisibility(View.GONE);
                //addLivePhotoAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.anchor_btn_publish:
                //短时间多次点击
                if (ButtonUtils.isFastDoubleClick(R.id.anchor_btn_publish, 3000)) { return; }
                //trim避免空格字符串
                if (TextUtils.isEmpty(mTvTitle.getText().toString().trim())) {
                    TipDialog.show(AnchorPrepareActivity.this,"请输入直播标题", TipDialog.TYPE.WARNING);
                    return;
                } else if (Utils.getCharacterNum(mTvTitle.getText().toString()) > Constants.TV_TITLE_MAX_LEN) {
                    TipDialog.show(AnchorPrepareActivity.this,"直播标题过长 ,最大长度为" + Constants.TV_TITLE_MAX_LEN / 2, TipDialog.TYPE.WARNING);
                    return;
                } else if (!Utils.isNetworkAvailable(this)) {
                    TipDialog.show(AnchorPrepareActivity.this,"当前网络环境不能发布直播", TipDialog.TYPE.WARNING);
                    return;
                } else if (path.size() == 0) {
                    TipDialog.show(AnchorPrepareActivity.this,"请选择直播封面", TipDialog.TYPE.WARNING);
                    return;
                } else if (TextUtils.isEmpty(UserMgr.getInstance().getNickname()) || TextUtils.isEmpty(UserMgr.getInstance().getAvatar())) {
                    TipDialog.show(AnchorPrepareActivity.this,"请设置头像和昵称", TipDialog.TYPE.WARNING);
                    return;
                } else {
                    if (!path.get(0).contains("http")) {
                        WaitDialog.show(AnchorPrepareActivity.this, "正在上传直播间封面");
                        UserMgr.getInstance().uploadImage(path.get(0),new HTTPMgr.Callback() {
                            @Override
                            public void onSuccess(JSONObject data) {
                                UserMgr.getInstance().setCoverPic(Objects.requireNonNull(data.optJSONObject("result")).optString("pic_url"));
                                UserMgr.getInstance().setLiveTitle(mTvTitle.getText().toString());
                                //储存账号
                                editor = pref.edit();
                                editor.putString("liveCover", Objects.requireNonNull(data.optJSONObject("result")).optString("pic_url"));
                                editor.putString("liveTitle", mTvTitle.getText().toString());
                                editor.apply();
                                startPublish();
                            }
                            @Override
                            public void onFailure(int code, final String msg) {
                                Handler mainHandler = new Handler(Looper.getMainLooper());
                                mainHandler.post(() -> {
                                    TipDialog.show(AnchorPrepareActivity.this,msg+":"+code, TipDialog.TYPE.WARNING);
                                });
                            }
                        }); }
                    else {
                        UserMgr.getInstance().setCoverPic(path.get(0));
                        UserMgr.getInstance().setLiveTitle(mTvTitle.getText().toString());
                        //储存账号
                        editor = pref.edit();
                        editor.putString("liveCover", path.get(0));
                        editor.putString("liveTitle", mTvTitle.getText().toString());
                        editor.apply();
                        startPublish();
                    }
                }
                break;
            case R.id.anchor_btn_cover:
                //短时间多次点击
                if (ButtonUtils.isFastDoubleClick(R.id.anchor_btn_cover)) { return; }
                if (!mPermission) {
                    mPermission = checkPublishPermission();
                    return;
                }
                GalleryPick.getInstance().setGalleryConfig(Utils.galleryConfig(iHandlerCallBack)).open(AnchorPrepareActivity.this);
                break;
            default:
        }
    }

    /**
     * 发起推流
     *
     */
    private void startPublish() {
        Intent intent;
        intent = new Intent(AnchorPrepareActivity.this, LiveActivity.class);
        intent.putExtra(Constants.IS_RECORD, "true");
        UserMgr.getInstance().setIsRecord("true");
        WaitDialog.dismiss();
        intent.putExtra(Constants.ROOM_TITLE, TextUtils.isEmpty(mTvTitle.getText().toString()) ? UserMgr.getInstance().getNickname() : mTvTitle.getText().toString());
        intent.putExtra(Constants.USER_ID, UserMgr.getInstance().getUserId());
        intent.putExtra(Constants.USER_NICK, UserMgr.getInstance().getNickname());
        intent.putExtra(Constants.USER_HEADPIC, UserMgr.getInstance().getAvatar());
        intent.putExtra(Constants.COVER_PIC, UserMgr.getInstance().getCoverPic());

        startActivity(intent);
        finish();
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
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(AnchorPrepareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(AnchorPrepareActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(AnchorPrepareActivity.this,
                        permissions.toArray(new String[0]),
                        Constants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }

        return true;
    }

    private boolean checkScrRecordPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
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
                }
                break;
            default:
                break;
        }
    }

/*    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.anchor_rb_record_camera:
                mRecordType = TCConstants.RECORD_TYPE_CAMERA;
                break;
            case R.id.anchor_rb_record_screen:
                mRecordType = TCConstants.RECORD_TYPE_RECORD;
                break;
            default:
                break;
        }
    }*/
}
