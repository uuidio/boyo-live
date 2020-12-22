package com.kol.jumhz.assistant;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.assistant.AssistantBean;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.view.AssistantView;
import com.kongzue.dialog.v3.TipDialog;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @ClassName: TCAssistantActivity
 * @Author: Dzy
 * @CreateDate: 2020-04-27 14:07
 * @Description: 助理列表
 */
public class AssistantActivity extends BaseActivity {
    private ActivityTitle mTitile;
    private AssistantView assistantView;
    private RecyclerView mList;
    public ArrayList<AssistantBean> list;
    private SkeletonScreen skeletonScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        setContentView(R.layout.activity_assistant);

        initUi();
        initData();
        initEvent();

    }

    private void initUi() {
        mTitile = findViewById(R.id.layout_title);
        mList = findViewById(R.id.list_assistant);

        mList.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        assistantView = new AssistantView(AssistantActivity.this, list);
        mList.setAdapter(assistantView);
        skeletonScreen = Skeleton.bind(mList)
                //设置加载列表适配器 ，并且开启动画 设置光晕动画角度等 最后显示
                .adapter(assistantView).shimmer(true).angle(0)
                .frozen(false)
                .color(R.color.whitesmoke)
                .duration(1000)
                .count(3)
                .load(R.layout.item_assistant_skeleton)
                .show();
    }
    private void initData() {
        UserMgr.getInstance().fetchAssistantList(new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    if(data.optJSONObject("result") == null){ return; }
                    if(data.optJSONObject("result").optJSONObject("data") == null){ return; }
                    list = new ArrayList<>();
                    JSONObject result = data.optJSONObject("result").optJSONObject("data");
                    assert result != null;
                    AssistantBean assistantBean = new AssistantBean(
                            result.optString("login_account"),
                            result.optString("img_url"),
                            result.optString("username")
                    );
                    list.add(assistantBean);
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        assistantView = new AssistantView(AssistantActivity.this, list);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        skeletonScreen.hide();
                        mList.setAdapter(assistantView);
                        //TipDialog.show(AssistantActivity.this, "", TipDialog.TYPE.SUCCESS);
                    });
                }
            }
            @Override
            public void onFailure(int code, final String msg) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> TipDialog.show(AssistantActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
                mainHandler.post(() -> {
                    skeletonScreen.hide();
                    TipDialog.show(AssistantActivity.this, msg+":"+code, TipDialog.TYPE.WARNING);
                });
            }
        });
    }

    private void initEvent() {
        mTitile.setReturnListener(v -> finish());
    }
}