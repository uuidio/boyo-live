package com.kol.jumhz.livegoods;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.utils.T;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.publish.AddLiveActivity;
import com.kol.jumhz.view.LiveAddGoodsShowView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: TCAddLiveGoodsActivity
 * @Author: Dzy
 * @CreateDate: 2020-04-27 14:06
 * @Description: 添加商品
 */
public class AddLiveGoodsActivity extends BaseActivity {

    private ActivityTitle mTitle;
    private EditText etSearch;
    private ImageView ivSearchDelete;
    private RelativeLayout rlAll, rlAdd;
    private RecyclerView mlist;
    private CheckBox mCheckBox;
    private LiveAddGoodsShowView liveAddGoodsShowView;
    private ArrayList<LiveGoodsBean> list = new ArrayList<>();
    private ArrayList<LiveGoodsBean> listRetrun = new ArrayList<>();
    private SkeletonScreen skeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_live_goods);

        initUi();
        initData();
        initEvent();
        getData();
    }

    private void initUi() {
        mlist = findViewById(R.id.rv_goods);
        mTitle = findViewById(R.id.rl_title_bar);
        mCheckBox =findViewById(R.id.cb);
        rlAll = findViewById(R.id.rl_all);
        rlAdd = findViewById(R.id.rl_add);
        etSearch = findViewById(R.id.et_search);
        ivSearchDelete = findViewById(R.id.iv_search_delete);

        mlist.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        // 这里创建adapter的时候，构造方法参数传了一个接口对象，这很关键，回调接口中的方法来实现对过滤后的数据的获取
        liveAddGoodsShowView = new LiveAddGoodsShowView(this,list, list -> {
            // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
        });
        mlist.setAdapter(liveAddGoodsShowView);

        skeletonScreen = Skeleton.bind(mlist)
                //设置加载列表适配器 ，并且开启动画 设置光晕动画角度等 最后显示
                .adapter(liveAddGoodsShowView).shimmer(true).angle(0)
                .frozen(false)
                .color(R.color.whitesmoke)
                .duration(1000)
                .count(10)
                .load(R.layout.item_goods_skeleton)
                .show();
    }

    private void initData() {
    }

    private void initEvent() {
        //首次进入页面不弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // 监听条码输入框
        TextChange textChange = new TextChange();
        etSearch.addTextChangedListener(textChange);

        mTitle.setReturnListener(v -> finish());
        mTitle.setMoreListener(v -> {
            mCheckBox.setChecked(false);
            liveAddGoodsShowView.cancelAll();
        });

        ivSearchDelete.setOnClickListener(v -> {
            etSearch.setText("");
            liveAddGoodsShowView = new LiveAddGoodsShowView(getApplicationContext(),list, list -> {
                // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
            });
            mlist.setAdapter(liveAddGoodsShowView);
        });

        rlAll.setOnClickListener(v -> {
            if (mCheckBox.isChecked()) {
                mCheckBox.setChecked(false);
                liveAddGoodsShowView.cancelAll();
            } else {
                mCheckBox.setChecked(true);
                liveAddGoodsShowView.all();
            }
        });

        rlAdd.setOnClickListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.rl_add)) { return; }
            String activity = getIntent().getStringExtra("activity");
            ArrayList <String> listAdd = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isChecked()) {
                    listAdd.add(String.valueOf(list.get(i).getId()));
                    listRetrun.add(list.get(i));
                }
            }
            if (listRetrun.size() == 0 ) {
                TipDialog.show(AddLiveGoodsActivity.this, "请选择商品", TipDialog.TYPE.WARNING);
                return; }
            WaitDialog.show(this, "");
            if ("RelevanceLiveGoodsActivity".equals(activity)) {
                UserMgr.getInstance().addLiveGoods(listAdd,new HTTPMgr.Callback() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        Intent intent=new Intent(getApplicationContext(), RelevanceLiveGoodsActivity.class);
                        intent.putExtra("retrunData", listRetrun);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    @Override
                    public void onFailure(int code, final String msg) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> T.showShort(getApplicationContext(),msg + ":"+code));
                    }
                });
            } else {
                Intent intent=new Intent(getApplicationContext(), AddLiveActivity.class);
                intent.putExtra("retrunData", listRetrun);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    /**
     * @Description: handler 处理返回的请求结果
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            list = (ArrayList<LiveGoodsBean>) data.getSerializable("resultGoods");
            // TODO: 更新界面
            liveAddGoodsShowView = new LiveAddGoodsShowView(getApplicationContext(),list, list -> {
                // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
            });
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            skeletonScreen.hide();
            mlist.setAdapter(liveAddGoodsShowView);
        }
    };


    private void getData(){
        // TODO: http request.
        String activity = getIntent().getStringExtra("activity");
        Message msg = new Message();
        Bundle data1 = new Bundle();
        int num = !"RelevanceLiveGoodsActivity".equals(activity) ? 2 : 0;
        //判断页面是否为从关联商品页面跳转过来的
        UserMgr.getInstance().fetchGoodsList(num, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                List list = new ArrayList();
                for (int i = 0; i < data.optJSONObject("result").optJSONArray("lists").length(); i++) {
                    JSONObject result = data.optJSONObject("result").optJSONArray("lists").optJSONObject(i);
                    LiveGoodsBean liveGoodsBean = new LiveGoodsBean(
                            result.optInt("id"),
                            result.optString("goods_image"),
                            result.optString("goods_name"),
                            result.optString("goods_price"),
                            result.optInt("shop_id"),
                            result.optBoolean("live_check")
                    );
                    list.add(liveGoodsBean);
                }
                data1.putStringArrayList("resultGoods", (ArrayList<String>) list);
                msg.setData(data1);
                handler.sendMessage(msg);
            }
            @Override
            public void onFailure(int code, final String msg) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    skeletonScreen.hide();
                    TipDialog.show(AddLiveGoodsActivity.this, msg+":"+code, TipDialog.TYPE.WARNING);});
            }
        });
    }

    class TextChange implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean sign = etSearch.getText().length() > 0;
            if (sign) {
                ivSearchDelete.setVisibility(View.VISIBLE);
            } else {
                ivSearchDelete.setVisibility(View.INVISIBLE);
            }
            // 如果adapter不为空的话就根据编辑框中的内容来过滤数据
            if(liveAddGoodsShowView != null){
                liveAddGoodsShowView.getFilter().filter(s);
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void afterTextChanged(Editable s) { }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
