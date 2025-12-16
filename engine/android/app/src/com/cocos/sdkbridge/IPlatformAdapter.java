package com.cocos.sdkbridge;

import org.json.JSONObject;

public interface IPlatformAdapter {
    /**
     * 初始化SDK (可选)
     */
    void initialize();

    /**
     * 登录
     *
     * @param callback 登录结果回调
     */
    void login(PlatformBridge.PlatformCallback callback);

    /**
     * 支付
     *
     * @param orderInfo 订单信息 (JSON格式)
     * @param callback  支付结果回调
     */
    void pay(JSONObject orderInfo, PlatformBridge.PlatformCallback callback);

    /**
     * 分享
     *
     * @param shareInfo 分享信息 (JSON格式)
     * @param callback  分享结果回调
     */
    void share(JSONObject shareInfo, PlatformBridge.PlatformCallback callback);

    /**
     * 退出
     */
    void exitApp();

    /**
     * 关闭闪图
     */
    void closeSplash();

    /**
     * 复制
     *
     * @param text     要复制的文本
     * @param callback 复制结果回调
     */
    void copyStr(String text, PlatformBridge.PlatformCallback callback);

    /**
     * 检测网络可用
     */
    default void networkAvailable(PlatformBridge.PlatformCallback callback) {
        callback.onResult(-1, "Not supported", null);
    }

    /**
     * 检测网络可用类型
     */
    default void netCheck(PlatformBridge.PlatformCallback callback) {
        callback.onResult(-1, "Not supported", null);
    }

    /**
     * 调用系统短信
     */
    default void sendMessage(JSONObject messageInfo, PlatformBridge.PlatformCallback callback) {
        callback.onResult(-1, "Not supported", null);
    }

    /**
     * 设置屏幕方向
     */
    default void orientation(JSONObject orientationInfo, PlatformBridge.PlatformCallback callback) {
        callback.onResult(-1, "Not supported", null);
    }

    /**
     * 震动
     */
    default void vibrator() {

    }

    /**
     * 获取设备ID
     */
    default void getDeviceId(PlatformBridge.PlatformCallback callback) {
        callback.onResult(-1, "Not supported", null);
    }

    /**
     * 获取包名
     */
    default void getPackageName(PlatformBridge.PlatformCallback callback) {
        callback.onResult(-1, "Not supported", null);
    }

    /**
     * 获取平台名称
     */
    String getPlatformName();
}