package com.zk.baidumap;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * author: ZK.
 * date:   On 2017/11/28.
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现，也可以写在自定义Application的onCreat中
        SDKInitializer.initialize(getApplicationContext());
        DisplayUtils.init(this);

    }
}
