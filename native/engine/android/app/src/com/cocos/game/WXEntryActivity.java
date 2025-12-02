package com.cocos.game;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cocos.game.R;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "WXEntryActivity onCreate");

        try {
            api = WXAPIFactory.createWXAPI(this, Config.wx_app_id, false);
            Log.d(TAG, "WXEntryActivity api created");

            boolean result = api.handleIntent(getIntent(), this);
            Log.d(TAG, "WXEntryActivity handleIntent result: " + result);

            if (!result) {
                Log.e(TAG, "WXEntryActivity handleIntent failed");
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "WXEntryActivity onCreate error: " + e.getMessage());
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void onReq(BaseReq req) {
        Log.d(TAG, "WXEntryActivity onReq: " + req.toString());
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "WXEntryActivity onResp: " + resp.toString());
        Log.d(TAG, "WXEntryActivity onResp errCode: " + resp.errCode);
        Log.d(TAG, "WXEntryActivity onResp errStr: " + resp.errStr);

        try {
            if (resp instanceof SendAuth.Resp) {
                Log.d(TAG, "WXEntryActivity onResp is SendAuth.Resp");
                SendAuth.Resp authResp = (SendAuth.Resp) resp;
                Log.d(TAG, "WXEntryActivity authResp code: " + authResp.code);
                Log.d(TAG, "WXEntryActivity authResp state: " + authResp.state);

                // 通知主Activity处理登录结果
                if (com.cocos.game.AppActivity.getInstance() != null) {
                    Log.d(TAG, "WXEntryActivity calling AppActivity.handleWeChatLoginResponse");
                    AppApi.handleWeChatLoginResponse(authResp);
                } else {
                    Log.e(TAG, "WXEntryActivity AppActivity.getInstance() is null");
                }
            } else {
                Log.d(TAG, "WXEntryActivity onResp is not SendAuth.Resp, type: " + resp.getClass().getSimpleName());
            }
        } catch (Exception e) {
            Log.e(TAG, "WXEntryActivity onResp error: " + e.getMessage());
            e.printStackTrace();
        }

        finish();
    }
}
