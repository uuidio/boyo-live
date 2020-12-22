package com.kol.jumhz.live;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.R;

public class EditLiveActivity extends AppCompatActivity {

    private LinearLayout llShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        setContentView(R.layout.activity_edit_live);
        findViewById(R.id.im_back).setOnClickListener(v -> finish());
        findViewById(R.id.ll_share).setOnClickListener(v -> {
            startActivity(new Intent(this,ShareActivity.class));
        });
    }
}