package com.kol.jumhz.live;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.view.SelectLiveGoodsShowView;
import com.kol.jumhz.R;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.ButtonUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
/**
 * @ClassName: SelectPicActivity
 * @Author: LanLnk
 * @CreateDate: 2020-04-30 10:52
 * @Description: 选择展示商品界面
 */
public class SelectPicActivity extends Activity {

    private RecyclerView layout;
    private ImageView mCancel;
    private RelativeLayout mReset, mOk;
    private SelectLiveGoodsShowView selectLiveGoodsShowView;
    private ArrayList<LiveGoodsBean> list = new ArrayList<>();
    private String num = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
        num = getIntent().getStringExtra("num");

        initUi();
        initData();
        initEvent();

        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", Toast.LENGTH_SHORT).show());
    }

    private void initUi() {
        layout = findViewById(R.id.pop_layout);
        mCancel = findViewById(R.id.iv_cancel);
        mReset = findViewById(R.id.rl_reset);
        mOk = findViewById(R.id.rl_ok);
    }

    private void initData() {
        SharedPreferences editor = getSharedPreferences("TCLiveGoodsInfo", Context.MODE_PRIVATE);
        String liastGoods = editor.getString("liveGoods", null);
        assert liastGoods != null;
        if (liastGoods.length() > 0) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<LiveGoodsBean>>() {}.getType();
            list = gson.fromJson(liastGoods,listType);
        }
    }

    private void initEvent() {
        layout.setLayoutManager(new GridLayoutManager(this,1));
        if(num == null) { num = "0"; }
        selectLiveGoodsShowView = new SelectLiveGoodsShowView(this, list, num);
        layout.setAdapter(selectLiveGoodsShowView);
        mCancel.setOnClickListener(v -> finish());

        mReset.setOnClickListener(v -> {
            SharedPreferences settings = getSharedPreferences("liveGoodsInfoPop", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.apply();
            selectLiveGoodsShowView.cancelAll();
        });

        mOk.setOnClickListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.rl_ok)) { return; }
            int id = 0;
            boolean isSelect = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isChecked()) {
                    id = list.get(i).getId();
                    isSelect = true;
                    break;
                }
                else if (i == list.size() -1) {
                    finish();
                }
            }
            if (isSelect){
                UserMgr.getInstance().updateLiveGoods(id,new HTTPMgr.Callback() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        ArrayList<LiveGoodsBean> ivGoodsInfo = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).isChecked()) {
                                ivGoodsInfo.add(list.get(i));
                                break; }
                        }
                        SharedPreferences settings = getSharedPreferences("liveGoodsInfoPop", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("liveGoodsInfoNum", String.valueOf(ivGoodsInfo.get(0).getNum()));
                        editor.putString("liveGoodsInfoUrl", ivGoodsInfo.get(0).getImage());
                        editor.putString("liveGoodsInfoName", ivGoodsInfo.get(0).getTitle());
                        editor.putString("liveGoodsInfoPrice", ivGoodsInfo.get(0).getPrice());
                        editor.apply();
                        finish();
                    }
                    @Override
                    public void onFailure(int code, final String msg) { }
                });
            }
        });
    }

    /**
     * @param event
     * @return
     * 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
