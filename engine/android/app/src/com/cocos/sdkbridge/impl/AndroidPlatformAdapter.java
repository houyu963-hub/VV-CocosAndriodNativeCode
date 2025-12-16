package com.cocos.sdkbridge.impl;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.cocos.game.AppActivity;
import com.cocos.sdkbridge.IPlatformAdapter;
import com.cocos.sdkbridge.PlatformBridge;
import com.cocos.sdkbridge.receiver.NetworkChangeReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class AndroidPlatformAdapter implements IPlatformAdapter {
    private String PLATFORM_NAME = "AndroidPlatformAdapter";
    private AppActivity sActivity;
    public Context sContext;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    public void initialize() {
        sActivity = AppActivity.getInstance();
        sContext = sActivity.getApplicationContext();

        // 注册网络改变监听
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        sActivity.registerReceiver(networkChangeReceiver, intentFilter);
        sActivity.networkChangeReceiver = networkChangeReceiver;
    }

    public void destroy() {
        if (networkChangeReceiver != null) {
            sActivity.unregisterReceiver(networkChangeReceiver);
        }
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
        sActivity.finish();
        System.exit(1);
    }

    @Override
    public void closeSplash() {
        sActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sActivity.sSplashBgImageView != null) {
                    sActivity.sSplashBgImageView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void copyStr(String text, PlatformBridge.PlatformCallback callback) {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                ClipboardManager clipboard = (ClipboardManager) sActivity.getSystemService(sActivity.getApplicationContext().CLIPBOARD_SERVICE);
                // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                ClipData clipData = ClipData.newPlainText(null, text);
                // 把数据集设置（复制）到剪贴板
                clipboard.setPrimaryClip(clipData);
                // 将文本内容放到系统剪贴板里。
                Toast.makeText(sActivity.getApplicationContext(), "复制成功!", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    @Override
    public void networkAvailable(PlatformBridge.PlatformCallback callback) {
        ConnectivityManager mgr = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    try {
                        JSONObject data = new JSONObject();
                        data.put("available", true);
                        callback.onResult(0, "Network available", data);
                    } catch (Exception e) {
                        callback.onResult(-1, "Network unAvailable", null);
                    }
                    return;
                }
            }
        }
        callback.onResult(-1, "Network unAvailabl", null);
    }

    @Override
    public void netCheck(PlatformBridge.PlatformCallback callback) {
        ConnectivityManager conMan = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        String text = "";
        if (wifi.equals(NetworkInfo.State.CONNECTED)) {
            text = "DO_WIFI";
        } else if (mobile.equals(NetworkInfo.State.CONNECTED)) {
            text = "DO_MOBILE";
        } else {
            text = "NO_CONNECTION";
        }
        try {
            JSONObject data = new JSONObject();
            data.put("state", text);
            callback.onResult(0, "Network available", data);
        } catch (Exception e) {
            callback.onResult(-1, "Network unAvailable", null);
        }
    }

    @Override
    public void sendMessage(JSONObject messageInfo, PlatformBridge.PlatformCallback callback) {
        sActivity.runOnUiThread(() -> {
            try {
                String phoneNumber = messageInfo.getString("phoneNumber");
                String smsContent = messageInfo.getString("smsContent");

                Uri uri = Uri.parse("smsto:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", smsContent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sActivity.startActivity(intent);

                callback.onResult(0, "SendMessage success", messageInfo);
            } catch (JSONException e) {
                callback.onResult(-1, "SendMessage failed", messageInfo);
            }
        });
    }

    @Override
    public void orientation(JSONObject orientationInfo, PlatformBridge.PlatformCallback callback) {
        sActivity.runOnUiThread(() -> {
            try {
                String orientation = orientationInfo.getString("orientation");
                if ("portrait".equals(orientation)) {
                    sActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    sActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                callback.onResult(0, "Orientation success", orientationInfo);
            } catch (JSONException e) {
                callback.onResult(-1, "Orientation failed", orientationInfo);
            }
        });
    }

    @Override
    public void vibrator() {
        new Thread(() -> {
            Vibrator vibrator = (Vibrator) sContext.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator.hasVibrator()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    VibrationEffect oneShot = VibrationEffect.createOneShot(25, 125);
                    vibrator.vibrate(oneShot);
                } else {
                    vibrator.vibrate(25);
                }
            }
        }).start();
    }

    @Override
    public void getDeviceId(PlatformBridge.PlatformCallback callback) {
        String androidId = Settings.Secure.getString(sContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            JSONObject data = new JSONObject();
            data.put("androidId", androidId);
            callback.onResult(0, "success", data);
        } catch (Exception e) {
            callback.onResult(-1, "failed", null);
        }
    }

    @Override
    public void getPackageName(PlatformBridge.PlatformCallback callback) {
        String packageName = sContext.getPackageName();
        try {
            JSONObject data = new JSONObject();
            data.put("packageName", packageName);
            callback.onResult(0, "success", data);
        } catch (Exception e) {
            callback.onResult(-1, "failed", null);
        }
    }

    @Override
    public void networkChangeReceiver(PlatformBridge.PlatformCallback callback) {
        networkChangeReceiver.callback = callback;
    }

    @Override
    public String getPlatformName() {
        return PLATFORM_NAME;
    }
}