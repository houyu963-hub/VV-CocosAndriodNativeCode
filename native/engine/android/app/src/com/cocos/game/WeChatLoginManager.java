package com.cocos.game;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WeChatLoginManager {
    private static final String TAG = "WeChatLoginManager";

    private IWXAPI api;
    private WeChatLoginCallback callback;

    public interface WeChatLoginCallback {
        void onSuccess(String code);

        void onError(String error);

        void onCancel();
    }

    public WeChatLoginManager(Context context) {
        String APP_ID = Config.wx_app_id;
        api = WXAPIFactory.createWXAPI(context, APP_ID, true);
        boolean registered = api.registerApp(APP_ID);
        Log.d(TAG, "WeChatLoginManager constructor - APP_ID: " + APP_ID);
        Log.d(TAG, "WeChatLoginManager constructor - registerApp result: " + registered);
        Log.d(TAG, "WeChatLoginManager constructor - isWXAppInstalled: " + api.isWXAppInstalled());
    }

    public boolean isWXAppInstalled() {
        return api.isWXAppInstalled();
    }

    public void login(Activity activity, WeChatLoginCallback callback) {
        Log.d(TAG, "WeChatLoginManager login called");
        this.callback = callback;

        if (!api.isWXAppInstalled()) {
            Log.e(TAG, "WeChatLoginManager login WX app is not installed");
            callback.onError("微信未安装");
            return;
        }

        Log.d(TAG, "WeChatLoginManager login WX app is installed, sending request");
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";

        boolean success = api.sendReq(req);
        Log.d(TAG, "WeChatLoginManager login sendReq result: " + success);
        if (!success) {
            Log.e(TAG, "WeChatLoginManager login sendReq failed");
            callback.onError("发送请求失败");
        }
    }

    public IWXAPI getApi() {
        return api;
    }
}