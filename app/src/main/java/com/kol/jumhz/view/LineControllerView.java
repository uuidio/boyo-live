package com.kol.jumhz.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kol.jumhz.R;
import com.kol.jumhz.common.utils.Constants;
import com.kol.jumhz.common.utils.Utils;

/**
 * @ClassName: LineControllerView
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:14
 * @Description: 设置等页面条状控制或显示信息的控件
 */
public class LineControllerView extends LinearLayout {

    private String name;
    private boolean isBottom;
    private String content;
    private boolean canNav;
    private boolean isSwitch;

    public LineControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_line_controller, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TCLineView, 0, 0);
        try {
            name = ta.getString(R.styleable.TCLineView_name);
            content = ta.getString(R.styleable.TCLineView_content1);
            isBottom = ta.getBoolean(R.styleable.TCLineView_isBottom, false);
            canNav = ta.getBoolean(R.styleable.TCLineView_canNav,false);
            isSwitch = ta.getBoolean(R.styleable.TCLineView_isSwitch,false);
            setUpView();
        } finally {
            ta.recycle();
        }
    }


    private void setUpView(){
        TextView tvName = findViewById(R.id.ctl_name);
        tvName.setText(name);
        TextView tvContent = findViewById(R.id.ctl_content);
        tvContent.setText(Utils.getLimitString(content, Constants.USER_INFO_MAXLEN));
        View bottomLine = findViewById(R.id.ctl_bottomLine);
        bottomLine.setVisibility(isBottom ? VISIBLE : GONE);
        ImageView navArrow = findViewById(R.id.ctl_rightArrow);
        navArrow.setVisibility(canNav ? VISIBLE : GONE);
        LinearLayout contentPanel = findViewById(R.id.ctl_contentText);
        contentPanel.setVisibility(isSwitch ? GONE : VISIBLE);
        Switch switchPanel = findViewById(R.id.ctl_btnSwitch);
        switchPanel.setVisibility(isSwitch?VISIBLE:GONE);
    }


    /**
     * 设置文字内容
     *
     * @param content 内容
     */
    public void setContent(String content){
        this.content = content;
        TextView tvContent = findViewById(R.id.ctl_content);
        tvContent.setText(Utils.getLimitString(content, Constants.USER_INFO_MAXLEN));
    }

    /**
     * 获取内容
     *
     */
    public String getContent() {
        TextView tvContent = findViewById(R.id.ctl_content);
        return tvContent.getText().toString();
    }
}
