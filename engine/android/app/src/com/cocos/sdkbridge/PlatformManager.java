package com.cocos.sdkbridge;

import android.content.Context;
import android.util.Log;

import com.cocos.game.AppActivity;
import com.cocos.sdkbridge.impl.AlipayPlatformAdapter;
import com.cocos.sdkbridge.impl.AndroidPlatformAdapter;
import com.cocos.sdkbridge.impl.WechatPlatformAdapter;

import cocos.game.R;

public class PlatformManager {
    private static PlatformManager instance;
    private IPlatformAdapter currentAdapter;
    private Context context;
    private String channel;

    private static final String TAG = "PlatformManager";

    private PlatformManager(Context context) {
        this.context = context.getApplicationContext();
        loadChannelConfig();
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

    public IPlatformAdapter getCurrentAdapter() {
        if (currentAdapter == null) {
            createAdapterForCurrentChannel();
        }
        return currentAdapter;
    }

    // =============== 私有方法 ===============
    private void loadChannelConfig() {
        // 从配置文件读取当前渠道
        String channel = context.getResources().getString(R.string.channel_name);
        this.channel = channel;
        Log.i(TAG, "Current channel: " + channel);
    }

    private void createAdapterForCurrentChannel() {
        String channel = this.channel;
        // 根据渠道创建对应的适配器
        if ("wechat".equals(channel)) {
            currentAdapter = new WechatPlatformAdapter();
            Log.i(TAG, "WeChat platform adapter selected");
        } else if ("alipay".equals(channel)) {
            currentAdapter = new AlipayPlatformAdapter();
            Log.i(TAG, "Alipay platform adapter selected");
        } else if ("android".equals(channel)) {
            currentAdapter = new AndroidPlatformAdapter();
            Log.i(TAG, "android platform adapter selected");
        } else {
            Log.i(TAG, "platform adapter unselected");
        }

        if (currentAdapter != null) {
            currentAdapter.initialize();
        }
    }

}