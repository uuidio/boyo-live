package com.kol.jumhz.assistant;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.assistant.BannedBean;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.view.BannedView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @ClassName: TCAssistantBannedActivity
 * @Author: Dzy
 * @CreateDate: 2020-04-08 19:56
 * @Description: 禁言列表
 */
public class AssistantBannedActivity extends BaseActivity {
    private ActivityTitle mTitile;
    private BannedView bannedView;
    private RecyclerView mList;
    private ArrayList<BannedBean> list;
    private SkeletonScreen skeletonScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_banned);

        initUi();
        initData();
        initEvent();

    }

    private void initUi() {
        mTitile = findViewById(R.id.layout_title);
        mList = findViewById(R.id.list_banned);
    }
    private void initData() {
        //WaitDialog.show(AssistantBannedActivity.this, "");
        UserMgr.getInstance().fetchBannedList(new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> WaitDialog.dismiss());
                if (data != null) {
                    if(data.optJSONObject("result") == null){
                        //WaitDialog.dismiss();
                        return; }
                    if(data.optJSONObject("result").optJSONArray("list") == null){
                        //WaitDialog.dismiss();
                        return; }
                    list = new ArrayList<>();
                    for (int i = 0; i < data.optJSONObject("result").optJSONArray("list").length(); i++) {
                        JSONObject result = data.optJSONObject("result").optJSONArray("list").optJSONObject(i);
                        BannedBean bannedBean = new BannedBean(
                                result.optInt("id"),
                                result.optInt("user_id"),
                                result.optInt("live_id"),
                                result.optString("accid"),
                                result.optString("roomid"),
                                result.optString("created_at"),
                                result.optString("updated_at"),
                                result.optString("nickname"),
                                result.optString("headimgurl")
                        );
                        list.add(bannedBean);
                    }
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        bannedView = new BannedView(AssistantBannedActivity.this, list);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        skeletonScreen.hide();
                        mList.setAdapter(bannedView);
                        //TipDialog.show(AssistantBannedActivity.this, "", TipDialog.TYPE.SUCCESS).setTipTime(700);
                    });
                }
            }
            @Override
            public void onFailure(int code, final String msg) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> TipDialog.show(AssistantBannedActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
            }
        });
    }

    private void initEvent() {
        mList.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        bannedView = new BannedView(AssistantBannedActivity.this, list);
        mList.setAdapter(bannedView);
        skeletonScreen = Skeleton.bind(mList)
                //设置加载列表适配器 ，并且开启动画 设置光晕动画角度等 最后显示
                .adapter(bannedView).shimmer(true).angle(0)
                .frozen(false)
                .color(R.color.whitesmoke)
                .duration(1000)
                .count(10)
                .load(R.layout.item_banned_skeleton)
                .show();
        mTitile.setReturnListener(v -> finish());
    }

}
