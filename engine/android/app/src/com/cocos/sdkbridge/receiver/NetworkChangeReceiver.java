package com.cocos.sdkbridge.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cocos.sdkbridge.PlatformBridge;

import org.json.JSONObject;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public PlatformBridge.PlatformCallback callback;

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = isNetworkAvailable(context);
        try {
            JSONObject data = new JSONObject();
            data.put("state", state);
            callback.onResult(0, "success", data);
        } catch (Exception e) {
            callback.onResult(-1, "failed", null);
        }
    }

    // 获取可用网络类型
    public String isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) { // WIFI
                    return "DO_WIFI";
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {// 移动数据
                    return "DO_MOBILE";
                }
            }
        }
        return "NO_CONNECTION";
    }

}