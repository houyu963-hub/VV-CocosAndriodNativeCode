package com.cocos.sdkbridge;

import android.content.Context;

import androidx.annotation.Nullable;

import com.cocos.game.AppActivity;
import com.cocos.sdkbridge.impl.AlipayPlatformAdapter;
import com.cocos.sdkbridge.impl.AndroidPlatformAdapter;
import com.cocos.sdkbridge.impl.WechatPlatformAdapter;


public class PlatformManager {
    private static PlatformManager instance;

    private IPlatformAdapter alipayAdapter;
    private IPlatformAdapter wechatAdapter;
    private IPlatformAdapter androidAdapter;

    // sdk 平台常量定义
    public static final String PLATFORM_WECHAT = "wechat";
    public static final String PLATFORM_ALIPAY = "alipay";
    public static final String PLATFORM_ANDROID = "android";

    private static final String TAG = "PlatformManager";

    private PlatformManager(Context context) {
        wechatAdapter = new WechatPlatformAdapter();
        alipayAdapter = new AlipayPlatformAdapter();
        androidAdapter = new AndroidPlatformAdapter();
    }

    public static synchronized PlatformManager getInstance() {
        if (instance == null) {
            Context context = AppActivity.getInstance().getApplicationContext();
            if (context != null) {
                instance = new PlatformManager(context);
            }
        }
        return instance;
    }

    // 获取指定平台的适配器
    public IPlatformAdapter getAdapterByPlatform() {
        return androidAdapter;
    }

    public IPlatformAdapter getAdapterByPlatform(@Nullable String platform) {
        IPlatformAdapter adapter = null;
        switch (platform) {
            case PLATFORM_WECHAT:
                adapter = wechatAdapter;
                break;
            case PLATFORM_ALIPAY:
                adapter = alipayAdapter;
                break;
            case PLATFORM_ANDROID:
                adapter = androidAdapter;
                break;
        }
        return adapter;
    }

}