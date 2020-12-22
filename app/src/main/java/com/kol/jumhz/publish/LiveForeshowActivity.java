package com.kol.jumhz.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.liveforeshow.LiveForeshowBean;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.view.LiveForeshowListView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * @ClassName: TCLiveForeshowActivity
 * @Author: Dzy
 * @CreateDate: 2020-04-27 14:07
 * @Description: 直播预告列表
 */
public class LiveForeshowActivity extends BaseActivity {
    public static Activity activityOne;
    private ActivityTitle mTitile;
    private ArrayList<LiveForeshowBean> list = new ArrayList<>();
    private LiveForeshowListView liveForeshowListView;
    private RecyclerView mList;
    private int page = 1;
    private int lastLoadDataItemPosition; //加载更多数据时最后一项的索引
    private SkeletonScreen skeletonScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOne = this;
        setContentView(R.layout.activity_live_foreshow);

        initUi();
        initData();
        initEvent();
    }

    private void initUi() {
        mTitile = findViewById(R.id.layout_title);
        mList = findViewById(R.id.list_liveforeshow);
    }
    private void initData() {
        if (page != 1){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> WaitDialog.show(LiveForeshowActivity.this,"加载更多"));
        }
        //WaitDialog.show(LiveForeshowActivity.this, "");
        UserMgr.getInstance().fetchLiveForeshowList(page, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> WaitDialog.dismiss());
                if (data != null) {
                    if(data.optJSONObject("result") == null){ return; }
                    if(data.optJSONObject("result").optJSONObject("lists") == null){ return; }
                    if(data.optJSONObject("result").optJSONObject("lists").optJSONArray("data") == null){ return; }

                    for (int i = 0; i < data.optJSONObject("result").optJSONObject("lists").optJSONArray("data").length(); i++) {
                        JSONObject result = data.optJSONObject("result").optJSONObject("lists").optJSONArray("data").optJSONObject(i);
                        LiveForeshowBean liveForeshowBean = new LiveForeshowBean(
                                result.optInt("id"),
                                result.optInt("live_id"),
                                result.optString("img_url"),
                                result.optString("title"),
                                result.optString("introduce"),
                                result.optString("start_at"),
                                result.optString("goodsids"),
                                result.optString("wechat"),
                                result.optString("wechat_img"),
                                result.optString("wechat_path")
                        );
                        list.add(liveForeshowBean);
                    }
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        if (page == 1) {
                            //TipDialog.show(LotteryListActivity.this, "", TipDialog.TYPE.SUCCESS);
                            liveForeshowListView = new LiveForeshowListView(LiveForeshowActivity.this, list);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            skeletonScreen.hide();
                            mList.setAdapter(liveForeshowListView);
                        } else {
                            liveForeshowListView.setList(list);
                            liveForeshowListView.notifyDataSetChanged();
                            WaitDialog.dismiss();
                        }
                        page = data.optJSONObject("result").optJSONObject("lists").optString("next_page_url").equals("null") ? 0 : page + 1;
                    });
                }
            }
            @Override
            public void onFailure(int code, final String msg) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    skeletonScreen.hide();
                    TipDialog.show(LiveForeshowActivity.this, msg+":"+code, TipDialog.TYPE.WARNING);
                });
            }
        });
    }

    private void initEvent() {
        mList.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        liveForeshowListView = new LiveForeshowListView(LiveForeshowActivity.this, list);
        mList.setAdapter(liveForeshowListView);
        skeletonScreen = Skeleton.bind(mList)
                //设置加载列表适配器 ，并且开启动画 设置光晕动画角度等 最后显示
                .adapter(liveForeshowListView).shimmer(true).angle(0)
                .frozen(false)
                .color(R.color.whitesmoke)
                .duration(1000)
                .count(5)
                .load(R.layout.item_liveforeshow_skeleton)
                .show();

        mTitile.setReturnListener(v -> finish());
        mTitile.setMoreListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.layout_title)) { return; }
            Intent intent=new Intent(getApplicationContext(), AddLiveActivity.class);
            intent.putExtra("activity", "LiveForeshowActivity");
            startActivityForResult(intent, 0x11);
        });

        mList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE && lastLoadDataItemPosition == liveForeshowListView.getItemCount() && page != 0){
                    initData();
                }else {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> WaitDialog.dismiss());
                }
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager){
                    LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
                    int firstVisibleItem = manager.findFirstVisibleItemPosition();
                    int l = manager.findLastCompletelyVisibleItemPosition();
                    lastLoadDataItemPosition = firstVisibleItem+(l-firstVisibleItem)+1;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserMgr.getInstance().saveLiveForeshowInfo(list);
    }
}
