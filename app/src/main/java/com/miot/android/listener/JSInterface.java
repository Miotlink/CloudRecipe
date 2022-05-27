package com.miot.android.listener;

import android.webkit.JavascriptInterface;

public interface JSInterface {

    /**
     * 获取菜谱详情信息
     * @param value
     * @return
     */

    public String getCloudMenuInfos(String value);


    public String setCloudMenuCommands(String params);



}
