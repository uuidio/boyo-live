package com.kol.jumhz.view;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.kol.jumhz.R;
import com.kol.jumhz.common.assistant.BannedBean;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.utils.Utils;
import com.kol.jumhz.common.net.UserMgr;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @ClassName: BannedView
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:10
 * @Description: 助理端禁言列表适配器
 */
public class BannedView extends RecyclerView.Adapter<BannedView.ViewHolder> {
    public ArrayList<BannedBean> list;
    private AppCompatActivity mContext;

    public BannedView(AppCompatActivity mContent, ArrayList<BannedBean> list){
        this.mContext = mContent;
        this.list= list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_banned_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Utils.showPicWithUrl(mContext, holder.item_iv_banned_head, list.get(position).getHeadimgurl(), R.drawable.ic_camera_download_bg);
        holder.item_tv_banned_name.setText(list.get(position).getNickname());

        holder.item_rl_relieve.setOnClickListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.rl_relieve)) { return; }
            WaitDialog.show(mContext,"");
            UserMgr.getInstance().cancelBanned(list.get(holder.getAdapterPosition()).getId(),new HTTPMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        TipDialog.show(mContext,"", TipDialog.TYPE.SUCCESS);
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(0,list.size());
                    });
                }
                @Override
                public void onFailure(int code, final String msg) { }
            });
        });
    }

    @Override
    public int getItemCount() {
        if (list==null){
            return 0;
        }
        return list.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView item_iv_banned_head;
        TextView item_tv_banned_name;
        RelativeLayout item_rl_relieve;

        public ViewHolder(View itemView) {
            super(itemView);
            item_iv_banned_head =itemView.findViewById(R.id.iv_banned_head);
            item_tv_banned_name =itemView.findViewById(R.id.tv_banned_name);
            item_rl_relieve =itemView.findViewById(R.id.rl_relieve);
        }
    }
}
