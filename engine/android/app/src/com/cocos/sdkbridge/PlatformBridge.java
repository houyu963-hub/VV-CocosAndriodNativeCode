package com.cocos.sdkbridge;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.cocos.game.AppActivity;
import com.cocos.lib.CocosHelper;
import com.cocos.lib.CocosJavascriptJavaBridge;

public class PlatformBridge {
    private static final String TAG = "PlatformBridge";

    // 登录
    public static void login(final String callbackId, final String params) {
        Log.d(TAG, "login called, callbackId: " + callbackId);
        runOnUiThread(() -> {
            try {
                IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
                adapter.login((resultCode, message, data) ->
                        sendResultToJs(callbackId, resultCode, message, data)
                );
            } catch (Exception e) {
                Log.e(TAG, "Login error", e);
                sendResultToJs(callbackId, -1, "Login failed: " + e.getMessage(), null);
            }
        });
    }

    // 支付
    public static void pay(final String callbackId, final String params) {
        Log.d(TAG, "pay called, params: " + params);
        runOnUiThread(() -> {
            try {
                JSONObject orderInfo = new JSONObject(params);
                IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
                adapter.pay(orderInfo, (resultCode, message, data) ->
                        sendResultToJs(callbackId, resultCode, message, data)
                );
            } catch (JSONException e) {
                Log.e(TAG, "Invalid pay params", e);
                sendResultToJs(callbackId, -1, "Invalid order info", null);
            } catch (Exception e) {
                Log.e(TAG, "Pay error", e);
                sendResultToJs(callbackId, -1, "Pay failed: " + e.getMessage(), null);
            }
        });
    }

    // 分享
    public static void share(final String callbackId, final String params) {
        Log.d(TAG, "share called, params: " + params);
        runOnUiThread(() -> {
            try {
                JSONObject shareInfo = new JSONObject(params);
                IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
                adapter.share(shareInfo, (resultCode, message, data) ->
                        sendResultToJs(callbackId, resultCode, message, data)
                );
            } catch (JSONException e) {
                Log.e(TAG, "Invalid share params", e);
                sendResultToJs(callbackId, -1, "Invalid share info", null);
            } catch (Exception e) {
                Log.e(TAG, "Share error", e);
                sendResultToJs(callbackId, -1, "Share failed: " + e.getMessage(), null);
            }
        });
    }

    // 退出
    public static void exitApp(final String callbackId, final String params) {
        Log.d(TAG, "exitApp called");
        runOnUiThread(() -> {
            try {
                IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
                adapter.exitApp();
            } catch (Exception e) {
                Log.e(TAG, "Exit app error", e);
            }
        });
    }

    // 关闭闪图
    public static void closeSplash(final String callbackId, final String params) {
        Log.d(TAG, "closeSplash called, params: " + params);
        runOnUiThread(() -> {
            try {
                IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
                adapter.closeSplash();
            } catch (Exception e) {
                Log.e(TAG, "closeSplash app error", e);
            }
        });
    }

    // 复制
    public static void copyStr(final String callbackId, final String params) {
        Log.d(TAG, "copyStr called, params: " + params);
        runOnUiThread(() -> {
            try {
                IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
                adapter.copyStr(params, (resultCode, message, data) ->
                        sendResultToJs(callbackId, resultCode, message, data)
                );
            } catch (Exception e) {
                Log.e(TAG, "Copy string error", e);
                sendResultToJs(callbackId, -1, "Copy failed: " + e.getMessage(), null);
            }
        });
    }

    // 检测网络可用
    public static void netCheck(final String callbackId, final String params) {
        Log.d(TAG, "netCheck called");
        runOnUiThread(() -> {
            try {
                IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
                adapter.netCheck((resultCode, message, data) ->
                        sendResultToJs(callbackId, resultCode, message, data)
                );
            } catch (Exception e) {
                Log.e(TAG, "Net check error", e);
                sendResultToJs(callbackId, -1, "Net check failed: " + e.getMessage(), null);
            }
        });
    }

    // 检测网络可用类型
    public static void networkAvailable(final String callbackId, final String params) {
        Log.d(TAG, "networkAvailable called");
        runOnUiThread(() -> {
            try {
                IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
                adapter.networkAvailable((resultCode, message, data) ->
                        sendResultToJs(callbackId, resultCode, message, data)
                );
            } catch (Exception e) {
                Log.e(TAG, "Network available check error", e);
                sendResultToJs(callbackId, -1, "Network check failed: " + e.getMessage(), null);
            }
        });
    }

    // 调用系统短信
    public static void sendMessage(final String callbackId, final String params) {
        Log.d(TAG, "copyStr called, params: " + params);
        runOnUiThread(() -> {
            try {
                JSONObject messageInfo = new JSONObject(params);
                IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
                adapter.sendMessage(messageInfo, new PlatformCallback() {
                    @Override
                    public void onResult(int resultCode, String message, JSONObject data) {
                        sendResultToJs(callbackId, resultCode, message, data);
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "Invalid share params", e);
                sendResultToJs(callbackId, -1, "Invalid share info", null);
            }
        });
    }

    // 设置屏幕方向
    public static void orientation(final String callbackId, final String params) {
        Log.d(TAG, "orientation called, params: " + params);
        runOnUiThread(() -> {
            try {
                JSONObject orientationInfo = new JSONObject(params);
                IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
                adapter.orientation(orientationInfo, new PlatformCallback() {
                    @Override
                    public void onResult(int resultCode, String message, JSONObject data) {
                        sendResultToJs(callbackId, resultCode, message, data);
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "Invalid share params", e);
                sendResultToJs(callbackId, -1, "Invalid share info", null);
            }
        });
    }

    // 震动 - 不需要回调
    public static void vibrator(final String callbackId, final String params) {
        Log.d(TAG, "vibrator called");
        runOnUiThread(() -> {
            try {
                IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
                adapter.vibrator();
            } catch (Exception e) {
                Log.e(TAG, "Vibrator error", e);
            }
        });
    }

    // 获取设备ID
    public static void getDeviceId(final String callbackId, final String params) {
        runOnUiThread(() -> {
            IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
            adapter.getDeviceId(new PlatformCallback() {
                @Override
                public void onResult(int resultCode, String message, JSONObject data) {
                    sendResultToJs(callbackId, resultCode, message, data);
                }
            });
        });
    }

    // 获取应用包名
    public static void getPackageName(final String callbackId, final String params) {
        runOnUiThread(() -> {
            IPlatformAdapter adapter = PlatformManager.getInstance().getCurrentAdapter();
            adapter.getPackageName(new PlatformCallback() {
                @Override
                public void onResult(int resultCode, String message, JSONObject data) {
                    sendResultToJs(callbackId, resultCode, message, data);
                }
            });
        });
    }


    // =============== 工具方法 ===============
    private static void runOnUiThread(Runnable runnable) {
        Activity activity = AppActivity.getInstance();
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(runnable);
        } else {
            Log.w(TAG, "Activity is not available or finishing");
        }
    }

    private static void sendResultToJs(String callbackId, int resultCode, String message, JSONObject data) {
        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject result = new JSONObject();

                    // 转换状态码
                    String code;
                    switch (resultCode) {
                        case 0:
                            code = "SUCCESS";
                            break;
                        case 1:
                            code = "CANCELLED";
                            break;
                        default:
                            code = "FAILED";
                            break;
                    }

                    result.put("code", code);
                    result.put("message", message != null ? message : "");
                    if (data != null) {
                        result.put("data", data);
                    }

                    String jsCode = String.format(
                            "window.__nativePlatformCallback && window.__nativePlatformCallback('%s', '%s')",
                            callbackId,
                            result.toString().replace("\\", "\\\\").replace("'", "\\'")
                    );

                    CocosJavascriptJavaBridge.evalString(jsCode);
                    Log.d(TAG, "Callback sent to JS: " + result.toString());
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to create JSON result", e);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to send result to JS", e);
                }
            }
        });
    }

    /**
     * 回调接口
     * resultCode 0 成功, -1 失败
     * message 提示信息
     * data 回调数据
     */
    public interface PlatformCallback {
        void onResult(int resultCode, String message, JSONObject data);
    }
}