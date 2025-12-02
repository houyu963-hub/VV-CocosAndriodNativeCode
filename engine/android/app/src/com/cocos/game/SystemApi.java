package com.cocos.game;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class SystemApi {
    private static String TAG = "SystemApi";

    public static Context sContext;
    public static AppActivity app;

    // 微信登录相关
    private static WeChatLoginManager weChatLoginManager;

    public static void init(Context sContext, AppActivity app) {
        SystemApi.sContext = sContext;
        SystemApi.app = app;
        // 初始化微信登录管理器
        weChatLoginManager = new WeChatLoginManager(sContext);
    }

    // 复制剪切板
    public static void copyClipper(String str) {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                ClipboardManager clipboard = (ClipboardManager) app.getSystemService(sContext.CLIPBOARD_SERVICE);
                // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                ClipData clipData = ClipData.newPlainText(null, str);
                // 把数据集设置（复制）到剪贴板
                clipboard.setPrimaryClip(clipData);
                // 将文本内容放到系统剪贴板里。
                Toast.makeText(sContext, "复制成功!", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    // 网络方式检查
    private static String netCheck() {
        ConnectivityManager conMan = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (wifi.equals(NetworkInfo.State.CONNECTED)) {
            return "DO_WIFI";
        } else if (mobile.equals(NetworkInfo.State.CONNECTED)) {
            return "DO_3G";
        } else {
            return "NO_CONNECTION";
        }
    }

    // 网络是否可用
    public static boolean isNetworkAvailable() {
        ConnectivityManager mgr = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 调用系统发短信界面
     *
     * @param phoneNumber 手机号码
     * @param smsContent  短信内容
     */
    public static void sendMessage(String phoneNumber, String smsContent) {
        if (smsContent == null || phoneNumber.length() < 4) {
            return;
        }
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", smsContent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        app.startActivity(intent);
    }

    // 设置横屏
    public static void setOrientationLandscape() {
        app.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    // 设置竖屏
    public static void setOrientationPortrait() {
        app.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // 震动
    public static void vibrator() {
        // 获取 Vibrator 对象
        Vibrator vibrator = (Vibrator) sContext.getSystemService(Context.VIBRATOR_SERVICE);
        // 判断设备是否支持震动
        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                VibrationEffect oneShot = VibrationEffect.createOneShot(25, 125);
                vibrator.vibrate(oneShot);
            } else {
                // 触发震动，参数是震动的时长（单位：毫秒）
                vibrator.vibrate(25); // 1 秒钟的震动
            }
        }
    }

    // 跳转外部地址
    public static void skipUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    // 打开Email
    public static void openEmail(String data) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("mailto:"));
        String[] mails = new String[]{data};
        intent.putExtra(Intent.EXTRA_EMAIL, mails);
        startActivity(intent);
    }

    // 打开whatsApp
    public static void openWhatsApp(String data) {
        String url = "https://wa.me/" + data;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    // 打开Telegram
    public static void openTelegram(String data) {
        String url = "tg://resolve?domain=" + data;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private static void startActivity(Intent intent) {
        if (intent.resolveActivity(app.getPackageManager()) != null) {
            app.startActivity(intent);
        } else {
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    Toast.makeText(app, "App not installed.", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();
        }
    }

    // 退出app
    public static void exitGame() {
        app.finish();
        System.exit(1);
    }

    // 获取设备id
    private static String deviceId() {
        String androidId = Settings.Secure.getString(sContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    // 获取应用包名
    private static String packageName() {
        String packageName = sContext.getPackageName();
        return packageName;
    }

    /**
     * 处理微信回调
     */
    public static void handleWeChatCallback() {
        try {
            Intent intent = app.getIntent();
            if (intent != null && intent.getData() != null) {
                String scheme = intent.getData().getScheme();
                Log.d(TAG, "AppActivity handleWeChatCallback scheme: " + scheme);

                String APP_ID = Config.wx_app_id;
                if (APP_ID.equals(scheme)) {
                    Log.d(TAG, "AppActivity handleWeChatCallback handling wechat callback");

                    if (weChatLoginManager != null && weChatLoginManager.getApi() != null) {
                        boolean result = weChatLoginManager.getApi().handleIntent(intent, new IWXAPIEventHandler() {
                            @Override
                            public void onReq(BaseReq req) {
                                Log.d(TAG, "AppActivity handleWeChatCallback onReq: " + req.toString());
                            }

                            @Override
                            public void onResp(BaseResp resp) {
                                Log.d(TAG, "AppActivity handleWeChatCallback onResp: " + resp.toString());
                                if (resp instanceof SendAuth.Resp) {
                                    handleWeChatLoginResponse((SendAuth.Resp) resp);
                                }
                            }
                        });
                        Log.d(TAG, "AppActivity handleWeChatCallback handleIntent result: " + result);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "AppActivity handleWeChatCallback error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 微信登录
     */
    public static void wechatLogin() {
        Log.d(TAG, "AppActivity wechatLogin called");

        if (weChatLoginManager == null) {
            Log.e(TAG, "AppActivity wechatLogin weChatLoginManager is null");
            Toast.makeText(app, "微信登录管理器未初始化", Toast.LENGTH_SHORT).show();
            return;
        }

        if (weChatLoginManager.isWXAppInstalled()) {
            Log.d(TAG, "AppActivity wechatLogin WX app is installed");
            weChatLoginManager.login(app, new WeChatLoginManager.WeChatLoginCallback() {
                @Override
                public void onSuccess(String code) {
                    Log.d(TAG, "微信登录成功，code: " + code);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "微信登录失败: " + error);
                    Toast.makeText(app, "微信登录失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "用户取消微信登录");
                    Toast.makeText(app, "用户取消微信登录", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "AppActivity wechatLogin WX app is not installed");
            Toast.makeText(app, "微信未安装", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理微信登录响应（从WXEntryActivity调用）
     */
    public static void handleWeChatLoginResponse(SendAuth.Resp resp) {
        Log.d(TAG, "AppActivity handleWeChatLoginResponse called");
        Log.d(TAG, "AppActivity handleWeChatLoginResponse errCode: " + resp.errCode);
        Log.d(TAG, "AppActivity handleWeChatLoginResponse code: " + resp.code);
        Log.d(TAG, "AppActivity handleWeChatLoginResponse state: " + resp.state);

        String msg = "";
        JSONObject jsonObject = new JSONObject();
        switch (resp.errCode) {
            case SendAuth.Resp.ErrCode.ERR_OK:
                Log.d(TAG, "微信授权成功，code: " + resp.code);
                Toast.makeText(sContext, "微信授权成功", Toast.LENGTH_SHORT).show();
                msg = "微信授权成功";
                break;
            case SendAuth.Resp.ErrCode.ERR_USER_CANCEL:
                Log.d(TAG, "用户取消微信授权");
                Toast.makeText(sContext, "用户取消微信授权", Toast.LENGTH_SHORT).show();
                msg = "用户取消微信授权";
                break;
            case SendAuth.Resp.ErrCode.ERR_AUTH_DENIED:
                Log.e(TAG, "用户拒绝微信授权");
                Toast.makeText(sContext, "用户拒绝微信授权", Toast.LENGTH_SHORT).show();
                msg = "用户拒绝微信授权";
                break;
            default:
                Log.e(TAG, "微信授权失败，errCode: " + resp.errCode);
                Toast.makeText(sContext, "微信授权失败", Toast.LENGTH_SHORT).show();
                msg = "微信授权失败";
                break;
        }
        try {
            if (resp.errCode == SendAuth.Resp.ErrCode.ERR_OK) {
                jsonObject.put("code", resp.code);
                jsonObject.put("msg", msg);
            } else {
                jsonObject.put("code", -1);
                jsonObject.put("msg", "用户取消微信授权");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        app.sendResultToCocos(100, jsonObject);
    }
}
