package com.kol.jumhz.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.RestartUtils;
import com.kol.jumhz.main.MainActivity2;

import org.json.JSONObject;

import static com.kol.jumhz.common.utils.RestartUtils.STATUS_NORMAL;

/**
 * @ClassName: WelcomeActivity
 * @Author: LanLnk
 * @CreateDate: 2020-07-07 17:32
 * @Description: 闪屏页面，只是显示一张图
 */
public class WelcomeActivity extends BaseActivity implements Animation.AnimationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RestartUtils.getInstance().setAppStatus(STATUS_NORMAL);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //startService(new Intent(this,AutoUpdateService.class));
        ImageView splashImage = findViewById(R.id.logo);

        Animation animation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f); // 将图片放大1.2倍，从中心开始缩放
        animation.setDuration(2000); // 动画持续时间
        animation.setFillAfter(true); // 动画结束后停留在结束的位置
        animation.setAnimationListener(this);
        splashImage.startAnimation(animation);

    }

    @Override
    public void onAnimationStart(Animation animation) {
        SharedPreferences settings = getSharedPreferences("TCUserInfo", Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(settings.getString("accessToken", ""))) {
            UserMgr.getInstance().login(settings.getString("userid", ""), settings.getString("userpwd", ""), new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    HTTPMgr.getInstance().setmEdUserId(settings.getString("userid", ""));
                    HTTPMgr.getInstance().setmEdPwd(settings.getString("userpwd", ""));
                }

                @Override
                public void onFailure(int code, final String msg) {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }
            });
        } else {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
        }

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity2.class);
        startActivity(intent);
        WelcomeActivity.this.finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
