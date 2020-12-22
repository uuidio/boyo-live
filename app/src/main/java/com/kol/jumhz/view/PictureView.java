package com.kol.jumhz.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kol.jumhz.R;

import java.util.List;

/**
 * Created on 2020/12/10.
 *
 * @author Simon
 */
public class PictureView extends RecyclerView.Adapter {
    private Context context;
    private List<Object> mList;
    public PictureView(Context context,List<Object> list)
    {
        this.context=context;
        this.mList=list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_picture,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 12;
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder
    {

        private ImageView imPicOk,image,imView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image= itemView.findViewById(R.id.image);
            imView=itemView.findViewById(R.id.view);
            imPicOk=itemView.findViewById(R.id.pic_ok);
        }
    }
}
