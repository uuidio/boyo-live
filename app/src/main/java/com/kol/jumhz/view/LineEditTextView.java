package com.kol.jumhz.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kol.jumhz.R;
import com.kol.jumhz.common.utils.Constants;
import com.kol.jumhz.common.utils.Utils;

/**
 * @ClassName: LineEditTextView
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 15:15
 * @Description: 文本修改控件，对控件EditText的简单封装，可以用来修改文本，并显示相关信息
 */
public class LineEditTextView extends LinearLayout {
    private String name;
    private boolean isBottom;
    private String content;
    private EditText contentEditView;

    public LineEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.layout_view_line_edit_text, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TCLineView, 0, 0);
        try {
            name = ta.getString(R.styleable.TCLineView_name);
            content = ta.getString(R.styleable.TCLineView_content1);
            isBottom = ta.getBoolean(R.styleable.TCLineView_isBottom, false);
            setUpView();

            //昵称长度限制
            filterLength(Constants.NICKNAME_MAX_LEN, "昵称长度不能超过" + Constants.NICKNAME_MAX_LEN / 2);
            contentEditView.setOnClickListener(v -> contentEditView.setCursorVisible(true));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ta.recycle();
        }
    }

    private void setUpView() {
        TextView tvName = findViewById(R.id.ett_name);
        tvName.setText(name);
        contentEditView = findViewById(R.id.ett_content);
        contentEditView.setText(content);
        View bottomLine = findViewById(R.id.ett_bottomLine);
        bottomLine.setVisibility(isBottom ? VISIBLE : GONE);
    }


    /**
     * 设置EditText内容
     */
    public void setContent(String content) {
        contentEditView.setText(content);
        contentEditView.setSelection(contentEditView.getText().length());
    }

    /**
     * 获取EditText内容
     */
    public String getContent() {
        return contentEditView.getText().toString();
    }


    /**
     * contentEditView可输入最大长度限制检测
     *
     * @param max_length 可输入最大长度
     * @param err_msg    达到可输入最大长度时的提示语
     */
    private void filterLength(final int max_length, final String err_msg) {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(max_length) {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                int destLen = Utils.getCharacterNum(dest.toString()); //获取字符个数(一个中文算2个字符)
                int sourceLen = Utils.getCharacterNum(source.toString());
                if (destLen + sourceLen > max_length) {
                    contentEditView.setError(err_msg);
                    return "";
                }
                return source;
            }
        };
        contentEditView.setFilters(filters);
    }

}
