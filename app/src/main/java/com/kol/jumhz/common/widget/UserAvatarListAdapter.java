package com.kol.jumhz.common.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.kol.jumhz.R;
import com.kol.jumhz.common.msg.SimpleUserInfo;
import com.kol.jumhz.common.utils.Utils;

import java.util.LinkedList;

/**
 * @ClassName: UserAvatarListAdapter
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:16
 * @Description: 直播头像列表适配器
 */
public class UserAvatarListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LinkedList<SimpleUserInfo> mUserAvatarList;
    private Context mContext;
    private String mPusherId; //主播id
    private final static int TOP_STORGE_MEMBER = 50; //最大容纳量

    public UserAvatarListAdapter(Context context, String pusherId) {
        this.mContext = context;
        this.mPusherId = pusherId;
        this.mUserAvatarList = new LinkedList<>();
    }

    /**
     * 添加用户信息
     * @param userInfo 用户基本信息
     * @return 存在重复或头像为主播则返回false
     */
    public boolean addItem(SimpleUserInfo userInfo) {

        if(userInfo == null) { return false; }

        //去除主播头像
        if(userInfo.userid.equals(mPusherId)) { return false; }

        //去重操作
        for (SimpleUserInfo simpleUserInfo : mUserAvatarList) {
            if(simpleUserInfo.userid.equals(userInfo.userid)) { return false; }
        }

        //始终显示新加入item为第一位
        mUserAvatarList.add(0, userInfo);
        //超出时删除末尾项
        //if(mUserAvatarList.size() > TOP_STORGE_MEMBER) {
        //    mUserAvatarList.remove(TOP_STORGE_MEMBER);
        //    notifyItemRemoved(TOP_STORGE_MEMBER);
        //}
        notifyItemInserted(0);
        return true;
    }

    public void removeItem(String userId) {
        SimpleUserInfo tempUserInfo = null;

        for(SimpleUserInfo userInfo : mUserAvatarList) {
            if(userInfo.userid.equals(userId)) { tempUserInfo = userInfo; }
        }

        if(null != tempUserInfo) {
            mUserAvatarList.remove(tempUserInfo);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_user_avatar, parent, false);

        final AvatarViewHolder avatarViewHolder = new AvatarViewHolder(view);
        avatarViewHolder.ivAvatar.setOnClickListener(view1 -> {
            SimpleUserInfo userInfo = mUserAvatarList.get(avatarViewHolder.getAdapterPosition());
            Toast.makeText(mContext.getApplicationContext(),"当前点击用户： " + userInfo.userid, Toast.LENGTH_SHORT).show();
        });

        return avatarViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Utils.showPicWithUrl(mContext, ((AvatarViewHolder)holder).ivAvatar,mUserAvatarList.get(position).avatar,
                R.drawable.ic_camera_download_bg);

    }

    @Override
    public int getItemCount() {
        return mUserAvatarList != null? mUserAvatarList.size(): 0;
    }

    private static class AvatarViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;

        private AvatarViewHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.iv_avatar);
        }
    }
}
