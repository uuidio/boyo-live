package com.kol.jumhz.assistant;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
 * @ClassName: AssistanAnnunciateActivity
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:28
 * @Description: 直播间公告界面
 */
public class AssistanAnnunciateActivity extends BaseActivity {
    private ActivityTitle mTitle;
    private EditText etAnnunciate;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistan_annunciate);

        initView();
        initData();
        ininEvent();
    }

    private void  initView() {
        mTitle = findViewById(R.id.layout_title);
        etAnnunciate = findViewById(R.id.et_annunciate);
        btnSave =findViewById(R.id.btn_save);
    }

    protected void initData() {
        WaitDialog.show(AssistanAnnunciateActivity.this, "");
        UserMgr.getInstance().fetchAnnunciateList(new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> WaitDialog.dismiss());
                if (data != null) {
                    if(data.optJSONObject("result") == null){
                        WaitDialog.dismiss();
                        return; }
                    if(data.optJSONObject("result").optJSONObject("data") == null){
                        WaitDialog.dismiss();
                        return; }

                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        etAnnunciate.setText(data.optJSONObject("result").optJSONObject("data").optString("notice"));
                        TipDialog.show(AssistanAnnunciateActivity.this, "", TipDialog.TYPE.SUCCESS).setTipTime(700);
                    });
                }
            }
            @Override
            public void onFailure(int code, final String msg) {
            }
        });
    }

    protected void ininEvent() {
        mTitle.setReturnListener(v -> finish());
        btnSave.setOnClickListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.btn_save)) { return; }
            WaitDialog.show(AssistanAnnunciateActivity.this, "");
            UserMgr.getInstance().updateAnnunciate(etAnnunciate.getText().toString(),new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    runOnUiThread(() -> WaitDialog.dismiss());
                    if (data != null) {
                        if(data.optInt("code") == 0){
                            WaitDialog.dismiss();
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> TipDialog.show(AssistanAnnunciateActivity.this, "更新公告成功", TipDialog.TYPE.SUCCESS).setTipTime(700));
                        }
                    }
                }
                @Override
                public void onFailure(int code, final String msg) {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> TipDialog.show(AssistanAnnunciateActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
                }
            });
        });
    }
}
