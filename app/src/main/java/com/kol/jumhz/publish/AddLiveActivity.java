package com.kol.jumhz.publish;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.liveforeshow.LiveForeshowBean;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.utils.Constants;
import com.kol.jumhz.common.utils.T;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.common.widget.AddLivePhotoAdapter;
import com.kol.jumhz.livegoods.AddLiveGoodsActivity;
import com.kol.jumhz.profile.PictureActivity;
import com.kol.jumhz.view.AddLiveForeGoodsshowView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.kol.jumhz.publish.LiveForeshowActivity.activityOne;

/**
 * @ClassName: TCAddLiveActivity
 * @Author: Dzy
 * @CreateDate: 2020-04-27 14:07
 * @Description: 直播间预告
 */
public class AddLiveActivity extends BaseActivity {
    private String TAG = "选择图片";

    private GalleryConfig galleryConfig;
    private IHandlerCallBack iHandlerCallBack;

    private AddLivePhotoAdapter addLivePhotoAdapter;
    private TimePickerView pvTime;
    private boolean mPermission = false;               // 是否已经授权
    private List<String> path = new ArrayList<>();

    private ActivityTitle mTitleBar;
    private EditText mTitle;
    private TextView mTime;
    private EditText mIntro;
    private RelativeLayout rlLiveTime;
    private ImageView ivLiveCover;
    private ImageView ivLiveGoods;
    private RecyclerView rvResultPhoto;
    private RecyclerView mlist;
    private Button btnOk;
    private ArrayList<LiveGoodsBean> listRetrun =null;
    private ArrayList<LiveGoodsBean> listGoods;
    private int activity = 0;
    private boolean isActivityResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        setContentView(R.layout.activity_add_live);
        initUi();
        initData();
        initEvent();
        initGallery();
        initTime();

        mPermission = checkPublishPermission();
    }

    private void initUi() {
        mTitleBar = findViewById(R.id.rl_title_bar);
        mTitle = findViewById(R.id.et_live_title);
        mTime = findViewById(R.id.tv_live_time);
        mIntro = findViewById(R.id.et_live_intro);
        rlLiveTime = findViewById(R.id.rl_live_time);
        ivLiveCover = findViewById(R.id.iv_live_cover);
        ivLiveGoods = findViewById(R.id.iv_live_goods);
        rvResultPhoto = findViewById(R.id.rv_result_photo);
        mlist = findViewById(R.id.rv_live_goods_item);
        btnOk = findViewById(R.id.btn_ok);

    }

    private void initData() {
        //首次进入页面获取焦点，但不弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        activity = getIntent().getIntExtra("LiveForeshow",0);
        if (activity != 0 && !isActivityResult) {
            WaitDialog.show(this,"");
            Intent intent = getIntent();
            int liveShowId = intent.getIntExtra("LiveForeshow",0);
            UserMgr.getInstance().fetchLiveForeshow(liveShowId,new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        listGoods = new ArrayList<>();
                        JSONObject liveData = data.optJSONObject("result").optJSONObject("data");
                        mTitle.setText(liveData.optString("title"));
                        mIntro.setText(liveData.optString("introduce"));
                        mTime.setText(liveData.optString("start_at"));
                        path.clear();
                        path.add(liveData.optString("img_url"));
                        for (int i = 0; i < liveData.optJSONArray("goods").length(); i++) {
                            JSONObject liveData1 = liveData.optJSONArray("goods").optJSONObject(i);
                            LiveGoodsBean liveGoodsBean = new LiveGoodsBean(
                                    liveData1.optInt("id"),
                                    liveData1.optString("goods_image"),
                                    liveData1.optString("goods_name"),
                                    liveData1.optString("goods_price"),
                                    liveData1.optInt("shop_id"),
                                    true
                            );
                            listGoods.add(liveGoodsBean);
                        }
                        GridLayoutManager gridLayoutManagerPhoto = new GridLayoutManager(getApplicationContext(), 1);
                        gridLayoutManagerPhoto.setOrientation(LinearLayoutManager.VERTICAL);
                        rvResultPhoto.setLayoutManager(gridLayoutManagerPhoto);
                        addLivePhotoAdapter = new AddLivePhotoAdapter(getApplicationContext(), path);
                        rvResultPhoto.setAdapter(addLivePhotoAdapter);

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        mlist.setLayoutManager(gridLayoutManager);
                        mlist.setAdapter(new AddLiveForeGoodsshowView(getApplicationContext(), listGoods));

                        TipDialog.show(AddLiveActivity.this,"", TipDialog.TYPE.SUCCESS);
                    });
                }
                @Override
                public void onFailure(int code, final String msg) {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> TipDialog.show(AddLiveActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
                }
            });
        }
    }

    private void initEvent() {
        mTitleBar.setReturnListener(v -> finish());
        mTitleBar.setMoreListener(v -> {
            mTitle.setText("");
            mTime.setText("");
            mIntro.setText("");
            rvResultPhoto.setAdapter(null);
            mlist.setAdapter(null);
        });
        rlLiveTime.setOnClickListener(v -> {
            // 收起软键盘
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            pvTime.show();
        });

        ivLiveCover.setOnClickListener(v -> {

            if (true)
            {
                startActivity(new Intent(this, PictureActivity.class));
                return;
            }
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.iv_live_cover)) { return; }
            GalleryPick.getInstance().setGalleryConfig(Utils.galleryConfig(iHandlerCallBack)).open(AddLiveActivity.this);
        });

        ivLiveGoods.setOnClickListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.iv_live_goods)) { return; }
            Intent intent=new Intent(getApplicationContext(), AddLiveGoodsActivity.class);
            intent.putExtra("activity", "AddLiveActivity");
            startActivityForResult(intent, 0x11);
        });

        btnOk.setOnClickListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.btn_ok)) { return; }
            if("".equals(mTitle.getText().toString().trim())) {
                T.showShort(AddLiveActivity.this,"请添加直播标题");
                return;
            } else if("".equals(mTime.getText().toString().trim())) {
                T.showShort(AddLiveActivity.this,"请选择开播时间");
                return;
            } else if("".equals(mIntro.getText().toString().trim())) {
                T.showShort(AddLiveActivity.this,"请添加直播简介");
                return;
            } else if(path == null || path.size() == 0) {
                T.showShort(AddLiveActivity.this,"请添加直播封面");
                return;
            } else if(listGoods == null || listGoods.size() == 0){
                T.showShort(AddLiveActivity.this,"请添加直播商品");
                return;
            }
            ArrayList <String> goodsId = new ArrayList<>();
            for (int i = 0; i < listGoods.size(); i++) {
                goodsId.add(String.valueOf(listGoods.get(i).getId()));
            }
            WaitDialog.show(this, "正在添加");
            Intent intent=new Intent(getApplicationContext(), LiveForeshowActivity.class);
            //当直播封面不存在时执行
            if (!path.get(0).contains("http")) {
                UserMgr.getInstance().uploadImage(path.get(0),new HTTPMgr.Callback() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        LiveForeshowBean liveForeshowBean = new LiveForeshowBean(
                                activity, 0, Objects.requireNonNull(data.optJSONObject("result")).optString("pic_url"),
                                mTitle.getText().toString(),
                                mIntro.getText().toString(),
                                mTime.getText().toString(),
                                goodsId.toString(),"");
                        if (activity == 0) {
                            UserMgr.getInstance().addLiveForeshow(liveForeshowBean,new HTTPMgr.Callback() {
                                @Override
                                public void onSuccess(JSONObject data) {
                                    Handler mainHandler = new Handler(Looper.getMainLooper());
                                    mainHandler.post(() ->  T.showShort(getApplicationContext(),"添加成功"));
                                    startActivity(intent);
                                    activityOne.finish();
                                    finish(); }
                                @Override
                                public void onFailure(int code, final String msg) {
                                    Handler mainHandler = new Handler(Looper.getMainLooper());
                                    mainHandler.post(() -> TipDialog.show(AddLiveActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
                                }
                            });
                        } else {
                            UserMgr.getInstance().updateLiveForeshow(liveForeshowBean,new HTTPMgr.Callback() {
                                @Override
                                public void onSuccess(JSONObject data) {
                                    Handler mainHandler = new Handler(Looper.getMainLooper());
                                    mainHandler.post(() ->  T.showShort(getApplicationContext(),"添加成功"));
                                    startActivity(intent);
                                    activityOne.finish();
                                    finish(); }
                                @Override
                                public void onFailure(int code, final String msg) {
                                    Handler mainHandler = new Handler(Looper.getMainLooper());
                                    mainHandler.post(() -> TipDialog.show(AddLiveActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(int code, final String msg) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> TipDialog.show(AddLiveActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
                    }
                });
            } else {
                LiveForeshowBean liveForeshowBean = new LiveForeshowBean(
                        activity,0,  path.get(0),
                        mTitle.getText().toString(),
                        mIntro.getText().toString(),
                        mTime.getText().toString(),
                        goodsId.toString(),"");
                if (activity == 0) {
                    UserMgr.getInstance().addLiveForeshow(liveForeshowBean,new HTTPMgr.Callback() {
                        @Override
                        public void onSuccess(JSONObject data) {
                            startActivity(intent);
                            activityOne.finish();
                            finish(); }
                        @Override
                        public void onFailure(int code, final String msg) {
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> TipDialog.show(AddLiveActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
                        }
                    });
                } else {
                    UserMgr.getInstance().updateLiveForeshow(liveForeshowBean,new HTTPMgr.Callback() {
                        @Override
                        public void onSuccess(JSONObject data) {
                            startActivity(intent);
                            activityOne.finish();
                            finish(); }
                        @Override
                        public void onFailure(int code, final String msg) {
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> TipDialog.show(AddLiveActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
                        }
                    });
                }
            }
        });

        GridLayoutManager gridLayoutManagerPhoto = new GridLayoutManager(this, 1);
        gridLayoutManagerPhoto.setOrientation(LinearLayoutManager.VERTICAL);
        rvResultPhoto.setLayoutManager(gridLayoutManagerPhoto);

        addLivePhotoAdapter = new AddLivePhotoAdapter(this, path);
        rvResultPhoto.setAdapter(addLivePhotoAdapter);
    }

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
                addLivePhotoAdapter = new AddLivePhotoAdapter(getApplicationContext(), path);
                rvResultPhoto.setAdapter(addLivePhotoAdapter);
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

    private void initTime() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",java.util.Locale.getDefault());
        String time = simpleDateFormat.format(new Date(System.currentTimeMillis()));

        startDate.set(Integer.parseInt(time.split("-")[0]),Integer.parseInt(time.split("-")[1])-1,Integer.parseInt(time.split("-")[2]));
        endDate.set(2099,11,31);
        pvTime = new TimePickerBuilder(this, (date, v) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",java.util.Locale.getDefault());
            mTime.setText(sdf.format(date)+":00"); })
                .setType(new boolean[]{true, true, true, true, true, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(20)//标题文字大小
                .setTitleText("")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                //.setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.parseColor("#66BB6A"))//确定按钮文字颜色
                .setCancelColor(Color.parseColor("#66BB6A"))//取消按钮文字颜色
                //.setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
                //.setBgColor(0xFF333333)//滚轮背景颜色 Night mode
                //.setLabel("","月","日","时","分","秒")//默认设置为年月日时分秒
                .setRangDate(startDate,endDate)//起始终止年月日设定
                .setLineSpacingMultiplier(2f)//设置两横线之间的间隔倍数
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0x11) {
            if (resultCode == RESULT_OK) {
                isActivityResult = true;
                listGoods = new ArrayList<>();
                listGoods = (ArrayList<LiveGoodsBean>) data.getSerializableExtra("retrunData");
                assert listRetrun != null;
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
                gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mlist.setLayoutManager(gridLayoutManager);
                mlist.setAdapter(new AddLiveForeGoodsshowView(this, listGoods));
            }
        }
    }

    /**
     * @ClassName: checkPublishPermission
     * @Description: 动态权限检查相关
     */
    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) { return; }
                }
                mPermission = true;
                break;
            default:
        }
    }
}


