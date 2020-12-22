package com.kol.jumhz.live;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.R;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        setContentView(R.layout.activity_share);
    }
}