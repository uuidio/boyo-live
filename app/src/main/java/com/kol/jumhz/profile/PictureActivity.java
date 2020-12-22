package com.kol.jumhz.profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.R;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.view.PictureView;

public class PictureActivity extends AppCompatActivity {

    private RecyclerView rvPicture;

    private PictureView pictureView;
    private ActivityTitle activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        setContentView(R.layout.activity_picture);
        rvPicture = findViewById(R.id.rv_picture);
        activityTitle=findViewById(R.id.at_picture);
        activityTitle.setReturnListener(v -> finish());
        rvPicture.setLayoutManager(new GridLayoutManager(this,5));
        pictureView=new PictureView(this,null);
        rvPicture.setAdapter(pictureView );
    }
}