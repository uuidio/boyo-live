package com.kol.jumhz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kol.jumhz.common.utils.RestartUtils;
import com.kol.jumhz.login.WelcomeActivity;

import static com.kol.jumhz.common.utils.RestartUtils.START_LAUNCH_ACTION;
import static com.kol.jumhz.common.utils.RestartUtils.STATUS_FORCE_KILLED;
import static com.kol.jumhz.common.utils.RestartUtils.STATUS_NORMAL;

/**
 * @Package: com.kol.jumhz
 * @ClassName: BaseActivity
 * @Description:
 * @Author: Lanlnk
 * @CreateDate: 2020/6/12 16:53
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (RestartUtils.getInstance().getAppStatus()) {
            case STATUS_FORCE_KILLED:
                Log.d("thisttt" , "BaseModuledActivity-STATUS_FORCE_KILLED");
                restartApp();
                break;
            case STATUS_NORMAL:
                Log.d("thisttt" , "BaseModuledActivity-STATUS_NORMAL");
                break;
            default:
                break;
        }
    }

    private void restartApp() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra(START_LAUNCH_ACTION,STATUS_FORCE_KILLED);
        startActivity(intent);
    }
}
