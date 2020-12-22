package com.kol.jumhz.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import org.json.JSONObject;

/**
 * @ClassName: TCAssistantLotteryActivity
 * @Author: Dzy
 * @CreateDate: 2020-04-08 19:57
 * @Description: 发起抽奖
 */
public class AssistantLotteryActivity extends BaseActivity {
    private ActivityTitle mTitile;
    private EditText etLotteryTitle;
    private EditText etLotteryGoods;
    private EditText etLotteryNum;
    private EditText etLotteryTime;
    private Button mOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_lottery);

        initUi();
        initEvent();
    }


    private void initUi() {
        mTitile = findViewById(R.id.layout_title);
        etLotteryTitle = findViewById(R.id.et_lottery_title);
        etLotteryGoods = findViewById(R.id.et_lottery_goods);
        etLotteryNum = findViewById(R.id.et_lottery_num);
        etLotteryTime = findViewById(R.id.et_lottery_time);
        mOk = findViewById(R.id.btn_lottery_ok);

    }

    private void initEvent() {
        mTitile.setReturnListener(v -> finish());
        mTitile.setMoreListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.layout_title)) { return; }
            startActivity(new Intent(getApplicationContext(), LotteryListActivity.class));});
        mOk.setOnClickListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.btn_lottery_ok)) { return; }
            WaitDialog.show(AssistantLotteryActivity.this,"");
            if (TextUtils.isEmpty(etLotteryTitle.getText().toString().trim())) {
                TipDialog.show(AssistantLotteryActivity.this,"请填写活动名称", TipDialog.TYPE.WARNING);
                return;
            } else if (TextUtils.isEmpty(etLotteryGoods.getText().toString().trim())) {
                TipDialog.show(AssistantLotteryActivity.this,"请填写活动奖品", TipDialog.TYPE.WARNING);
                return;
            } else if (TextUtils.isEmpty(etLotteryNum.getText().toString().trim())) {
                TipDialog.show(AssistantLotteryActivity.this,"请填写中奖人数", TipDialog.TYPE.WARNING);
                return;
            } else if (TextUtils.isEmpty(etLotteryTime.getText().toString().trim())) {
                TipDialog.show(AssistantLotteryActivity.this,"请填写响应时间", TipDialog.TYPE.WARNING);
                return;
            } else if (Integer.parseInt(etLotteryTime.getText().toString().trim()) < 20) {
                TipDialog.show(AssistantLotteryActivity.this,"响应时间必须大于20秒", TipDialog.TYPE.WARNING);
                return;
            }
            UserMgr.getInstance().addLottery(etLotteryTitle.getText().toString(), etLotteryGoods.getText().toString(),
                    etLotteryNum.getText().toString(), etLotteryTime.getText().toString(), new HTTPMgr.Callback() {
                        @Override
                        public void onSuccess(JSONObject data) {
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> {
                                TipDialog.show(AssistantLotteryActivity.this,"发起成功", TipDialog.TYPE.SUCCESS);
                                etLotteryTitle.setText("");
                                etLotteryGoods.setText("");
                                etLotteryNum.setText("");
                                etLotteryTime.setText("");
                            });
                        }
                        @Override
                        public void onFailure(int code, String msg) {
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> TipDialog.show(AssistantLotteryActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
                        }
                    });
        });
    }

}
