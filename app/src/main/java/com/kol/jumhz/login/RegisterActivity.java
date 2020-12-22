package com.kol.jumhz.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kol.jumhz.R;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.main.MainActivity;

import org.json.JSONObject;


/**
 *  Module:   TCRegisterActivity
 *
 *  Function: 注册小直播账号
 *
 */
public class RegisterActivity extends Activity  {

    public static final String TAG = RegisterActivity.class.getSimpleName();

    //共用控件
    private RelativeLayout relativeLayout;

    private ProgressBar progressBar;

    private EditText etUsername;

    private EditText etPassword;

    private EditText etPasswordVerify;

    private Button btnRegister;

    private TextView tvBackBtn;

    //动画
    AlphaAnimation fadeInAnimation, fadeOutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        relativeLayout = (RelativeLayout) findViewById(R.id.rl_register_root);

        etUsername = (EditText) findViewById(R.id.et_username);

        etPassword = (EditText) findViewById(R.id.et_password);

        etPasswordVerify = (EditText) findViewById(R.id.et_password_verify);

        btnRegister = (Button) findViewById(R.id.btn_register);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        tvBackBtn = (TextView) findViewById(R.id.tv_back);

        fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeInAnimation.setDuration(250);
        fadeOutAnimation.setDuration(250);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userNameRegisterViewInit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showOnLoadingInUIThread(final boolean active) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showOnLoading(active);
            }
        });
    }

    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showOnLoading(boolean active) {
        if (active) {
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.INVISIBLE);
            etPassword.setEnabled(false);
            etPasswordVerify.setEnabled(false);
            etUsername.setEnabled(false);
            btnRegister.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnRegister.setVisibility(View.VISIBLE);
            etPassword.setEnabled(true);
            etPasswordVerify.setEnabled(true);
            etUsername.setEnabled(true);
            btnRegister.setEnabled(true);
        }

    }

    private void userNameRegisterViewInit() {

        etPassword.setText("");
        etPassword.setError(null, null);

        etPasswordVerify.setText("");
        etPasswordVerify.setError(null, null);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用normal注册逻辑
                attemptNormalRegist(etUsername.getText().toString(),
                        etPassword.getText().toString(), etPasswordVerify.getText().toString());
            }
        });

        tvBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showRegistError(String errorString) {
//        etRegister.setError(errorString);
        showOnLoading(false);
    }

    private void showPasswordVerifyError(String errorString) {
        etPassword.setError(errorString);
        showOnLoading(false);
    }
    private void attemptNormalRegist(String username, String password, String passwordVerify) {

        if (!Utils.isUsernameVaild(username)) {
            showRegistError("用户名不符合规范");
            return ;
        }
        if (!Utils.isPasswordValid(password)) {
            showPasswordVerifyError("密码长度应为8-16位");
            return ;
        }
        if (!password.equals(passwordVerify)) {
            showPasswordVerifyError("两次输入密码不一致");
            return ;
        }
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(getApplicationContext(), "当前无网络连接", Toast.LENGTH_SHORT).show();
            return ;
        }

        register(username, password);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void jumpToHomeActivity () {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void jumpToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void register(final String username, final String password) {
        final UserMgr tcLoginMgr = UserMgr.getInstance();
        tcLoginMgr.register(username, password, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                int retCode = data.optInt("code", 0);
                String message = data.optString("message", "");
                if (retCode == 200) {
                showToast("成功注册");

                // 注册成功之后，将自动登录。
                tcLoginMgr.login(username, password, new HTTPMgr.Callback() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        showToast("自动登录成功");
                        jumpToHomeActivity(); // 登录成功，跳转到 MainActivity
                    }

                    @Override
                    public void onFailure(int code, final String msg) {
                        showToast("自动登录失败");
                        showOnLoadingInUIThread(false);
                        jumpToLoginActivity(); // 登录失败，登录界面。
                    }
                });
                } else {
                    if (retCode == 610) {
                        message = "用户名格式错误";
                    } else if (retCode == 611){
                        message = "密码格式错误";
                    } else if (retCode == 612){
                        message = "用户已存在";
                    }
                    showToast("注册失败 ：" + message);
                    showOnLoadingInUIThread(false);
                }
            }

            @Override
            public void onFailure(int code, final String msg) {
                String errorMsg = msg;
                showToast("注册失败 ：" + errorMsg);
                showOnLoadingInUIThread(false);
            }
        });
        showOnLoading(true);
    }
}
