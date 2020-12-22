package com.kol.jumhz.playback;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.playback.PlayBackBean;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.view.PlayBackListView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * @ClassName: PlaybackActivity
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:40
 * @Description: 历史回放页面
 */
public class PlaybackActivity extends BaseActivity implements PlayBackListView.OnTcItemClickListener {
    private ActivityTitle mTitile;
    private PlayBackListView playBackListView;
    private RecyclerView mList;
    private ArrayList<PlayBackBean> list = new ArrayList<>();
    private SkeletonScreen skeletonScreen;

    private int page = 1;
    private int lastLoadDataItemPosition; //加载更多数据时最后一项的索引


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        setContentView(R.layout.activity_playback);

        initUi();
        initData();
        initEvent();
    }

    private void initUi() {
        mTitile = findViewById(R.id.layout_title);
        mList = findViewById(R.id.list_playback);
    }
    private void initData() {
        if (page != 1){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> WaitDialog.show(PlaybackActivity.this,"加载更多"));
        }

        UserMgr.getInstance().fetchPlayBackList(page, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> WaitDialog.dismiss());
                if (data != null) {
                    if(data.optJSONObject("result") == null){ return; }
                    if(data.optJSONObject("result").optJSONObject("list") == null){ return; }
                    if(data.optJSONObject("result").optJSONObject("list").optJSONArray("data") == null){ return; }
                    for (int i = 0; i < data.optJSONObject("result").optJSONObject("list").optJSONArray("data").length(); i++) {
                        JSONObject result = data.optJSONObject("result").optJSONObject("list").optJSONArray("data").optJSONObject(i);
                        if (result.optString("playback") == null) {
                            continue;
                        }
                        PlayBackBean playBackBean = new PlayBackBean(
                                result.optInt("id"),
                                result.optInt("live_id"),
                                result.optString("title"),
                                result.optString("surface_img"),
                                result.optString("playback"),
                                result.optString("start_at"),
                                result.optString("end_at"),
                                result.optString("created_at")
                        );
                        list.add(playBackBean);
                    }
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            if (page == 1) {
                                //TipDialog.show(LotteryListActivity.this, "", TipDialog.TYPE.SUCCESS);
                                playBackListView = new PlayBackListView(PlaybackActivity.this, list);
                                playBackListView.setOnTcItemClickListener(PlaybackActivity.this);
                                skeletonScreen.hide();
                                mList.setAdapter(playBackListView);
                            } else {
                                playBackListView.setList(list);
                                playBackListView.notifyDataSetChanged();
                                WaitDialog.dismiss();
                            }
                            page = data.optJSONObject("result").optJSONObject("list").optString("next_page_url").equals("null") ? 0 : page + 1;
                    });
                }
            }
            @Override
            public void onFailure(int code, final String msg) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    skeletonScreen.hide();
                    TipDialog.show(PlaybackActivity.this, msg + ":" + code, TipDialog.TYPE.WARNING);
                });
            }
        });
    }

    private void initEvent() {
        mList.setLayoutManager(new GridLayoutManager(this,3));
        playBackListView = new PlayBackListView(this, list);
        mList.setAdapter(playBackListView);

        skeletonScreen = Skeleton.bind(mList)
                //设置加载列表适配器 ，并且开启动画 设置光晕动画角度等 最后显示
                .adapter(playBackListView).shimmer(true).angle(0)
                .frozen(false)
                .color(R.color.whitesmoke)
                .duration(1000)
                .count(5)
                .load(R.layout.item_playback_skeleton)
                .show();

        mTitile.setReturnListener(v -> finish());

        mTitile.setMoreListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.layout_title, 3000)) { return; }
            WaitDialog.show(PlaybackActivity.this,"");
            page = 1;
            list.clear();
            skeletonScreen.show();
            initData();
        });

        mList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE && lastLoadDataItemPosition == playBackListView.getItemCount() && page != 0){
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
    protected void onDestroy() {
        super.onDestroy();
        if (playBackListView != null) {
            playBackListView.release();
        }
    }

    @Override
    public void onTcItemClickListener(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            PlayBackListView.DataViewHolder dataViewHolder = (PlayBackListView.DataViewHolder) holder;
            dataViewHolder.item_player.startFullScreen();
        }
    }
}
