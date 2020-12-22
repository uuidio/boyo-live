package com.kol.jumhz.protocol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kol.jumhz.R;
import com.kol.jumhz.common.widget.ActivityTitle;

public class ProtocolActivity extends AppCompatActivity {

    private ActivityTitle activityTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
        activityTitle=findViewById(R.id.rl_protocol_title);
        String type=getIntent().getStringExtra("type");
        activityTitle.setTitle(type);

        activityTitle.setReturnListener(v -> {
            finish();
        });
    }
}