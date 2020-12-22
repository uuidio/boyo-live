package com.kol.jumhz.live;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.view.SelectLiveGoodsAssistantView;
import com.kol.jumhz.R;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @ClassName: SelectPicActivity
 * @Author: LanLnk
 * @CreateDate: 2020-04-30 10:52
 * @Description: 助理端选择展示商品界面
 */
public class SelectPicAssistantActivity extends Activity {

    private RecyclerView layout;
    private ImageView mCancel;
    private SelectLiveGoodsAssistantView selectLiveGoodsAssistantView;
    private ArrayList<LiveGoodsBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic_assistant);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();

        initUi();
        initData();
        initEvent();

        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", Toast.LENGTH_SHORT).show());
    }

    private void initUi() {
        layout = findViewById(R.id.pop_layout);
        mCancel = findViewById(R.id.iv_cancel);
    }

    private void initData() {
        SharedPreferences editor = getSharedPreferences("TCLiveGoodsInfoAssistant", Context.MODE_PRIVATE);
        String liastGoods = editor.getString("liveGoodsAssistant", null);
        assert liastGoods != null;
        if (liastGoods.length() > 0) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<LiveGoodsBean>>() {}.getType();
            list = gson.fromJson(liastGoods,listType);
        }
    }

    private void initEvent() {
        layout.setLayoutManager(new GridLayoutManager(this,1));
        selectLiveGoodsAssistantView = new SelectLiveGoodsAssistantView(this, list);
        layout.setAdapter(selectLiveGoodsAssistantView);
        mCancel.setOnClickListener(v -> finish());
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
