package com.kol.jumhz.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kol.jumhz.R;
import com.kol.jumhz.common.assistant.AssistantBean;

import java.util.ArrayList;


/**
 * @ClassName: AssistantView
 * @Author: Dzy
 * @CreateDate: 2020-04-29 11:46
 * @Description:  助理列表适配器
 */
public class AssistantView extends RecyclerView.Adapter<AssistantView.ViewHolder> {
    public ArrayList<AssistantBean> list;
    private Context mContext;

    public AssistantView(Context mContent, ArrayList<AssistantBean> list){
        this.mContext = mContent;
        this.list= list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_assistant_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.item_tv_name.setText(list.get(position).getUsername());

    }

    @Override
    public int getItemCount() {
        if (list==null){
            return 0;
        }
        return list.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        TextView item_tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            item_tv_name=itemView.findViewById(R.id.tv_name);
        }
    }
}
