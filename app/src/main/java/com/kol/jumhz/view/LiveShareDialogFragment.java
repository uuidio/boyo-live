package com.kol.jumhz.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.R;
import com.kol.jumhz.common.utils.ButtonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: LiveShareDialogFragment
 * @Author: LanLnk
 * @CreateDate: 2020-04-30 10:57
 * @Description: 直播间分享海报Dialog
 */
public class LiveShareDialogFragment extends android.app.DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置背景透明
        getDialog().getWindow().setGravity(Gravity.TOP);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog mDetailDialog = new Dialog(getActivity(), R.style.sharedialog);
        mDetailDialog.setContentView(R.layout.fragment_dialog_live_share);
        ImmersionBar.with(getActivity()).init();
        mDetailDialog.setCancelable(false);

        RelativeLayout rlMain = mDetailDialog.findViewById(R.id.rl);
        ImageView ivImg1 = mDetailDialog.findViewById(R.id.iv_img1);
        ImageView ivImg2 = mDetailDialog.findViewById(R.id.iv_img2);
        ImageView ivImg3 = mDetailDialog.findViewById(R.id.iv_img3);
        TextView tvName = mDetailDialog.findViewById(R.id.tv_name);
        TextView tvInfo = mDetailDialog.findViewById(R.id.tv_info);

        ImageView ivClose = mDetailDialog.findViewById(R.id.btn_close);
        ivClose.setOnClickListener(v -> mDetailDialog.dismiss());


        Glide.with(getActivity()).load(getArguments().getString("img1")).into(ivImg1);
        Glide.with(getActivity()).load(getArguments().getString("img2")).into(ivImg2);
        Glide.with(getActivity()).load(getArguments().getString("img3")).into(ivImg3);

        //确认则显示观看detail
        tvName.setText(getArguments().getString("name"));
        tvInfo.setText(getArguments().getString("info"));

        Button tvSave = mDetailDialog.findViewById(R.id.btn_save);
        tvSave.setOnClickListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.btn_save)) { return; }
            saveBitmapFromView(rlMain);
            mDetailDialog.dismiss();
        });

        return mDetailDialog;
    }

    public void saveBitmapFromView(View view) {
        int w = view.getWidth();
        int h = view.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        view.layout(0, 0, w, h);
        view.draw(c);
        // 缩小图片
        Matrix matrix = new Matrix();
        matrix.postScale(1f,1f); //长和宽放大缩小的比例
        bmp = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        saveBitmap(bmp,format.format(new Date())+".JPEG");
    }

    /**
     *
     * @param bitmap
     * @param bitName
     * 保存文件，文件名为当前日期
     */
    public void saveBitmap(Bitmap bitmap, String bitName){
        String fileName ;
        File file ;
        if(Build.BRAND .equals("Xiaomi") ){ // 小米手机
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/"+bitName ;
        }else{ // Meizu 、Oppo
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+bitName ;
        }
        file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
                // 插入图库
                MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), file.getAbsolutePath(), bitName, null);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        // 发送广播，通知刷新图库的显示
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        Toast.makeText(getActivity(), "已保存到图库", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
