package com.kol.jumhz.common.net;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Module:   TCHTTPMgr
 * <p>
 * Function: 专门用来请求小直播后台的网络请求工具类。
 * <p>
 * 1. APP 基于 OKHTTP 封装了网络请求工具模块，若您不想使用 OKHTTP 仅需修改此类即可。
 * <p>
 * 2. 封装了向小直播后台发起请求的方法 {@link HTTPMgr#requestWithSign(String, JSONObject, Callback)}
 * 该方法会自动带上根据 token 以及 userId 生成的 userSign 以保证安全。
 */

public class HTTPMgr {
    private static final String TAG = "TCHTTPMgr";

    private OkHttpClient mOkHTTPClient;
    private String mUserId;
    private String mToken;
    private String mUserIdAssistant;
    private String mTokenAssistant;
    private String mEdUserId;
    private String mEdPwd;

    public String getmEdUserId() {
        return mEdUserId;
    }

    public void setmEdUserId(String mEdUserId) {
        this.mEdUserId = mEdUserId;
    }

    public String getmEdPwd() {
        return mEdPwd;
    }

    public void setmEdPwd(String mEdPwd) {
        this.mEdPwd = mEdPwd;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getmToken() {
        return mToken;
    }

    public void setToken(String mToken) {
        Log.e("TCHTTPMgr", "setToken: " + mToken);
        this.mToken = mToken;
    }

    public static final class TCHTTPClientHolder {
        static HTTPMgr INSTANCE = new HTTPMgr();
    }

    public static final HTTPMgr getInstance() {
        return TCHTTPClientHolder.INSTANCE;
    }

    private HTTPMgr() {

        mOkHTTPClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS) //连接超时阈值
                .readTimeout(10, TimeUnit.SECONDS) //读超时阈值
                .writeTimeout(10, TimeUnit.SECONDS) //写超时阈值
                //.retryOnConnectionFailure(true) //当失败后重试
                .build();
    }

    public void setUserIdAndToken(String userId, String token) {
        mUserId = userId;
        mToken = token;
    }

    public void setUserIdAndTokenAssistant(String userId, String token) {
        mUserId = userId;
        mToken = token;
    }

    /**
     * 一般性的网络请求
     * <p>
     * 项目中可能需要网络请求
     *
     * @param url
     * @param body
     * @param callback
     */
    public void request(String url, JSONObject body, Callback callback) {
        Log.i(TAG, "request: url = " + url + " body = " + (body != null ? body.toString() : ""));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type","multipart/form-data")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body != null ? body.toString() : ""))
                .build();
        mOkHTTPClient.newCall(request).enqueue(new HttpCallback(callback));
    }


    /**
     * 上传图片的网络请求
     *
     * @param url
     * @param body
     * @param callback
     */
    public void requestWithUrlPostImage(String url, String body, Callback callback) {
        Log.i(TAG, "request: url = " + url + " body = " + (body != null ? body.toString() : ""));
        File file = new File(body);
        RequestBody image = RequestBody.create(MediaType.parse("image/png"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", body, image)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader("Authorization", "Bearer "+ mToken)
                .post(requestBody)
                .build();
        mOkHTTPClient.newCall(request).enqueue(new HttpCallback(callback));
    }

    /**
     * 一般性的网络请求
     * <p>
     * 项目中可能需要网络请求
     *
     * @param url
     * @param body
     * @param callback
     */
    public void requestWithUrlPost(String url, JSONObject body, Callback callback) {
        Log.i(TAG, "request: url = " + url + " body = " + (body != null ? body.toString() : ""));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer "+ mToken)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body != null ? body.toString() : ""))
                .build();
        mOkHTTPClient.newCall(request).enqueue(new HttpCallback(callback));
    }

    public void requestWithUrlGet(String url, JSONObject body, Callback callback) {
        Log.i(TAG, "request: url = " + url + " body = " + (body != null ? body.toString() : ""));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer "+ mToken)
                .get()
                .build();
        mOkHTTPClient.newCall(request).enqueue(new HttpCallback(callback));
    }

    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                     网络请求回调
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */

    /**
     * 考虑到您项目中可能并不是使用 okHTTP 所以我们特意对callback进行了一层封装
     * <p>
     * 这样子你可以仅仅修改 @{@link HTTPMgr} 内的代码，即可完整网络模块的改造。
     */
    public interface Callback {

        /**
         * 登录成功
         */
        void onSuccess(JSONObject data);

        /**
         * 登录失败
         *
         * @param code 错误码
         * @param msg  错误信息
         */
        void onFailure(int code, final String msg);
    }

    /**
     * 专用用于解析小直播请求的callback
     */
    private static class HttpCallback implements okhttp3.Callback {
        private Callback callback;

        public HttpCallback(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            if (callback != null) {
                callback.onFailure(-1, "网络错误");
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String body = response.body().string();
            Log.i(TAG, "HttpCallback : onResponse: body = " + body);
            JSONObject jsonObject = null;
            int code = -1;
            try {
                jsonObject = new JSONObject(body);
                code = 0;
            } catch (JSONException e) {
                code = -1;
            }
            if (code == 0) {
                if (callback != null) { callback.onSuccess(jsonObject); }
            } else {
                if (callback != null) { callback.onFailure(code, "数据错误"); }
            }
        }
    }
}
