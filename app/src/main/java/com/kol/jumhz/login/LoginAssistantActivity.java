package com.kol.jumhz.login;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.main.MainAssistantActivity;
import com.kongzue.dialog.v3.TipDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *  Module:   TCLoginAssistantActivity
 *
 *  Function: 用于登录助理端的页面
 *
 *  1. 未登陆过，输入账号密码登录
 *
 *  2. 已经登陆过，获取读取缓存，并且自动登录。 详见{@link UserMgr}
 * @author Dzy
 */
public class LoginAssistantActivity extends BaseActivity {
    private SharedPreferences pref;
    private ProgressBar progressBar;
    private ImageView ivLoginSucceed;
    private ActivityTitle mTitle;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvAnchorLogin;
    private TextView tvXieyi;
    private TextView tvYinsi;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_assistant);
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        mTitle = findViewById(R.id.rl_user_login_title);
        etPassword = findViewById(R.id.et_password);
        etUsername = findViewById(R.id.et_username);
        tvRegister = findViewById(R.id.btn_register);
        tvAnchorLogin = findViewById(R.id.tv_anchor_login);
        tvXieyi = findViewById(R.id.tv_xieyi);
        tvYinsi = findViewById(R.id.tv_yinsi);
        checkBox = findViewById(R.id.checkBox);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressbar);
        ivLoginSucceed = findViewById(R.id.iv_login_succeed);

        userNameLoginViewInit();
        initEvent();

        //检测是否存在缓存
        //checkLogin();
    }

    /**
     * 用户名密码登录界面init
     */
    public void userNameLoginViewInit() {
        etUsername.setInputType(EditorInfo.TYPE_CLASS_TEXT);

        // 监听多个输入框
        TextChange textChange = new TextChange();
        etUsername.addTextChangedListener(textChange);
        etPassword.addTextChangedListener(textChange);

        etUsername.setText(pref.getString("accountAssistant", ""));
        etPassword.setText(pref.getString("passwordAssistant", ""));

        //如果用户更改，清除密码
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPassword.setText(null);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void initEvent() {
        mTitle.setReturnListener(v -> finish());

        tvRegister.setOnClickListener(view -> {
            //注册界面 phoneView 与 normalView跳转逻辑一致
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(view -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.btn_login)) { return; }
            if (!checkBox.isChecked()) {
                TipDialog.show(LoginAssistantActivity.this,"请阅读并同意协议勾选", TipDialog.TYPE.WARNING);
                //Toast.makeText(getApplicationContext(), "请阅读并同意勾选协议", Toast.LENGTH_SHORT).show();
                return;
            }
            //调用normal登录逻辑
            showOnLoading(true);
            login(etUsername.getText().toString(), etPassword.getText().toString());
            //attemptNormalLogin(etUsername.getText().toString(), etPassword.getText().toString());
        });

        tvAnchorLogin.setOnClickListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.tv_anchor_login)) { return; }
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        tvXieyi.setOnClickListener(v -> {
            String str = initAssets("yhxy.txt");
            final View inflate = LayoutInflater.from(LoginAssistantActivity.this).inflate(R.layout.dialog_xieyi_yinsi_style, null);
            TextView tvTitle =  inflate.findViewById(R.id.tv_title);
            TextView tvContent = inflate.findViewById(R.id.tv_content);
            TextView tvCancle = inflate.findViewById(R.id.tv_cancle);
            tvTitle.setText("《聚美集用户协议与交易规则》");
            tvContent.setText(str);
            final Dialog dialog = new AlertDialog.Builder(LoginAssistantActivity.this)
                    .setView(inflate)
                    .show();
            DisplayMetrics dm = new DisplayMetrics();
            //获取屏幕宽高
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (dm.widthPixels-dm.widthPixels*0.1f);
            params.height = (int) (dm.heightPixels-dm.heightPixels*0.15f);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            tvCancle.setOnClickListener(v12 -> {
                checkBox.setChecked(true);
                dialog.dismiss();
            });
        });

        tvYinsi.setOnClickListener(v -> {
            String str = initAssets("yszc.txt");
            final View inflate = LayoutInflater.from(LoginAssistantActivity.this).inflate(R.layout.dialog_xieyi_yinsi_style, null);
            TextView tvTitle = inflate.findViewById(R.id.tv_title);
            TextView tvContent = inflate.findViewById(R.id.tv_content);
            TextView tvCancle = inflate.findViewById(R.id.tv_cancle);
            tvTitle.setText("《聚美集隐私政策》");
            tvContent.setText(str);
            final Dialog dialog = new AlertDialog.Builder(LoginAssistantActivity.this)
                    .setView(inflate).show();
            DisplayMetrics dm = new DisplayMetrics();
            //获取屏幕宽高
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (dm.widthPixels-dm.widthPixels*0.1f);
            params.height = (int) (dm.heightPixels-dm.heightPixels*0.15f);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            tvCancle.setOnClickListener(v1 -> {
                checkBox.setChecked(true);
                dialog.dismiss();
            });
        });
    }

    /**
     * trigger loading模式
     *
     * @param active
     */
    private void showOnLoading(boolean active) {
        if (active) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);
            etUsername.setEnabled(false);
            etPassword.setEnabled(false);
            tvRegister.setClickable(false);
        } else {
            progressBar.setVisibility(View.GONE);
            ivLoginSucceed.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            etUsername.setEnabled(true);
            etPassword.setEnabled(true);
            tvRegister.setClickable(true);
            tvRegister.setTextColor(getResources().getColor(R.color.colorTextBlack));
        }

    }

    private void showToast(final String msg) {
        runOnUiThread(() -> TipDialog.show(LoginAssistantActivity.this,msg, TipDialog.TYPE.WARNING));
    }

    private void showOnLoadingInUIThread(final boolean active) {
        runOnUiThread(() -> showOnLoading(active));
    }

    private void showLoginError(String errorString) {
        etUsername.setError(errorString);
        showOnLoading(false);
    }

    private void showPasswordError(String errorString) {
        etPassword.setError(errorString);
        showOnLoading(false);
    }

    /**
     * 登录成功后被调用，跳转至TCMainActivity
     */
    private void jumpToHomeActivity() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            ivLoginSucceed.setVisibility(View.VISIBLE);
        });
        //储存账号
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("accountAssistant", etUsername.getText().toString());
        editor.putString("passwordAssistant", etPassword.getText().toString());
        editor.apply();
        Intent intent = new Intent(this, MainAssistantActivity.class);
        startActivity(intent);
        finish();
    }

    private void login(String username, String password) {
        final UserMgr tcLoginMgr = UserMgr.getInstance();
        tcLoginMgr.loginAssistant(username, password, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                HTTPMgr.getInstance().setmEdUserId(etUsername.getText().toString());
                HTTPMgr.getInstance().setmEdPwd(etPassword.getText().toString());
                jumpToHomeActivity();
            }

            @Override
            public void onFailure(int code, final String msg) {
                showToast(msg);
                showOnLoadingInUIThread(false);
            }
        });
    }

    private void checkLogin() {
        if (Utils.isNetworkAvailable(this)) {
            //返回true表示存在本地缓存，进行登录操作，显示loadingFragment
            if (UserMgr.getInstance().hasUser()) {
                showOnLoadingInUIThread(true);
                UserMgr.getInstance().autoLoginAssistant(new HTTPMgr.Callback() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        jumpToHomeActivity();
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        showToast("自动登录失败");
                        showOnLoadingInUIThread(false);
                    }
                });
            }
        }
    }

    /**
     * 用户名密码登录
     *
     * @param username 用户名
     * @param password 密码
     */
    public void attemptNormalLogin(String username, String password) {
        if (Utils.isPasswordValid(password)) {
            if (Utils.isNetworkAvailable(this)) {
                if (!checkBox.isChecked()) {
                    TipDialog.show(LoginAssistantActivity.this,"请阅读并同意协议勾选", TipDialog.TYPE.WARNING);
                    return;
                }
                login(username, password);
            } else {
                TipDialog.show(LoginAssistantActivity.this,"当前无网络连接", TipDialog.TYPE.ERROR);
            }
        } else {
            TipDialog.show(LoginAssistantActivity.this,"密码长度应为8-16位", TipDialog.TYPE.WARNING);
        }
    }

    /**
     * EditText监听器
     */
    class TextChange implements TextWatcher {
        @Override
        public void afterTextChanged(Editable arg0) {
        }
        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
        @Override
        public void onTextChanged(CharSequence cs, int start, int before, int count) {
            boolean sign2 = etUsername.getText().length() > 0;
            boolean sign3 = etPassword.getText().length() > 0;
            // 在layout文件中，对Button的text属性应预先设置默认值，否则刚打开程序的时候Button是无显示的
            if (sign2 & sign3) {
                btnLogin.setEnabled(true);
            } else {
                btnLogin.setEnabled(false);
            }
        }
    }

    /**
     * 从assets下的txt文件中读取数据
     */
    public String initAssets(String fileName) {
        String str = null;
        InputStreamReader inputStreamReader;
        try {
            InputStream inputStream = getAssets().open(fileName);
            try {
                inputStreamReader = new InputStreamReader(inputStream, "gbk");
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    str = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return str;
    }
}
