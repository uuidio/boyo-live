package com.kol.jumhz.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kol.jumhz.R;

import java.util.List;

/**
 * Created on 2020/12/10.
 *
 * @author Simon
 */
public class LiveListView extends RecyclerView.Adapter {
    private Context context;
    private List<Object> mList;
    private int screenWidth;
    private int allMargin = 0;

    public LiveListView(Context context, List<Object> list, int screenWidth) {
        this.context = context;
        this.mList = list;
        this.screenWidth = screenWidth;
        allMargin = (int) (context.getResources().getDimension(R.dimen.w_32px_port) * 4 + context.getResources().getDimension(R.dimen.w_29px_port) * 2);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_live_list, null);
        params.height = (int) (((screenWidth - allMargin) / 3) * 0.6);
        MyViewHolder holder = new MyViewHolder(view);
        holder.rlview.setLayoutParams(params);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        holder.llView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView tvStatus;
        private RelativeLayout rlview;
        private LinearLayout llView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tvStatus = itemView.findViewById(R.id.tv_status);
            rlview = itemView.findViewById(R.id.rlview);
            llView = itemView.findViewById(R.id.ll_view);

        }
    }

    private ItemClickListener listener;

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }
}
