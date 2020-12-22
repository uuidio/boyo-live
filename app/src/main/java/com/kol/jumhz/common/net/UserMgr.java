package com.kol.jumhz.common.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kol.jumhz.GlobalConfig;
import com.kol.jumhz.common.liveforeshow.LiveForeshowBean;
import com.kol.jumhz.common.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @ClassName: UserMgr
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:46
 * @Description: 接口管理
 */
public class UserMgr {
    public static final String TAG = UserMgr.class.getSimpleName();

    private Context mContext;              // context 上下文
    private String mUserId = "";           // 用户id
    private String mUserPwd = "";          // 用户密码
    private String mUserIdAssistant = "";   // 助理id
    private String mUserPwdAssistant = "";  // 助理密码
    private String mAccessToken = "";            // token
    private String mAccessTokenAssistant = "";   // 助理token
    private long mSdkAppID = 0;            // sdkAppId
    private String mNickName = "";         // 昵称
    private String mNickNameAssistant = "";// 助理昵称
    private String mUserAvatar = "";       // 头像连接地址
    private String mUserAvatarAssistant = "";// 助理头像连接地址
    private String mLiveTitle = "";       // 直播间标题
    private String mCoverPic;             // 直播用的封面图

    private String roomId = "";            //聊天室id
    private String isRecord = "";        //是否为录制直播

    private static class TCUserMgrHolder {
        private static UserMgr instance = new UserMgr();
    }

    public static UserMgr getInstance() {
        return TCUserMgrHolder.instance;
    }

    private UserMgr() {
    }

    public void initContext(Context context) {
        mContext = context.getApplicationContext();
        loadUserInfo();
    }

    public boolean hasUser() {
        return !TextUtils.isEmpty(mUserId) && !TextUtils.isEmpty(mUserPwd);
    }

    public boolean hasUserAssistant() {
        return !TextUtils.isEmpty(mUserIdAssistant) && !TextUtils.isEmpty(mUserPwdAssistant);
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getUserAvatar() {
        return mUserAvatar;
    }

    public String getNickname() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        this.mNickName = nickName;
    }

    public String getAvatar() {
        return mUserAvatar;
    }

    public void setAvatar(String avatar) {
        this.mUserAvatar = avatar;
    }

    public String getCoverPic() {
        return mCoverPic;
    }

    public void setCoverPic(String pic) {
        this.mCoverPic = pic;
    }

    public String getLiveTitle() {
        return mLiveTitle;
    }

    public void setLiveTitle(String mLiveTitle) {
        this.mLiveTitle = mLiveTitle;
    }

    public long getSDKAppID() {
        return mSdkAppID;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getIsRecord() {
        return isRecord;
    }

    public void setIsRecord(String isRecord) {
        this.isRecord = isRecord;
    }

    public String getmUserIdAssistant() {
        return mUserIdAssistant;
    }

    public void setmUserIdAssistant(String mUserIdAssistant) {
        this.mUserIdAssistant = mUserIdAssistant;
    }

    public String getmAccessTokenAssistant() {
        return mAccessTokenAssistant;
    }

    public void setmAccessTokenAssistant(String mAccessTokenAssistant) {
        this.mAccessTokenAssistant = mAccessTokenAssistant;
    }

    public String getmNickNameAssistant() {
        return mNickNameAssistant;
    }

    public void setmNickNameAssistant(String mNickNameAssistant) {
        this.mNickNameAssistant = mNickNameAssistant;
    }

    public String getmUserAvatarAssistant() {
        return mUserAvatarAssistant;
    }

    public void setmUserAvatarAssistant(String mUserAvatarAssistant) {
        this.mUserAvatarAssistant = mUserAvatarAssistant;
    }


    public void logout() {
        mUserId = "";
        mUserPwd = "";
        mCoverPic = "";
        mUserAvatar = "";
        clearUserInfo();
    }

    public void logoutAssistant() {
        mUserIdAssistant = "";
        mUserPwdAssistant = "";
        mUserAvatarAssistant = "";
        clearUserInfoAssistant();
    }


    private void loadUserInfo() {
        //TODO: decrypt
        if (mContext == null) { return; }
        SharedPreferences settings = mContext.getSharedPreferences("TCUserInfo", Context.MODE_PRIVATE);
        mUserId = settings.getString("userid", "");
        mUserPwd = settings.getString("userpwd", "");
    }

    private void loadUserInfoAssistant() {
        //TODO: decrypt
        if (mContext == null) { return; }
        SharedPreferences settings = mContext.getSharedPreferences("TCUserInfo", Context.MODE_PRIVATE);
        mUserIdAssistant = settings.getString("useridassistant", "");
        mUserPwdAssistant = settings.getString("userpwdassistant", "");
    }

    private void saveUserInfo() {
        if (mContext == null) { return; }
        SharedPreferences settings = mContext.getSharedPreferences("TCUserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("accessToken", mAccessToken);
        editor.putString("userid", mUserId);
        editor.putString("userpwd", mUserPwd);
        editor.putString("nickName", mNickName);
        editor.putString("avatar", mUserAvatar);
        editor.apply();
    }

    private void saveUserInfoAssistant() {
        if (mContext == null) { return; }
        SharedPreferences settings = mContext.getSharedPreferences("TCUserInfoAssistant", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("useridassistant", mUserIdAssistant);
        editor.putString("userpwdassistant", mUserPwdAssistant);
        editor.putString("nickNameassistant", mNickNameAssistant);
        editor.putString("avatarassistant", mUserAvatarAssistant);
        editor.apply();
    }


    private void clearUserInfo() {
        if (mContext == null) { return; }
        SharedPreferences settings = mContext.getSharedPreferences("TCUserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    private void clearUserInfoAssistant() {
        if (mContext == null) { return; }
        SharedPreferences settings = mContext.getSharedPreferences("TCUserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    public void saveLiveForeshowInfo(ArrayList listLiveForeshow) {
        if (mContext == null) { return; }
        SharedPreferences settings = mContext.getSharedPreferences("TCLiveForeshowInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Gson gosn = new Gson();
        String result = gosn.toJson(listLiveForeshow);
        editor.putString("liveForeshow", result);
        editor.apply();
    }

    public void saveLiveGoodsInfo(ArrayList listGoods) {
        if (mContext == null) { return; }
        SharedPreferences settings = mContext.getSharedPreferences("TCLiveGoodsInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Gson gosn = new Gson();
        String result = gosn.toJson(listGoods);
        editor.putString("liveGoods", result);
        editor.apply();
    }

    public void saveLiveGoodsInfoAssistant(ArrayList listGoods) {
        if (mContext == null) { return; }
        SharedPreferences settings = mContext.getSharedPreferences("TCLiveGoodsInfoAssistant", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Gson gosn = new Gson();
        String result = gosn.toJson(listGoods);
        editor.putString("liveGoodsAssistant", result);
        editor.apply();
    }

    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      网络请求相关
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */

    /**
     * 发起网络请求注册账号
     *
     * @param userId
     * @param password
     * @param callback
     */
    public void register(final String userId, final String password, final HTTPMgr.Callback callback) {
        try {
            String pwd = Utils.md5(Utils.md5(password) + userId);
            JSONObject body = new JSONObject()
                    .put("userid", userId)
                    .put("password", pwd);
            HTTPMgr.getInstance().request(GlobalConfig.APP_SVR_URL + "/register", body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发起网络请求登录
     * <p>
     * 此方法为自动获取本地的 id 和 psw 进行自动登录
     *
     * @param callback
     */
    public void autoLogin(final HTTPMgr.Callback callback) {
        loginInternal(mUserId, mUserPwd, callback);

    }

    /**
     * 发起网络请求登录
     * <p>
     * 此方法为自动获取本地的 id 和 psw 进行自动登录
     *
     * @param callback
     */
    public void autoLoginAssistant(final HTTPMgr.Callback callback) {
        loginInternalAssistant(mUserIdAssistant, mUserPwdAssistant, callback);

    }

    /**
     * 发起网络请求登录
     *
     * @param userid
     * @param password
     * @param callback
     */
    public void login(final String userid, final String password, final HTTPMgr.Callback callback) {
        final String pwd = Utils.md5(Utils.md5(password) + userid);
        loginInternal(userid, password, callback);
    }

    /**
     * 发起网络请求登录助理账号
     *
     * @param userid
     * @param password
     * @param callback
     */
    public void loginAssistant(final String userid, final String password, final HTTPMgr.Callback callback) {
        loginInternalAssistant(userid, password, callback);
    }

    /**
     * 发起网络请求退出登录
     *
     * @param token
     * @param callback
     */
    public void loginout(final String token, final HTTPMgr.Callback callback) {
        loginoutInternal(token, callback);

    }

    /**
     * 发起网络请求退出登录助理账号
     *
     * @param token
     * @param callback
     */
    public void loginoutAssistant(final String token, final HTTPMgr.Callback callback) {
        loginoutInternalAssistant(token, callback);

    }

    /**
     * 主播具体的登录实现
     *
     * @param userId
     * @param pwd
     * @param callback
     */
    private void loginInternal(final String userId, final String pwd, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject()
                    .put("username", userId)
                    .put("password", pwd);
            HTTPMgr.getInstance().request(GlobalConfig.APP_SVR_URL + "/live/v1/anchor/login", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    mSdkAppID = GlobalConfig.SDKAPPID;          // sdkappId
                    mUserId = userId;
                    mUserPwd = pwd;
                    int code = data.optInt("code");
                    String msg = data.optString("message");
                    final JSONObject retData = data.optJSONObject("result");
                    if (code == 0 && retData != null) {
                        mAccessToken = retData.optString("access_token");
                        HTTPMgr.getInstance().setToken(mAccessToken);
                        HTTPMgr.getInstance().setUserIdAndToken(mUserId, mAccessToken);
                        UserMgr.getInstance().fetchUserInfo(new HTTPMgr.Callback() {
                            @Override
                            public void onSuccess(JSONObject data) {
                                if (callback != null) {
                                    callback.onSuccess(retData);
                                }
                            }
                            @Override
                            public void onFailure(int code, final String msg) {
                                Log.i(TAG, "onError: errorCode = " + code + " info = " + msg);
                            }
                        });
                    } else {
                        String errorMsg = msg;
                        if (code == 620) {
                            errorMsg = "用户不存在";
                        } else if (code == 402) {
                            errorMsg = "密码错误";
                        }
                        if (callback != null) {
                            callback.onFailure(code, errorMsg);
                        }
                        clearUserInfo();
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                    clearUserInfo();
                }
            });
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 主播具体的退出登录实现
     * @param token
     * @param callback
     */

    private void loginoutInternal(final String token, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject();
            HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL + "/live/v1/anchor/logout", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    int code = data.optInt("code");
                    String msg = data.optString("message");
                    final JSONObject retData = data.optJSONObject("result");
                    if (code == 0 ) {
                        if (callback != null) { callback.onSuccess(retData); }
                    } else {
                        if (callback != null) { callback.onFailure(code, msg); }
                    }
                    logout();
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        logout();
                        callback.onFailure(code, msg);
                    }
                }
            });
        }
        catch (Exception e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 主播推流
     * @param title
     * @param imgUrl
     * @param record
     * @param callback
     */
    public void anchorPush(final String title, final String imgUrl, final String record, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject()
                    .put("title", title)
                    .put("img_url", imgUrl)
                    .put("record", record);
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL + "/live/v1/live/begin", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    if (data != null) {
                        Log.e("TCUserMgr", "onSuccess: 主播推流" +data);
                    }
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        }
        catch (Exception e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 助理拉流
     * @param title
     * @param imgUrl
     * @param record
     * @param callback
     */
    public void assistantPull(final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject();
            HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL + "/live/v1/assistant/live", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    if (data != null) {
                        Log.e("TCUserMgr", "onSuccess: 助理拉流" +data);
                    }
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        }
        catch (Exception e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 助理具体的登录实现
     *
     * @param userId
     * @param pwd
     * @param callback
     */
    private void loginInternalAssistant(final String userId, final String pwd, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject()
                    .put("username", userId)
                    .put("password", pwd);
            HTTPMgr.getInstance().request(GlobalConfig.APP_SVR_URL + "/live/v1/assistant/login", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    mSdkAppID = GlobalConfig.SDKAPPID;          // sdkappId
                    mUserIdAssistant = userId;
                    mUserPwdAssistant = pwd;
                    int code = data.optInt("code");
                    String msg = data.optString("message");
                    final JSONObject retData = data.optJSONObject("result");
                    if (code == 0 && retData != null) {
                        mAccessTokenAssistant = retData.optString("access_token");
                        HTTPMgr.getInstance().setToken(mAccessTokenAssistant);
                        HTTPMgr.getInstance().setUserIdAndTokenAssistant(mUserIdAssistant, mAccessTokenAssistant);
                        UserMgr.getInstance().fetchUserInfoAssistant(new HTTPMgr.Callback() {
                            @Override
                            public void onSuccess(JSONObject data) {
                                if (callback != null) {
                                    callback.onSuccess(retData);
                                }
                            }
                            @Override
                            public void onFailure(int code, final String msg) {
                                Log.i(TAG, "onError: errorCode = " + code + " info = " + msg);
                            }
                        });
                    } else {
                        String errorMsg = msg;
                        if (code == 620) {
                            errorMsg = "用户不存在";
                        } else if (code == 402) {
                            errorMsg = "密码错误";
                        }
                        if (callback != null) {
                            callback.onFailure(code, errorMsg);
                        }
                        clearUserInfoAssistant();
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                    clearUserInfoAssistant();
                }
            });
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 助理具体的退出登录实现
     * @param token
     * @param callback
     */

    private void loginoutInternalAssistant(final String token, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject();
            HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL + "/live/v1/assistant/logout", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    int code = data.optInt("code");
                    String msg = data.optString("message");
                    final JSONObject retData = data.optJSONObject("result");
                    if (code == 0 ) {
                        if (callback != null) { callback.onSuccess(retData); }
                    } else {
                        if (callback != null) { callback.onFailure(code, msg); }
                    }
                    logoutAssistant();
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        logoutAssistant();
                        callback.onFailure(code, msg);
                    }
                }
            });
        }
        catch (Exception e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 获取直播预告列表
     *
     * @param callback
     */
    public void fetchLiveForeshowList(final int page, final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL+"/live/v1/foreshow/list?page="+page, body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    Log.e("TCUserMgr", "onSuccess: 获取直播预告列表" +data);
                }
                if (callback != null) {
                    //saveLiveGoodsInfo(list);
                    callback.onSuccess(data);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 分享直播间信息
     *
     * @param callback
     */
    public void shareLiveRoom(final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL+"/live/v1/live/share", body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    Log.e("TCUserMgr", "onSuccess: 分享直播间信息" +data);
                }
                if (callback != null) {
                    //saveLiveGoodsInfo(list);
                    callback.onSuccess(data);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 获取直播预告详情
     *
     * @param callback
     */
    public void fetchLiveForeshow(final int id, final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL+"/live/v1/foreshow/edit?id="+id, body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    Log.e("TCUserMgr", "onSuccess: 获取直播预告详情" +data);
                }
                if (callback != null) {
                    //saveLiveGoodsInfo(list);
                    callback.onSuccess(data);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 更新直播预告
     *
     * @param data
     * @param callback
     */
    public void updateLiveForeshow(final LiveForeshowBean data, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject()
                    .put("id", data.getId())
                    .put("title", data.getTitle())
                    .put("introduce", data.getInfo())
                    .put("img_url", data.getImage())
                    .put("start_at", data.getDate())
                    .put("goodsids", data.getGoodsList());
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL+"/live/v1/foreshow/update", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        if (data != null && data.getInt("code") == 0) {
                            Log.e("TCUserMgr", "onSuccess: 更新直播预告" +data);
                        }
                        if (callback != null) {
                            callback.onSuccess(data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 添加直播预告
     *
     * @param data
     * @param callback
     */
    public void addLiveForeshow(final LiveForeshowBean data, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject()
                    .put("title", data.getTitle())
                    .put("introduce", data.getInfo())
                    .put("img_url", data.getImage())
                    .put("start_at", data.getDate())
                    .put("goodsids", data.getGoodsList());
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL+"/live/v1/foreshow/add", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        if (data != null && data.getInt("code") == 0) {
                            Log.e("TCUserMgr", "onSuccess: 添加直播预告" +data);
                        }
                        if (callback != null) {
                            callback.onSuccess(data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 删除直播预告
     *
     * @param id
     * @param callback
     */
    public void deleteLiveForeshow(final int id, final HTTPMgr.Callback callback) {
        try {
            JSONObject body  = new JSONObject()
                    .put("id",id);
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL+"/live/v1/foreshow/delete", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    if (data != null) {
                        Log.e("TCUserMgr", "onSuccess: 删除直播预告" +data);
                    }
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 强制关闭直播
     *
     * @param callback
     */
    public void closedLive(final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL+"/live/v1/live/close", body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    Log.e("TCUserMgr", "onSuccess: 强制关闭直播" +data);
                }
                if (callback != null) {
                    callback.onSuccess(data);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    Log.e("TCUserMgr", "onFailure: 强制关闭直播" +msg+code);
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 获取真实观看人数
     *
     * @param callback
     */
    public void fetchViewsNumber(final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL+"/live/v1/live/audience", body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                }
                if (callback != null) {
                    Log.e("TCUserMgr", "onSuccess: 获取真实观看人数" +data);
                    callback.onSuccess(data);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    Log.e("TCUserMgr", "onFailure: 获取真实观看人数" +msg+code);
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 上传图片
     *
     * @param image
     * @param callback
     */
    public void uploadImage(final String image, final HTTPMgr.Callback callback) {
        HTTPMgr.getInstance().requestWithUrlPostImage(GlobalConfig.APP_SVR_URL+"/live/v1/upload/image", image, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                try {
                    if (data != null && data.getInt("code") == 0) {
                        Log.e("TCUserMgr", "onSuccess: 上传图片" +data);
                    }
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 获取商品列表
     *
     * @param isRelevance //0为未关联,1为已关联,2为全部商品
     * @param callback
     */
    public void fetchGoodsList(int isRelevance, final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        String str = "";
        switch (isRelevance) {
            case 0 :
                str = "?check=0";
                break;
            case 1 :
                str = "?check=1";
                break;
            case 2 :
                str = "";
                break;
            default:
        }
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL+"/live/v1/goods/list"+str, body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    Log.e("TCUserMgr", "onSuccess: 获取商品列表" +data);
                }
                if (callback != null) {
                    callback.onSuccess(data);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 添加直播商品
     *
     * @param ids
     * @param callback
     */
    public void addLiveGoods(final ArrayList <String> ids, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject()
                .put("ids", ids);
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL+"/live/v1/goods/save", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        if (data != null && data.getInt("code") == 0) {
                            Log.e("TCUserMgr", "onSuccess: 添加直播商品" +data);
                        }
                        if (callback != null) {
                            callback.onSuccess(data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 删除直播商品
     *
     * @param ids
     * @param callback
     */
    public void deleteLiveGoods(final ArrayList<String> ids, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject().put("ids", ids);
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL+"/live/v1/goods/delete", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        if (data != null && data.getInt("code") == 0) {
                            Log.e("TCUserMgr", "onSuccess: 删除直播商品" +data);
                            if (callback != null) {
                                callback.onSuccess(data);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 更新直播悬浮商品
     *
     * @param id
     * @param callback
     */
    public void updateLiveGoods(final int id, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject().put("id", id);
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL+"/live/v1/goods/showSave", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        if (data != null && data.getInt("code") == 0) {
                            Log.e("TCUserMgr", "onSuccess: 更新直播悬浮商品" +data);
                        }
                        if (callback != null) {
                            callback.onSuccess(data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 获取助理列表
     *
     * @param callback
     */
    public void fetchAssistantList(final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL+"/live/v1/assistan/get", body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    Log.e("TCUserMgr", "onSuccess: 获取助理列表" +data);
                }
                if (callback != null) {
                    //saveLiveGoodsInfo(list);
                    callback.onSuccess(data);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 获取历史回放列表
     *
     * @param callback
     */
    public void fetchPlayBackList(final int page, final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL+"/live/v1/live/playback?page="+page, body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    Log.e("TCUserMgr", "onSuccess: 获取历史回放列表" +data);
                }
                if (callback != null) {
                    //saveLiveGoodsInfo(list);
                    callback.onSuccess(data);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 删除历史回放
     *
     * @param id
     * @param callback
     */
    public void deletePlayBack(final int id, final HTTPMgr.Callback callback) {
        try {
            JSONObject body  = new JSONObject()
                    .put("id",id);
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL+"/live/v1/live/playback/delete", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    if (data != null) {
                        Log.e("TCUserMgr", "onSuccess: 删除历史回放" +data);
                    }
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发布抽奖
     * @param title
     * @param prize
     * @param number
     * @param response
     * @param callback
     */
    public void addLottery(final String title, final String prize, final String number, final String response, final HTTPMgr.Callback callback) {
        try {
            JSONObject body  = new JSONObject()
                    .put("title",title)
                    .put("prize",prize)
                    .put("number",number)
                    .put("response",response);
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL+"/live/v1/assistant/raffles/create", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    if (data != null) {
                        Log.e("TCUserMgr", "onSuccess: 发起抽奖" +data);
                    }
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取中奖名单列表
     *
     * @param callback
     */
    public void fetchLotteryList(final int page, final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL+"/live/v1/assistant/raffles/prizewinner?page="+page, body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    Log.e("TCUserMgr", "onSuccess: 获取中奖名单列表" +data);
                }
                if (callback != null) {
                    callback.onSuccess(data);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 开奖
     *
     * @param rafflesId
     * @param callback
     */
    public void openLottery(final String rafflesId, final HTTPMgr.Callback callback) {
        try {
            JSONObject body  = new JSONObject()
                    .put("raffles_id",rafflesId);
            HTTPMgr.getInstance().request(GlobalConfig.APP_SVR_URL+"/live/v1/assistant/raffles/result", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    if (data != null) {
                        Log.e("TCUserMgr", "onSuccess: 开奖" +data);
                    }
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取禁言列表
     *
     * @param callback
     */
    public void fetchBannedList(final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL+"/live/v1/assistant/banned/list", body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    Log.e("TCUserMgr", "onSuccess: 获取禁言列表" +data);
                }
                if (callback != null) {
                    //saveLiveGoodsInfo(list);
                    callback.onSuccess(data);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 设置禁言
     *
     * @param accid
     * @param roomid
     * @param callback
     */
    public void setBanned(final String accid, final String roomid, final HTTPMgr.Callback callback) {
        try {
            JSONObject body  = new JSONObject()
                    .put("accid",accid)
                    .put("roomid",roomid);
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL+"/live/v1/assistant/banned/set", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    if (data != null) {
                        Log.e("TCUserMgr", "onSuccess: 取消禁言" +data);
                    }
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 取消禁言
     *
     * @param id
     * @param callback
     */
    public void cancelBanned(final int id, final HTTPMgr.Callback callback) {
        try {
            JSONObject body  = new JSONObject()
                    .put("id",id);
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL+"/live/v1/assistant/banned/cancel", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    if (data != null) {
                        Log.e("TCUserMgr", "onSuccess: 取消禁言" +data);
                    }
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取公告详情
     *
     * @param callback
     */
    public void fetchAnnunciateList(final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL+"/live/v1/assistant/notice/details", body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    Log.e("TCUserMgr", "onSuccess: 获取公告详情" +data);
                }
                if (callback != null) {
                    callback.onSuccess(data);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 更新公告
     *
     * @param notice
     * @param callback
     */
    public void updateAnnunciate(final String notice, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject().put("notice", notice);
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL+"/live/v1/assistant/notice/update", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        if (data != null && data.getInt("code") == 0) {
                            Log.e("TCUserMgr", "onSuccess: 更新公告" +data);
                        }
                        if (callback != null) {
                            callback.onSuccess(data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                    if (callback != null) {
                        callback.onFailure(code, msg);
                    }
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(-1, "");
            }
        }
    }

    /**
     * 获取主播用户的信息
     *
     * @param callback
     */
    public void fetchUserInfo(final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL + "/live/v1/anchor/detail", body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    mNickName = data.optJSONObject("result").optJSONObject("data").optString("username");
                    mUserAvatar = data.optJSONObject("result").optJSONObject("data").optString("img_url");
                }
                if (callback != null) {
                    callback.onSuccess(data);
                }
                saveUserInfo();
            }

            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 更新主播用户信息
     *
     * @param callback
     */
    public void uploadUserInfo(final String avatarUrl, final String nickName, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject()
                    .put("username", nickName != null ? nickName : "")
                    .put("img_url", avatarUrl != null ? avatarUrl : "");
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL + "/live/v1/anchor/modify", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    mUserAvatar = avatarUrl;
                    mNickName = nickName;
                    saveUserInfo();
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                }

                @Override
                public void onFailure(int code, String msg) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取助理用户的信息
     *
     * @param callback
     */
    public void fetchUserInfoAssistant(final HTTPMgr.Callback callback) {
        JSONObject body = new JSONObject();
        HTTPMgr.getInstance().requestWithUrlGet(GlobalConfig.APP_SVR_URL + "/live/v1/assistant/detail", body, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    mNickNameAssistant = data.optJSONObject("result").optJSONObject("data").optString("username");
                    mUserAvatarAssistant = data.optJSONObject("result").optJSONObject("data").optString("img_url");
                }
                if (callback != null) {
                    callback.onSuccess(data);
                }
                saveUserInfoAssistant();
            }

            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        });
    }

    /**
     * 更新助理用户信息
     *
     * @param callback
     */
    public void uploadUserInfoAssistant(final String avatarUrl, final String nickName, final HTTPMgr.Callback callback) {
        try {
            JSONObject body = new JSONObject()
                    .put("username", nickName != null ? nickName : "")
                    .put("img_url", avatarUrl != null ? avatarUrl : "");
            HTTPMgr.getInstance().requestWithUrlPost(GlobalConfig.APP_SVR_URL + "/live/v1/assistant/modify", body, new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    mUserAvatarAssistant = avatarUrl;
                    mNickNameAssistant = nickName;
                    saveUserInfoAssistant();
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                }
                @Override
                public void onFailure(int code, String msg) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
