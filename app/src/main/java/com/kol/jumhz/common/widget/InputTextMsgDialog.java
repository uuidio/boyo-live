package com.kol.jumhz.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kol.jumhz.R;

/**
 * @ClassName: InputTextMsgDialog
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:13
 * @Description: 观众、主播的弹幕或普通文本的输入框
 */
public class InputTextMsgDialog extends Dialog {

    public interface OnTextSendListener {

       void onTextSend(String msg, boolean tanmuOpen);
    }

    private EditText messageTextView;
    private Context mContext;
    private InputMethodManager imm;
    private int mLastDiff = 0;
    private OnTextSendListener mOnTextSendListener;
    private boolean mDanmuOpen = false;

    public InputTextMsgDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        setContentView(R.layout.dialog_input_text);

        messageTextView = findViewById(R.id.et_input_message);
        messageTextView.setInputType(InputType.TYPE_CLASS_TEXT);
        //修改下划线颜色
        messageTextView.getBackground().setColorFilter(context.getResources().getColor(R.color.transparent), PorterDuff.Mode.CLEAR);


        TextView confirmBtn = findViewById(R.id.confrim_btn);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        confirmBtn.setOnClickListener(view -> {
            String msg = messageTextView.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {

                mOnTextSendListener.onTextSend(msg, mDanmuOpen);
                imm.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED);
                imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                messageTextView.setText("");
                dismiss();
            } else {
                Toast.makeText(mContext, "还没想好发什么吗？", Toast.LENGTH_LONG).show();
            }
            messageTextView.setText(null);
        });

        final Button barrageBtn = findViewById(R.id.barrage_btn);
        barrageBtn.setOnClickListener(view -> {
            mDanmuOpen = !mDanmuOpen;
            if (mDanmuOpen) {
                barrageBtn.setBackgroundResource(R.drawable.barrage_slider_on);
            } else {
                barrageBtn.setBackgroundResource(R.drawable.barrage_slider_off);
            }
        });

        LinearLayout mBarrageArea = findViewById(R.id.barrage_area);
        mBarrageArea.setOnClickListener(v -> {
            mDanmuOpen = !mDanmuOpen;
            if (mDanmuOpen) {
                barrageBtn.setBackgroundResource(R.drawable.barrage_slider_on);
            } else {
                barrageBtn.setBackgroundResource(R.drawable.barrage_slider_off);
            }
        });

        messageTextView.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId) {
                case KeyEvent.KEYCODE_ENDCALL:
                case KeyEvent.KEYCODE_ENTER:
                    if (messageTextView.getText().length() > 0) {
//                            mOnTextSendListener.onTextSend("" + messageTextView.getText(), mDanmuOpen);
                        //sendText("" + messageTextView.getText());
                        //imm.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED);
                        imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
//                            messageTextView.setText("");
                        dismiss();
                    } else {
                        Toast.makeText(mContext, "还没想好发什么吗？", Toast.LENGTH_LONG).show();
                    }
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    dismiss();
                    return false;
                default:
                    return false;
            }
        });

        LinearLayout mConfirmArea = findViewById(R.id.confirm_area);
        mConfirmArea.setOnClickListener(v -> {
            String msg = messageTextView.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                mOnTextSendListener.onTextSend(msg, mDanmuOpen);
                imm.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED);
                imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                messageTextView.setText("");
                dismiss();
            } else {
                Toast.makeText(mContext, "还没想好发什么吗？", Toast.LENGTH_LONG).show();
            }
            messageTextView.setText(null);
        });

        messageTextView.setOnKeyListener((view, i, keyEvent) -> {
            Log.d("My test", "onKey " + keyEvent.getCharacters());
            return false;
        });

        RelativeLayout rlDlg = findViewById(R.id.rl_outside_view);
        rlDlg.setOnClickListener(v -> {
            if(v.getId() != R.id.rl_inputdlg_view) { dismiss(); }
        });

        final LinearLayout rldlgview = findViewById(R.id.rl_inputdlg_view);

        rldlgview.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> {
            Rect r = new Rect();
            //获取当前界面可视部分
            getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            //获取屏幕的高度
            int screenHeight =  getWindow().getDecorView().getRootView().getHeight();
            //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
            int heightDifference = screenHeight - r.bottom;

            if (heightDifference <= 0 && mLastDiff > 0){
                //imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                dismiss();
            }
            mLastDiff = heightDifference;
        });
        rldlgview.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
            dismiss();
        });
    }

    public void setmOnTextSendListener(OnTextSendListener onTextSendListener) {
        this.mOnTextSendListener = onTextSendListener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //dismiss之前重置mLastDiff值避免下次无法打开
        mLastDiff = 0;
    }

    @Override
    public void show() {
        super.show();
    }
}
