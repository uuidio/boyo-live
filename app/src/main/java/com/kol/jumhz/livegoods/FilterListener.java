package com.kol.jumhz.livegoods;

import com.kol.jumhz.common.livegoods.LiveGoodsBean;

import java.util.ArrayList;

/**
 * @Package: com.tencent.qcloud.jumhz.livegoods
 * @ClassName: FilterListener
 * @Description: 搜索商品接口
 * @Author: Lanlnk
 * @CreateDate: 2020/5/26 10:06
 */
public interface FilterListener {
    void getFilterData(ArrayList<LiveGoodsBean> list);// 获取过滤后的数据

}
