package com.kol.jumhz.assistant;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.assistant.LotteryBean;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.view.LotteryListView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * @ClassName: TCLotteryListActivity
 * @Author: Dzy
 * @CreateDate: 2020-04-20 19:20
 * @Description: 中奖名单
 */
public class LotteryListActivity extends BaseActivity {
    private ActivityTitle mTitile;
    private RecyclerView mList;
    private LotteryListView lotteryListView;
    private ArrayList<LotteryBean> list = new ArrayList<>();
    private int page = 1;
    private int lastLoadDataItemPosition; //加载更多数据时最后一项的索引
    private SkeletonScreen skeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_list);

        initUi();
        initData();
        initEvent();

    }

    private void initUi() {
        mTitile = findViewById(R.id.layout_title);
        mList = findViewById(R.id.list_banned);

        mList.setLayoutManager(new LinearLayoutManager(LotteryListActivity.this));
        lotteryListView = new LotteryListView(LotteryListActivity.this, list);
        mList.setAdapter(lotteryListView);
        skeletonScreen = Skeleton.bind(mList)
                //设置加载列表适配器 ，并且开启动画 设置光晕动画角度等 最后显示
                .adapter(lotteryListView).shimmer(true).angle(0)
                .frozen(false)
                .color(R.color.whitesmoke)
                .duration(1000)
                .count(7)
                .load(R.layout.item_lottery_skeleton)
                .show();
    }

    public void initData(){
        if (page != 1){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> WaitDialog.show(LotteryListActivity.this,"加载更多"));
        }
        UserMgr.getInstance().fetchLotteryList(page, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data.optJSONObject("result") == null) { return; }
                if (data.optJSONObject("result").optJSONObject("list") == null) { return; }
                if (data.optJSONObject("result").optJSONObject("list").optJSONArray("data") == null) { return; }
                for (int i = 0; i < data.optJSONObject("result").optJSONObject("list").optJSONArray("data").length(); i++) {
                    JSONObject result = data.optJSONObject("result").optJSONObject("list").optJSONArray("data").optJSONObject(i);
                    LotteryBean lotteryBean = new LotteryBean(
                            result.optString("nickname"),
                            result.optInt("user_id"),
                            result.optString("prize"),
                            result.optString("created_at")
                    );
                    list.add(lotteryBean);
                }
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    if (page == 1) {
                        //TipDialog.show(LotteryListActivity.this, "", TipDialog.TYPE.SUCCESS);
                        lotteryListView = new LotteryListView(LotteryListActivity.this, list);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        skeletonScreen.hide();
                        mList.setAdapter(lotteryListView);
                    } else {
                        lotteryListView.setmList(list);
                        lotteryListView.notifyDataSetChanged();
                        WaitDialog.dismiss();
                    }
                    page = data.optJSONObject("result").optJSONObject("list").optString("next_page_url").equals("null") ? 0 : page + 1;
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> TipDialog.show(LotteryListActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
            }
        });
    }

    private void initEvent() {
        mTitile.setReturnListener(v -> finish());

        mList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE && lastLoadDataItemPosition == lotteryListView.getItemCount() && page != 0){
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

}
