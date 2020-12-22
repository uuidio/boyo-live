package com.kol.jumhz.main;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.assistant.AssistantActivity;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.live.EditLiveActivity;
import com.kol.jumhz.livegoods.RelevanceLiveGoodsActivity;
import com.kol.jumhz.playback.PlaybackActivity;
import com.kol.jumhz.publish.AddLiveActivity;
import com.kol.jumhz.view.LiveListView;

import java.util.List;

public class MainActivity2 extends BaseActivity {

    private TextView tvRelatGoods;
    private TextView tvHistory;
    private TextView tvAssisant;
    private TextView tvExit;
    private ImageView imAddLive;
    private RecyclerView rvLive;
    private ImageView imMore;
    private ImageView imHead;
    private LinearLayout llDialog;
    private LiveListView adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        setContentView(R.layout.activity_main2);
        initView();
        initListaner();
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        adapter=new LiveListView(this,null,screenWidth);
        rvLive.setLayoutManager(new GridLayoutManager(this,3));
        rvLive.addItemDecoration(itemDecoration);
        rvLive.setAdapter(adapter);
        adapter.setItemClickListener(position -> {
            startActivity(new Intent(this, EditLiveActivity.class));
        });

    }
    RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            int mode = parent.getChildLayoutPosition(view) % 3;
            outRect.top=(int) getResources().getDimension(R.dimen.w_45px_port);
            outRect.bottom = 0;
            if (mode == 0) {
                outRect.left = (int) getResources().getDimension(R.dimen.w_29px_port);
                outRect.right = (int) getResources().getDimension(R.dimen.w_32px_port);
            } else if (mode == 3 - 1) {
                outRect.right =(int) getResources().getDimension(R.dimen.w_32px_port);
                outRect.left = (int) getResources().getDimension(R.dimen.w_32px_port);
            } else {
                outRect.left = (int) getResources().getDimension(R.dimen.w_32px_port);
                outRect.right = (int) getResources().getDimension(R.dimen.w_29px_port);
            }
        }
    };

    private void initView() {
        tvRelatGoods = findViewById(R.id.tv_relate_goods);
        tvHistory = findViewById(R.id.tv_history);
        tvAssisant = findViewById(R.id.tv_assisant);
        tvExit = findViewById(R.id.tv_exit);
        imAddLive = findViewById(R.id.im_add_live);
        rvLive = findViewById(R.id.rv_live);
        imMore = findViewById(R.id.im_more);
        imHead = findViewById(R.id.im_head);
        llDialog = findViewById(R.id.ll_dialog);
    }

    private void initListaner() {
        imMore.setOnClickListener(v -> {
            if (llDialog.getVisibility()== View.VISIBLE)
            {
                llDialog.setVisibility(View.GONE);
            }else {
                llDialog.setVisibility(View.VISIBLE);
            }
        });
        tvRelatGoods.setOnClickListener(v -> {
            if (ButtonUtils.isFastDoubleClick(R.id.tv_relate_goods)) { return; }
            startActivity(new Intent(this, RelevanceLiveGoodsActivity.class));
            llDialog.setVisibility(View.GONE);
        });

        tvHistory.setOnClickListener(v -> {
            if (ButtonUtils.isFastDoubleClick(R.id.tv_history)) { return; }
            startActivity(new Intent(this, PlaybackActivity.class));
            llDialog.setVisibility(View.GONE);
        });
        imAddLive.setOnClickListener(v -> {
            if (ButtonUtils.isFastDoubleClick(R.id.im_add_live)) { return; }
            Intent intent=new Intent(getApplicationContext(), AddLiveActivity.class);
            intent.putExtra("activity", "LiveForeshowActivity");
            startActivityForResult(intent, 0x11);
        });
        tvAssisant.setOnClickListener(v -> {
            if (ButtonUtils.isFastDoubleClick(R.id.tv_assisant)) { return; }
            startActivity(new Intent(this, AssistantActivity.class));
        });
        tvExit.setOnClickListener(v -> {
            dialog("确定要退出登录？");
        });

    }


    /**
     * 弹出对话框方法
     * @param text
     */
    private void dialog(String text) {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();

        Window window = dlg.getWindow();
        //这一句消除白块
       // window.setBackgroundDrawable(new BitmapDrawable());
        window.setGravity(Gravity.CENTER);
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_tips,null);
        //dlg.setView(view);
        dlg.show();

        WindowManager.LayoutParams params= window.getAttributes();
        params.width= (int) getResources().getDimension(R.dimen.w_742px_port);
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity=Gravity.CENTER;

        dlg.getWindow().setAttributes(params);
        window.setContentView(view);

        assert window != null;
        TextView tv = window.findViewById(R.id.tv);
        tv.setText(text);
        TextView on = window.findViewById(R.id.tv_no);
        TextView ok = window.findViewById(R.id.tv_ok);
        //确定按钮
        ok.setOnClickListener(v -> {
            dlg.dismiss();
            if (text.contains("登录")) {

            } else if (text.contains("强制")) {

            } else {
                //销毁任务栈
                ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                assert activityManager != null;
                List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
                for (ActivityManager.AppTask appTask : appTaskList) { appTask.finishAndRemoveTask(); }
            }
        });
        //取消按钮
        on.setOnClickListener(v -> dlg.cancel());
    }

}