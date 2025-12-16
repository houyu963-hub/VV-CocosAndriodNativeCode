package com.cocos.sdkbridge.impl;

import com.cocos.game.AppActivity;
import com.cocos.sdkbridge.IPlatformAdapter;
import com.cocos.sdkbridge.PlatformBridge;

import org.json.JSONObject;

public class WechatPlatformAdapter implements IPlatformAdapter {
    private String PLATFORM_NAME = "WechatPlatformAdapter";
    private static AppActivity sActivity;

    @Override
    public void initialize() {
        this.sActivity = AppActivity.getInstance();
    }

    @Override
    public void login(PlatformBridge.PlatformCallback callback) {
        throw new Error("Method not implemented.");
    }

    @Override
    public void pay(JSONObject orderInfo, PlatformBridge.PlatformCallback callback) {
        throw new Error("Method not implemented.");
    }

    @Override
    public void share(JSONObject shareInfo, PlatformBridge.PlatformCallback callback) {
        throw new Error("Method not implemented.");
    }

    @Override
    public void exitApp() {
        throw new Error("Method not implemented.");
    }

    @Override
    public void closeSplash() {
        throw new Error("Method not implemented.");
    }

    @Override
    public void copyStr(String text, PlatformBridge.PlatformCallback callback) {
        throw new Error("Method not implemented.");
    }

    @Override
    public String getPlatformName() {
        return PLATFORM_NAME;
    }
}