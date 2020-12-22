package com.kol.jumhz.livegoods;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.kol.jumhz.BaseActivity;
import com.kol.jumhz.R;
import com.kol.jumhz.common.livegoods.LiveGoodsBean;
import com.kol.jumhz.common.net.HTTPMgr;
import com.kol.jumhz.common.net.UserMgr;
import com.kol.jumhz.common.utils.ButtonUtils;
import com.kol.jumhz.common.widget.ActivityTitle;
import com.kol.jumhz.view.RelevanceLiveGoodsShowNumView;
import com.kol.jumhz.view.RelevanceLiveGoodsShowView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @ClassName: TCRelevanceLiveGoodsActivity
 * @Author: Dzy
 * @CreateDate: 2020-04-27 14:08
 * @Description: 关联直播商品列表
 */
public class RelevanceLiveGoodsActivity extends BaseActivity implements View.OnClickListener  {

    private ActivityTitle mTitle, mTitleBrag;
    private RelativeLayout rlSearchDrag;
    private RecyclerView mlist;
    private CheckBox mCheckBox;
    private RelativeLayout rlAll;
    private TextView tvTips, tvAdd, tvDelete, tvSave, tvReset;
    private EditText etSearch;
    private ImageView ivSearchDelete;
    private LinearLayout llBottom, llBottomDrag;
    private RelevanceLiveGoodsShowView relevanceLiveGoodsShowView;
    private RelevanceLiveGoodsShowNumView relevanceLiveGoodsShowNumView;
    private ArrayList<LiveGoodsBean> list = new ArrayList<>();
    private SkeletonScreen skeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        setContentView(R.layout.activity_relevance_live_goods);


        initUi();
        initData();
        initEvent();
    }

    private void initUi() {
        mlist = findViewById(R.id.rv_goods);
        mTitle = findViewById(R.id.rl_title_bar);
        mTitleBrag = findViewById(R.id.rl_title_bar_brag);
        rlSearchDrag = findViewById(R.id.rl_search_drag);
        mCheckBox =findViewById(R.id.cb);
        rlAll =findViewById(R.id.rl_all);
        tvTips = findViewById(R.id.tv_tips);
        tvAdd = findViewById(R.id.tv_add);
        tvDelete = findViewById(R.id.tv_delete);
        tvSave = findViewById(R.id.tv_save);
        tvReset = findViewById(R.id.tv_reset);
        llBottom = findViewById(R.id.ll_bottom);
        llBottomDrag = findViewById(R.id.ll_bottom_drag);
        etSearch = findViewById(R.id.et_search);
        ivSearchDelete = findViewById(R.id.iv_search_delete);

    }

    private void initData() {
        UserMgr.getInstance().fetchGoodsList(1, new HTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                list.clear();
                for (int i = 0; i < data.optJSONObject("result").optJSONArray("lists").length(); i++) {
                    JSONObject result = data.optJSONObject("result").optJSONArray("lists").optJSONObject(i);
                    if (!result.optBoolean("live_check")) { continue; }
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
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    relevanceLiveGoodsShowView = new RelevanceLiveGoodsShowView(getApplicationContext(),list,  new FilterListener() {
                        @Override
                        public void getFilterData(ArrayList<LiveGoodsBean> list) {
                            // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    skeletonScreen.hide();
                    mlist.setAdapter(relevanceLiveGoodsShowView);
                });
            }
            @Override
            public void onFailure(int code, final String msg) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    skeletonScreen.hide();
                    TipDialog.show(RelevanceLiveGoodsActivity.this, msg+":"+code, TipDialog.TYPE.WARNING);});
            }
        });

    }

    private void initEvent() {
        ivSearchDelete.setOnClickListener(this);
        rlAll.setOnClickListener(this);
        tvAdd.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        tvReset.setOnClickListener(this);
        mTitle.setMoreListener(this);

        mlist.setLayoutManager(new GridLayoutManager(this,1));
        // 这里创建adapter的时候，构造方法参数传了一个接口对象，这很关键，回调接口中的方法来实现对过滤后的数据的获取
        relevanceLiveGoodsShowView = new RelevanceLiveGoodsShowView(this,list, list -> {
            // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
        });
        mlist.setAdapter(relevanceLiveGoodsShowView);

        skeletonScreen = Skeleton.bind(mlist)
                //设置加载列表适配器 ，并且开启动画 设置光晕动画角度等 最后显示
                .adapter(relevanceLiveGoodsShowView).shimmer(true).angle(0)
                .frozen(false).color(R.color.whitesmoke)
                .duration(1000).count(10)
                .load(R.layout.item_goods_skeleton).show();

        // 监听条码输入框
        TextChange textChange = new TextChange();
        etSearch.addTextChangedListener(textChange);

        mTitle.setReturnListener(v -> finish());

        //点击排序
        mTitle.setMoreListener(v -> {
            // 收起软键盘
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);

            etSearch.setFocusable(false);
            mTitle.setVisibility(View.INVISIBLE);
            mTitleBrag.setVisibility(View.VISIBLE);
            tvTips.setVisibility(View.INVISIBLE);

            rlSearchDrag.setVisibility(View.VISIBLE);
            rlSearchDrag.setClickable(true);

            llBottom.setVisibility(View.INVISIBLE);
            llBottom.setClickable(false);
            llBottomDrag.setVisibility(View.VISIBLE);
            llBottomDrag.setClickable(true);

            relevanceLiveGoodsShowNumView = new RelevanceLiveGoodsShowNumView(getApplicationContext(),list);
            mlist.setAdapter(relevanceLiveGoodsShowNumView);
            //helper.attachToRecyclerView(mlist);
        });
        //点击取消排序
        mTitleBrag.setMoreListener(v -> {
            //短时间多次点击
            if (ButtonUtils.isFastDoubleClick(R.id.ll_bottom_drag)) { return; }
            mTitle.setVisibility(View.VISIBLE);
            mTitleBrag.setVisibility(View.INVISIBLE);
            tvTips.setVisibility(View.VISIBLE);

            rlSearchDrag.setVisibility(View.INVISIBLE);
            rlSearchDrag.setClickable(false);
            llBottom.setVisibility(View.VISIBLE);
            llBottom.setClickable(true);
            llBottomDrag.setVisibility(View.INVISIBLE);
            llBottomDrag.setClickable(false);

            //搜索栏设置可获取焦点
            etSearch.setFocusable(true);
            etSearch.setFocusableInTouchMode(true);

            relevanceLiveGoodsShowView = new RelevanceLiveGoodsShowView(getApplicationContext(),list, list -> {
                // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
            });
            mlist.setAdapter(relevanceLiveGoodsShowView);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0x11) {
            if (resultCode == RESULT_OK) {
                ArrayList<LiveGoodsBean> listRetrun = (ArrayList<LiveGoodsBean>) data.getSerializableExtra("retrunData");
                assert listRetrun != null;
                list.addAll(listRetrun);
                //mlist.setLayoutManager(new GridLayoutManager(this,1));
                relevanceLiveGoodsShowView = new RelevanceLiveGoodsShowView(this,list, list -> {
                    // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
                });
                mlist.setAdapter(relevanceLiveGoodsShowView);
                TipDialog.show(RelevanceLiveGoodsActivity.this, "", TipDialog.TYPE.SUCCESS);
                //helper.attachToRecyclerView(mlist);
            }
        }
    }

    //handler 处理返回的请求结果
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            list = (ArrayList<LiveGoodsBean>) data.getSerializable("resultGoods");
            // TODO: 更新界面
            relevanceLiveGoodsShowView = new RelevanceLiveGoodsShowView(getApplicationContext(),list,  new FilterListener() {
                @Override
                public void getFilterData(ArrayList<LiveGoodsBean> list) {
                    // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
                }
            });
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            skeletonScreen.hide();
            mlist.setAdapter(relevanceLiveGoodsShowView);
            //helper.attachToRecyclerView(mlist);
        }
    };

    //新线程进行网络请求
    final Runnable runnable = () -> UserMgr.getInstance().fetchGoodsList(1, new HTTPMgr.Callback() {
        @Override
        public void onSuccess(JSONObject data) {
            Message msg = new Message();
            Bundle data1 = new Bundle();
            ArrayList mlist = new ArrayList();
            for (int i = 0; i < data.optJSONObject("result").optJSONArray("lists").length(); i++) {
                JSONObject result = data.optJSONObject("result").optJSONArray("lists").optJSONObject(i);
                if (!result.optBoolean("live_check")) { continue; }
                LiveGoodsBean liveGoodsBean = new LiveGoodsBean(
                        result.optInt("id"),
                        result.optString("goods_image"),
                        result.optString("goods_name"),
                        result.optString("goods_price"),
                        result.optInt("shop_id"),
                        result.optBoolean("live_check")
                );
                mlist.add(liveGoodsBean);
            }
            data1.putSerializable("resultGoods", (ArrayList<String>) mlist);
            msg.setData(data1);
            handler.sendMessage(msg);
        }
        @Override
        public void onFailure(int code, final String msg) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                skeletonScreen.hide();
                TipDialog.show(RelevanceLiveGoodsActivity.this, msg+":"+code, TipDialog.TYPE.WARNING);});
        }
    });

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击清空搜索
            case R.id.iv_search_delete:
                etSearch.setText("");
                relevanceLiveGoodsShowView = new RelevanceLiveGoodsShowView(getApplicationContext(),list, list -> {
                    // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
                });
                mlist.setAdapter(relevanceLiveGoodsShowView);
                break;
            //点击全选
            case R.id.rl_all:
                if (mCheckBox.isChecked()) {
                    mCheckBox.setChecked(false);
                    relevanceLiveGoodsShowView.cancelAll();
                } else {
                    mCheckBox.setChecked(true);
                    relevanceLiveGoodsShowView.all();
                }
                break;
            //点击添加
            case R.id.tv_add:
                //短时间多次点击
                if (ButtonUtils.isFastDoubleClick(R.id.tv_add)) { return; }
                Intent intent=new Intent(getApplicationContext(), AddLiveGoodsActivity.class);
                intent.putExtra("activity", "RelevanceLiveGoodsActivity");
                startActivityForResult(intent, 0x11);
                break;
            //点击删除
            case R.id.tv_delete:
                //短时间多次点击
                if (ButtonUtils.isFastDoubleClick(R.id.tv_delete)) { return; }
                WaitDialog.show(RelevanceLiveGoodsActivity.this,"");
                ArrayList <String> listAdd = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isChecked()) {
                        listAdd.add(String.valueOf(list.get(i).getId()));
                    }
                }
                if (listAdd.size() == 0 ) {
                    TipDialog.show(RelevanceLiveGoodsActivity.this, "请选择商品", TipDialog.TYPE.WARNING);
                    return;
                }
                UserMgr.getInstance().deleteLiveGoods(listAdd,new HTTPMgr.Callback() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).isChecked()) {
                                    list.remove(i);
                                    i--;
                                }
                            }
                            TipDialog.show(RelevanceLiveGoodsActivity.this, "", TipDialog.TYPE.SUCCESS);
                            mCheckBox.setChecked(false);
                            relevanceLiveGoodsShowView.delete();
                        });
                    }
                    @Override
                    public void onFailure(int code, final String msg) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> TipDialog.show(RelevanceLiveGoodsActivity.this, msg+":"+code, TipDialog.TYPE.WARNING));
                    }
                });
                break;
            //点击完成排序
            case R.id.tv_save:
                //短时间多次点击
                if (ButtonUtils.isFastDoubleClick(R.id.tv_save)) { return; }
                ArrayList num;
                num = relevanceLiveGoodsShowNumView.getNum();

                mTitle.setVisibility(View.VISIBLE);
                mTitleBrag.setVisibility(View.INVISIBLE);
                tvTips.setVisibility(View.VISIBLE);

                rlSearchDrag.setVisibility(View.INVISIBLE);
                rlSearchDrag.setClickable(false);
                llBottom.setVisibility(View.VISIBLE);
                llBottom.setClickable(true);
                llBottomDrag.setVisibility(View.INVISIBLE);
                llBottomDrag.setClickable(false);

                //搜索栏设置可获取焦点
                etSearch.setFocusable(true);
                etSearch.setFocusableInTouchMode(true);

                //根据返回的num重新排序
                if (num.size() != 0) {
                    for (int i = num.size() -1; i >= 0; i--) {
                        for (int j = 0; j < list.size(); j++) {
                            if (Integer.parseInt(num.get(i).toString()) == list.get(j).getId()) {
                                list.add(0,list.get(j));
                                list.remove(j+1);
                                break;
                            }
                        }
                    }
                }

                relevanceLiveGoodsShowView = new RelevanceLiveGoodsShowView(getApplicationContext(),list, list -> {
                    // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
                });
                mlist.setAdapter(relevanceLiveGoodsShowView);
                //helper.attachToRecyclerView(null);
                break;
            //点击重置排序
            case R.id.tv_reset:
                //短时间多次点击
                if (ButtonUtils.isFastDoubleClick(R.id.tv_reset)) { return; }
                relevanceLiveGoodsShowNumView.init();
                break;
            default:
        }
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
            if(relevanceLiveGoodsShowView != null){
                relevanceLiveGoodsShowView.getFilter().filter(s);
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
        //new Thread(runnable).start();  //启动子线程
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(runnable);
        }
        UserMgr.getInstance().saveLiveGoodsInfo(list);
    }
}
